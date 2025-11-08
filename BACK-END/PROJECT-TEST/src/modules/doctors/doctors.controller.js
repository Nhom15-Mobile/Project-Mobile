// src/modules/doctors/doctors.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./doctors.service');
const apptSvc = require('../appointments/appointments.service');
const { validateUpdateProfile } = require('./doctors.validation');

// format giờ local VN để FE hiển thị
const tz = 'Asia/Ho_Chi_Minh';
const fmt = new Intl.DateTimeFormat('sv-SE', {
  timeZone: tz,
  dateStyle: 'short',
  timeStyle: 'short'
});
const withLocal = (slot) => ({
  ...slot,
  localStart: fmt.format(new Date(slot.start)),
  localEnd: fmt.format(new Date(slot.end))
});

// ===== Public =====
async function search(req, res) {
  try {
    const result = await svc.search(req.query);
    return R.ok(res, result);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function getOne(req, res) {
  try {
    // :id = userId (DoctorProfile.userId)
    const item = await svc.getById(req.params.id);
    if (!item) return R.notFound(res, 'Doctor not found');
    return R.ok(res, item);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

/**
 * Danh sách bác sĩ còn slot trong 1 ngày
 * Query:
 *  - day (YYYY-MM-DD) required
 *  - specialty (optional)
 *  - q (optional)           → filter theo fullName
 *  - minRating (optional)   → filter >=
 *  - slotsPerDoctor (opt)   → số slot trả về mỗi bác sĩ (mặc định 3)
 *  - view=slots + doctorUserId → trả slot của 1 bác sĩ cụ thể (kiểu cũ)
 */
async function available(req, res) {
  try {
    const { day, specialty, q, minRating, slotsPerDoctor, view, doctorUserId } = req.query;
    if (!day) return R.badRequest(res, 'day (YYYY-MM-DD) is required');

    // --- nếu view=slots → giữ behavior cũ ---
    if (view === 'slots') {
      if (!doctorUserId) return R.badRequest(res, 'doctorUserId is required when view=slots');
      const slots = await apptSvc.availableSlots(doctorUserId, day);
      return R.ok(res, slots);
    }

    // --- default: danh sách bác sĩ còn slot ---
    const doctors = await svc.availableDoctors({
      dayISO: day,
      specialty,
      slotsPerDoctor: slotsPerDoctor ? Number(slotsPerDoctor) : 3
    });

    // thêm local time format
    const mapped = doctors.map(d => ({
      ...d,
      firstSlots: d.firstSlots?.map(s => ({
        ...s,
        localStart: fmt.format(new Date(s.start)),
        localEnd: fmt.format(new Date(s.end))
      })) || []
    }));

    return R.ok(res, mapped);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function specialties(req, res) {
  try {
    const data = await svc.listSpecialties();
    return R.ok(res, data);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

// ===== Doctor self profile =====
async function myProfile(req, res) {
  try {
    const dp = await svc.getProfileByUserId(req.user.id);
    if (!dp) return R.notFound(res, 'Doctor profile not found');
    return R.ok(res, dp);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function updateMyProfile(req, res) {
  try {
    const { error, data } = validateUpdateProfile(req.body);
    if (error) return R.badRequest(res, error);
    const profile = await svc.updateProfile(req.user.id, data); // userId = DoctorProfile.userId
    return R.ok(res, profile, 'Profile updated');
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

// ===== Workday =====
async function setWorkDayBlocks(req, res) {
  try {
    const dp = await svc.getProfileByUserId(req.user.id);
    if (!dp) return R.notFound(res, 'Doctor profile not found');

    const { day, blocks, replaceUnbooked } = req.body;
    if (!day || !Array.isArray(blocks) || !blocks.length)
      return R.badRequest(res, 'day & blocks are required');

    const result = await svc.setWorkDayBlocks({
      doctorProfileId: dp.userId, // theo schema
      day,
      blocks,
      replaceUnbooked: !!replaceUnbooked
    });

    return R.created(res, {
      created: result.created,
      slots: result.slots.map(withLocal)
    }, 'Workday saved');
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function myWorkDay(req, res) {
  try {
    const dp = await svc.getProfileByUserId(req.user.id);
    if (!dp) return R.notFound(res, 'Doctor profile not found');

    const { day } = req.query;
    if (!day) return R.badRequest(res, 'day is required (YYYY-MM-DD)');

    const slots = await svc.getWorkDay({ doctorProfileId: dp.userId, day });
    return R.ok(res, slots.map(withLocal));
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

module.exports = {
  search,
  getOne,
  available,
  specialties,
  myProfile,
  updateMyProfile,
  setWorkDayBlocks,
  myWorkDay
};
