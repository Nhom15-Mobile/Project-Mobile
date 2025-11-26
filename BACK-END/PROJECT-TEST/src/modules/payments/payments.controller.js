const R = require('../../utils/apiResponse');
const svc = require('./payments.service');
const prisma = require('../../config/db');



async function momoCreate(req, res) {
  const { appointmentId } = req.body;
  if (!appointmentId) return R.badRequest(res, 'appointmentId is required');
  const data = await svc.createMoMoForAppointment({ appointmentId, byUserId: req.user.id });
  return R.ok(res, data);
}

// IPN (public, không auth)
async function momoNotify(req, res) {
  try {
    const result = await svc.handleMomoIPN(req.body);
    return res.json({ resultCode: result.code, message: result.msg });
  } catch (e) {
    return res.status(200).json({ resultCode: 99, message: e.message || 'Error' });
  }
}

// (tuỳ chọn) nếu bạn giữ API phiếu khám:
async function receipt(req, res) {
  const { id } = req.params; // appointment id
  const appt = await prisma.appointment.findUnique({
    where: { id },
    include: { patient: true, doctor: true, slot: true, careProfile: true, payment: true }
  });
  if (!appt) return R.notFound(res, 'Appointment not found');
  if (req.user.role === 'PATIENT' && appt.patientId !== req.user.id) return R.forbidden(res);
  if (appt.paymentStatus !== 'PAID') return R.badRequest(res, 'Payment not completed');

  const dp = await prisma.doctorProfile.findUnique({ where: { userId: appt.doctorId } });
  const specialty = dp?.specialty || 'GENERAL';

  return R.ok(res, {
    receiptNo: appt.payment?.id || appt.id,
    patientName: appt.careProfile?.fullName || appt.patient.fullName,
    specialty,
    examDate: appt.slot?.start,
    examTime: appt.slot ? { start: appt.slot.start, end: appt.slot.end } : null,
    clinicRoom: dp?.clinicName || 'Phòng khám',
    amount: appt.payment?.amount,
    bookedAt: appt.createdAt
  });
}
async function fakeCreate(req, res) {
  const { appointmentId } = req.body;
  if (!appointmentId) return R.badRequest(res, 'appointmentId is required');

  try {
    const data = await svc.createFakePayment({
      appointmentId,
      byUserId: req.user.id,
    });
    return R.ok(res, data, 'Fake payment QR created');
  } catch (e) {
    console.error(e);
    return R.error(res, e.message || 'Failed to create fake payment');
  }
}

async function fakeConfirm(req, res) {
  const { appointmentId } = req.body;
  if (!appointmentId) return R.badRequest(res, 'appointmentId is required');

  try {
    const data = await svc.confirmFakePayment({
      appointmentId,
      byUserId: req.user.id,
    });
    return R.ok(res, data, 'Fake payment confirmed');
  } catch (e) {
    console.error(e);
    return R.error(res, e.message || 'Failed to confirm fake payment');
  }
}

module.exports = { momoCreate, momoNotify, receipt, fakeCreate, fakeConfirm };