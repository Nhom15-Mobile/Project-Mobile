// src/modules/payments/payments.service.js
const axios = require('axios');
const crypto = require('crypto');
const prisma = require('../../config/db');
const config = require('../../config/env');
const { notifyBooked } = require('../notifications/notifications.service');
const { generateAppointmentQR } = require('../../utils/qr');
const QRCode = require('qrcode'); // ← THÊM DÒNG NÀY

// --- helpers ---
function signRaw(raw, secretKey) {
  return crypto.createHmac('sha256', secretKey).update(raw).digest('hex');
}

/**
 * Lấy phí khám theo chuyên khoa:
 * - Ưu tiên đọc từ config.specialties: [{ name, fee }]
 * - Fallback sang config.fees.specialtyFees hoặc fees.defaultSpecialtyFee (giữ tương thích dự án cũ)
 */
function getFeeBySpecialty(specialty) {
  const name = String(specialty || '').trim();
  // 1) specialties (mới)
  if (Array.isArray(config.specialties) && config.specialties.length) {
    const found = config.specialties.find(
      s => String(s.name).trim().toLowerCase() === name.toLowerCase()
    );
    if (found && Number(found.fee)) return Math.max(1, Math.floor(Number(found.fee)));
  }
  // 2) fees (cũ)
  const map = config.fees?.specialtyFees || {};
  const v = Number(map[name]) || Number(config.fees?.defaultSpecialtyFee || 150000);
  return Math.max(1, Math.floor(v)); // int >= 1
}

function ensureMomoConfig() {
  const reqKeys = ['partnerCode', 'accessKey', 'secretKey', 'endpoint', 'returnUrl', 'notifyUrl'];
  const missing = reqKeys.filter(k => !config.momo?.[k]);
  if (missing.length) throw new Error(`MoMo config missing: ${missing.join(', ')}`);
}

function trimTrailingSlash(u = '') {
  return String(u || '').replace(/\/+$/, '');
}

// --- MoMo create for an appointment ---
async function createMoMoForAppointment({ appointmentId, byUserId }) {
  // 0) config guard
  ensureMomoConfig();

  // 1) load appointment + quyền sở hữu
  const appt = await prisma.appointment.findUnique({
    where: { id: appointmentId },
    include: {
      patient: { select: { id: true, fullName: true } },
      doctor:  { select: { id: true, fullName: true } },
      careProfile: true,
      slot: true
    }
  });
  if (!appt) throw new Error('Appointment not found');
  if (appt.patientId !== byUserId) throw new Error('Forbidden');
  if (appt.status === 'CANCELLED') throw new Error('Appointment cancelled');
  if (appt.paymentStatus === 'PAID') throw new Error('Appointment already paid');

  // 2) fee theo specialty của bác sĩ (doctorId là User.id)
  const dp = await prisma.doctorProfile.findUnique({ where: { userId: appt.doctorId } });
  const specialty = dp?.specialty || 'GENERAL';
  const amount = getFeeBySpecialty(specialty);

  // 3) chuẩn bị Payment record
  const provider = 'MOMO';
  const currency = 'VND';
  // base để encode appointmentId
  const orderIdBase = `APPT_${appointmentId}`;
  // thêm suffix cho mỗi lần gọi → orderId luôn khác nhau
  const orderId = `${orderIdBase}_${Date.now()}`;
  const requestId = `${orderId}_${Math.floor(Math.random() * 1000)}`;

  await prisma.payment.upsert({
    where: { appointmentId },
    update: { provider, amount, currency, status: 'REQUIRES_PAYMENT' },
    create: { appointmentId, provider, amount, currency, status: 'REQUIRES_PAYMENT' }
  });

  // 4) call MoMo create
  const endpoint = `${trimTrailingSlash(config.momo.endpoint)}/create`;
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
      data: {
        providerRef: data.transId ? String(data.transId) : null,
        meta: data
      }
    });

    // giữ trạng thái chờ thanh toán
    await prisma.appointment.update({
      where: { id: appointmentId },
      data: { paymentStatus: 'REQUIRES_PAYMENT' }
    });

    // 6) Generate QR base64 từ payUrl (cho app hiển thị trực tiếp)
    let qrImage = null;
    if (data.payUrl) {
      try {
        qrImage = await QRCode.toDataURL(data.payUrl); // data:image/png;base64,...
      } catch (e) {
        console.error('QR generate error:', e);
      }
    }

    return {
      amount,
      orderInfo,
      payUrl: data.payUrl || null,
      qrImage, // ← BASE64 QR CHO MOBILE
      qrCodeUrl: data.qrCodeUrl || null,
      deeplink: data.deeplink || data.deeplinkWebInApp || null
    };
  } catch (err) {
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
    + `&amount=${amount ?? ''}`
    + `&extraData=${extraData ?? ''}`
    + `&message=${message ?? ''}`
    + `&orderId=${orderId ?? ''}`
    + `&orderInfo=${orderInfo ?? ''}`
    + `&orderType=${orderType ?? ''}`
    + `&partnerCode=${partnerCode ?? ''}`
    + `&payType=${payType ?? ''}`
    + `&requestId=${requestId ?? ''}`
    + `&responseTime=${responseTime ?? ''}`
    + `&resultCode=${resultCode ?? ''}`
    + `&transId=${transId ?? ''}`;

  const sign = signRaw(raw, config.momo.secretKey);
  return sign === signature;
}

