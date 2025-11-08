const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./doctors.controller');

// thứ tự quan trọng: routes cụ thể trước, :id sau
router.get('/specialties', ctrl.specialties);
router.get('/available', ctrl.available);

// self profile
router.get('/me/profile',  auth, allow('DOCTOR','ADMIN'), ctrl.myProfile);
router.patch('/me/profile', auth, allow('DOCTOR','ADMIN'), ctrl.updateMyProfile);

// workday
router.post('/workday/blocks', auth, allow('DOCTOR','ADMIN'), ctrl.setWorkDayBlocks);
router.get('/workday',        auth, allow('DOCTOR','ADMIN'), ctrl.myWorkDay);

// public browse
router.get('/', ctrl.search);
router.get('/:id', ctrl.getOne); // :id là userId của bác sĩ
router.get('/available', ctrl.available);
module.exports = router;