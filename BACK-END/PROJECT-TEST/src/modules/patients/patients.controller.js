const R = require('../../utils/apiResponse');
const svc = require('./patients.service');
const { validateUpsertProfile } = require('./patients.validation');

async function getProfile(req, res) {
  try {
    const profile = await svc.getProfile(req.user.id);
    return R.ok(res, profile || {});
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function updateProfile(req, res) {
  try {
    const { error, data } = validateUpsertProfile(req.body);
    if (error) return R.badRequest(res, error);

    const profile = await svc.upsertProfile(req.user.id, data);
    return R.ok(res, profile, 'Profile saved');
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function listAppointments(req, res) {
  try {
    const items = await svc.getAppointments(req.user.id);
    return R.ok(res, items);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

// CHỈ trả về các lịch đã thanh toán
async function listPaidAppointments(req, res) {
  try {
    const items = await svc.getPaidAppointments(req.user.id);
    return R.ok(res, items);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

// nhắc lịch trong withinMinutes (default 5)
async function listUpcomingReminders(req, res) {
  try {
    const withinMinutes = req.query.withinMinutes || '5';
    const items = await svc.getUpcomingAppointmentReminders(
      req.user.id,
      withinMinutes
    );
    return R.ok(res, items);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

// Lấy kết quả khám (exam result) cho 1 lịch của chính patient
async function getAppointmentResult(req, res) {
  try {
    const { id } = req.params;
    const data = await svc.getAppointmentResult(req.user.id, id);
    return R.ok(res, data);
  } catch (e) {
    console.error(e);
    if (e.message === 'Appointment not found') {
      return R.notFound(res, e.message);
    }
    if (e.message === 'Forbidden') {
      return R.forbidden(res);
    }
    return R.badRequest(res, e.message || 'Bad request');
  }
}
// ========== LIST CÁC APPOINTMENT ĐÃ CÓ KẾT QUẢ KHÁM ==========
async function listAppointmentResults(req, res) {
  try {
    const items = await svc.getAppointmentResults(req.user.id);
    return R.ok(res, items);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}
module.exports = {
  getProfile,
  updateProfile,
  listAppointments,
  listPaidAppointments,
  listUpcomingReminders,
  getAppointmentResult,
  listAppointmentResults,
};
