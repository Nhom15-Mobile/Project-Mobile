const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./appointments.controller');

// sau khi login, cả PATIENT/DOCTOR/ADMIN đều có thể xem slot available
router.get('/available', auth, allow('PATIENT','DOCTOR','ADMIN'), ctrl.available);

// patient đặt / hủy
router.post('/book', auth, allow('PATIENT'), ctrl.book);
router.post('/:id/cancel', auth, allow('PATIENT'), ctrl.cancel);

// calendar public (để FE dựng lịch trước khi chọn bác sĩ)
router.get('/calendar', ctrl.calendar);

module.exports = router;
