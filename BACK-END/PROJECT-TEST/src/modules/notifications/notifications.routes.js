// src/modules/notifications/notifications.routes.js
const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./notifications.controller');


router.get('/', auth, allow('PATIENT','DOCTOR','ADMIN'), ctrl.listMine);
router.post('/appointments/:id/notify', auth, allow('ADMIN','DOCTOR'), ctrl.notifyAppointmentChange);


module.exports = router;