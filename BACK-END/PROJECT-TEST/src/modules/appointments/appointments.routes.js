// src/modules/appointments/appointments.routes.js
const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./appointments.controller');


router.get('/available', auth, allow('PATIENT','DOCTOR','ADMIN'), ctrl.available);
router.post('/book', auth, allow('PATIENT'), ctrl.book);
router.post('/:id/cancel', auth, allow('PATIENT'), ctrl.cancel);


module.exports = router;