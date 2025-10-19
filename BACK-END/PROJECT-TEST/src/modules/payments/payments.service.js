// src/modules/payments/payments.service.js
const axios = require('axios');
const crypto = require('crypto');
const prisma = require('../../config/db');
const config = require('../../config/env');

// --- helpers ---
function signRaw(raw, secretKey) {
  return crypto.createHmac('sha256', secretKey).update(raw).digest('hex');
}

function getFeeBySpecialty(specialty) {
  const map = config.fees?.specialtyFees || {};
  const v = Number(map[specialty]) || Number(config.fees?.defaultSpecialtyFee || 150000);
  return Math.max(1, Math.floor(v)); // int >= 1
}

function ensureMomoConfig() {
  const reqKeys = ['partnerCode', 'accessKey', 'secretKey', 'endpoint', 'returnUrl', 'notifyUrl'];
  const missing = reqKeys.filter(k => !config.momo?.[k]);
  if (missing.length) {
    throw new Error(`MoMo config missing: ${missing.join(', ')}`);
  }
}

// --- MoMo create for an appointment ---
async function createMoMoForAppointment({ appointmentId, byUserId }) {
  // 0) config guard
  ensureMomoConfig();

  // 1) load appointment + quyền sở hữu
  const appt = await prisma.appointment.findUnique({
    where: { id: appointmentId },
    include: { patient: true, doctor: true, careProfile: true, slot: true }
  });
  if (!appt) throw new Error('Appointment not found');
  if (appt.patientId !== byUserId) throw new Error('Forbidden');
  if (appt.status === 'CANCELLED') throw new Error('Appointment cancelled');

  // 2) fee theo specialty của bác sĩ (doctorId là User.id)
  const dp = await prisma.doctorProfile.findUnique({ where: { userId: appt.doctorId } });
  const specialty = dp?.specialty || 'GENERAL';
  const amount = getFeeBySpecialty(specialty);

  // 3) chuẩn bị Payment record
  const provider = 'MOMO';
  const currency = 'VND';
  const orderId = `APPT_${appointmentId}`;     // duy nhất theo appt
  const requestId = `${orderId}_${Date.now()}`; // duy nhất theo request

  await prisma.payment.upsert({
    where: { appointmentId },
    update: { provider, amount, currency, status: 'REQUIRES_PAYMENT' },
    create: { appointmentId, provider, amount, currency, status: 'REQUIRES_PAYMENT' }
  });

  // 4) call MoMo create
  const endpoint = `${config.momo.endpoint.replace(/\/+$/,'')}/create`;
  const orderInfo = `Thanh toán đặt khám - ${specialty}`;
  const redirectUrl = config.momo.returnUrl;
  const ipnUrl = config.momo.notifyUrl;
  const requestType = 'captureWallet';

  // raw signature (MoMo v2)
  const raw = `accessKey=${config.momo.accessKey}`
    + `&amount=${amount}`
    + `&extraData=`
    + `&ipnUrl=${ipnUrl}`
    + `&orderId=${orderId}`
    + `&orderInfo=${orderInfo}`
    + `&partnerCode=${config.momo.partnerCode}`
    + `&redirectUrl=${redirectUrl}`
    + `&requestId=${requestId}`
    + `&requestType=${requestType}`;

  const signature = signRaw(raw, config.momo.secretKey);

  const payload = {
    partnerCode: config.momo.partnerCode,
    accessKey:   config.momo.accessKey,
    requestId,
    amount:      String(amount),
    orderId,
    orderInfo,
    redirectUrl,
    ipnUrl,
    requestType,
    extraData: '',
    lang: 'vi',
    signature
  };

  try {
    const { data } = await axios.post(endpoint, payload, { timeout: 10000 });

    // 5) lưu providerRef / meta
    await prisma.payment.update({
      where: { appointmentId },
      data: { providerRef: data.transId ? String(data.transId) : null, meta: data }
    });

    // giữ trạng thái chờ thanh toán
    await prisma.appointment.update({
      where: { id: appointmentId },
      data: { paymentStatus: 'REQUIRES_PAYMENT' }
    });

    return {
      amount,
      orderInfo,
      payUrl: data.payUrl,
      qrCodeUrl: data.qrCodeUrl || null
    };
  } catch (err) {
    // surface MoMo error rõ ràng
    if (err.response) {
      const { status, data } = err.response;
      const msg = (data && (data.message || data.localMessage)) || JSON.stringify(data);
      throw new Error(`MoMo ${status}: ${msg}`);
    }
    throw err;
  }
}

// --- verify MoMo IPN signature ---
function verifyMomoSignature(params) {
  const {
    partnerCode, orderId, requestId, amount, orderInfo, orderType,
    transId, resultCode, message, payType, responseTime, extraData, signature
  } = params;

  const raw = `accessKey=${config.momo.accessKey}`
    + `&amount=${amount}`
    + `&extraData=${extraData}`
    + `&message=${message}`
    + `&orderId=${orderId}`
    + `&orderInfo=${orderInfo}`
    + `&orderType=${orderType}`
    + `&partnerCode=${partnerCode}`
    + `&payType=${payType}`
    + `&requestId=${requestId}`
    + `&responseTime=${responseTime}`
    + `&resultCode=${resultCode}`
    + `&transId=${transId}`;

  const sign = signRaw(raw, config.momo.secretKey);
  return sign === signature;
}

async function handleMomoIPN(body) {
  if (!verifyMomoSignature(body)) return { ok: false, code: 97, msg: 'Signature mismatch' };

  const { orderId, resultCode } = body; // APPT_<appointmentId>
  if (!orderId || !orderId.startsWith('APPT_')) return { ok: false, code: 98, msg: 'Invalid orderId' };
  const appointmentId = orderId.substring('APPT_'.length);

  const paid = Number(resultCode) === 0;

  await prisma.$transaction(async (tx) => {
    await tx.payment.updateMany({
      where: { appointmentId },
      data: { status: paid ? 'PAID' : 'FAILED', meta: body }
    });
    await tx.appointment.update({
      where: { id: appointmentId },
      data: {
        paymentStatus: paid ? 'PAID' : 'FAILED',
        status: paid ? 'CONFIRMED' : 'PENDING'
      }
    });
  });

  return { ok: true, code: 0, msg: 'OK' };
}

module.exports = { createMoMoForAppointment, handleMomoIPN };