async function handleMomoIPN(body) {
  if (!verifyMomoSignature(body)) return { ok: false, code: 97, msg: 'Signature mismatch' };

  const { orderId, resultCode } = body; // APPT_<appointmentId>_...
  if (!orderId || !orderId.startsWith('APPT_')) {
    return { ok: false, code: 98, msg: 'Invalid orderId' };
  }

  // orderId = APPT_<appointmentId>_<suffix>
  const core = orderId.substring('APPT_'.length); // "<appointmentId>_<suffix>"
  const [appointmentId] = core.split('_');        // phần trước _ đầu tiên

  // Lấy trạng thái trước update để chặn bắn notify trùng khi IPN bị gọi lại
  const apptBefore = await prisma.appointment.findUnique({
    where: { id: appointmentId },
    include: {
      patient: { select: { id: true, fullName: true } },
      doctor:  { select: { id: true, fullName: true } },
    }
  });
  if (!apptBefore) return { ok: false, code: 99, msg: 'Appointment not found' };

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

  // Chỉ gửi thông báo khi từ trạng thái CHƯA-PAID -> PAID
  if (paid && apptBefore.paymentStatus !== 'PAID') {
    await notifyBooked({
      patientId: apptBefore.patientId,
      doctorId:  apptBefore.doctorId,
      appointment: {
        id: appointmentId,
        service: apptBefore.service,
        scheduledAt: apptBefore.scheduledAt,
        patientName: apptBefore.patient?.fullName || 'Người bệnh',
        doctorName:  apptBefore.doctor?.fullName  || 'Bác sĩ'
      }
    });
  }

  return { ok: true, code: 0, msg: 'OK' };
}

// ================== FAKE PAYMENT (KHÔNG QUA MOMO) ==================

/**
 * Tạo "QR FAKE" cho một appointment:
 * - Không gọi MoMo, chỉ tạo Payment provider = 'FAKE'
 * - Generate QR = dataURL chứa JSON { appointmentId, amount, ... }
 */
async function createFakePayment({ appointmentId, byUserId }) {
  // 1) load appointment + check quyền
  const appt = await prisma.appointment.findUnique({
    where: { id: appointmentId },
    include: {
      patient: { select: { id: true, fullName: true } },
      doctor:  { select: { id: true, fullName: true } },
      slot: true,
    },
  });
  if (!appt) throw new Error('Appointment not found');
  if (appt.patientId !== byUserId) throw new Error('Forbidden');
  if (appt.status === 'CANCELLED') throw new Error('Appointment cancelled');
  if (appt.paymentStatus === 'PAID') throw new Error('Appointment already paid');

  // 2) tính phí theo specialty (tận dụng hàm có sẵn)
  const dp = await prisma.doctorProfile.findUnique({ where: { userId: appt.doctorId } });
  const specialty = dp?.specialty || 'GENERAL';
  const amount = getFeeBySpecialty(specialty);

  // 3) upsert Payment provider = 'FAKE'
  const provider = 'FAKE';
  const currency = 'VND';

  await prisma.payment.upsert({
    where: { appointmentId },
    update: { provider, amount, currency, status: 'REQUIRES_PAYMENT' },
    create: { appointmentId, provider, amount, currency, status: 'REQUIRES_PAYMENT' }
  });

  // 4) cập nhật trạng thái appointment -> chờ thanh toán
  await prisma.appointment.update({
    where: { id: appointmentId },
    data: { paymentStatus: 'REQUIRES_PAYMENT' }
  });

  // 5) generate QR nội bộ
  const qrPayload = {
    appointmentId,
    amount,
    patientName: appt.patient?.fullName || null,
    doctorName: appt.doctor?.fullName || null,
    scheduledAt: appt.scheduledAt,
    fake: true,
  };
  const qrCode = await generateAppointmentQR(qrPayload); // data:image/png;base64,...

  return {
    amount,
    specialty,
    qrCode, // FE chỉ cần <img :src="qrCode" />
  };
}

/**
 * Xác nhận "fake payment":
 * - Đặt appointment = PAID + CONFIRMED
 * - Payment.status = PAID
 * - Gọi notifyBooked để bắn thông báo
 */
async function confirmFakePayment({ appointmentId, byUserId }) {
  const apptBefore = await prisma.appointment.findUnique({
    where: { id: appointmentId },
    include: {
      patient: { select: { id: true, fullName: true } },
      doctor:  { select: { id: true, fullName: true } },
      payment: true,
    }
  });
  if (!apptBefore) throw new Error('Appointment not found');

  // Optional: chỉ cho bệnh nhân hoặc admin/doctor confirm
  // if (apptBefore.patientId !== byUserId) throw new Error('Forbidden');

  // nếu đã PAID rồi thì thôi
  if (apptBefore.paymentStatus === 'PAID') {
    return { alreadyPaid: true };
  }

  await prisma.$transaction(async (tx) => {
    await tx.payment.updateMany({
      where: { appointmentId },
      data: { status: 'PAID' }
    });
    await tx.appointment.update({
      where: { id: appointmentId },
      data: {
        paymentStatus: 'PAID',
        status: 'CONFIRMED'
      }
    });
  });

  // bắn notification giống MoMo IPN (handleMomoIPN)
  await notifyBooked({
    patientId: apptBefore.patientId,
    doctorId:  apptBefore.doctorId,
    appointment: {
      id: appointmentId,
      service: apptBefore.service,
      scheduledAt: apptBefore.scheduledAt,
      patientName: apptBefore.patient?.fullName || 'Người bệnh',
      doctorName:  apptBefore.doctor?.fullName  || 'Bác sĩ'
    }
  });

  return { ok: true };
}

module.exports = {
  createMoMoForAppointment,
  handleMomoIPN,
  createFakePayment,
  confirmFakePayment
};
