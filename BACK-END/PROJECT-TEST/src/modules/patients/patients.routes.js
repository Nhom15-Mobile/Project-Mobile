const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./patients.controller');

router.get('/profile', auth, allow('PATIENT'), ctrl.getProfile);
router.post('/profile', auth, allow('PATIENT'), ctrl.updateProfile);

// tất cả appointments của patient
router.get('/appointments', auth, allow('PATIENT'), ctrl.listAppointments);

// chỉ appointments đã thanh toán
router.get('/appointments/paid', auth, allow('PATIENT'), ctrl.listPaidAppointments);

// nhắc lịch: các appointment CONFIRMED + PAID, sắp tới trong withinMinutes
router.get(
  '/appointments/upcoming-reminders',
  auth,
  allow('PATIENT'),
  ctrl.listUpcomingReminders
);

// kết quả khám (exam result) cho 1 appointment
// >>> NEW: các appointment đã có kết quả khám <<<
router.get(
  '/appointments/results',
  auth,
  allow('PATIENT'),
  ctrl.listAppointmentResults
);

module.exports = router;
