// src/modules/appointments/appointments.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./appointments.service');

async function available(req, res) {
  const { doctorId, date } = req.query;
  if (!doctorId) return R.badRequest(res, 'doctorId is required');
  const slots = await svc.availableSlots(doctorId, date);
  return R.ok(res, slots);
}

async function book(req, res) {
  const { doctorProfileId, slotId, service } = req.body;
  if (!doctorProfileId || !slotId || !service) {
    return R.badRequest(res, 'doctorProfileId, slotId, service are required');
  }
  const appt = await svc.book({
    patientId: req.user.id,
    doctorProfileId,
    slotId,
    service
  });
  return R.created(res, appt, 'Appointment created, proceed to payment');
}

async function cancel(req, res) {
  const { id } = req.params;
  const { reason } = req.body;
  const appt = await svc.cancel({
    appointmentId: id,
    byUserId: req.user.id,
    reason
  });
  return R.ok(res, appt, 'Appointment cancelled');
}

module.exports = { available, book, cancel };