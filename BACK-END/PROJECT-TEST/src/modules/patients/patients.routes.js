// src/modules/patients/patients.routes.js
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

module.exports = router;
