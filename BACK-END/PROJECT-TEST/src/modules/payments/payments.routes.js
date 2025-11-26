const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./payments.controller');
const validate = require('../../middlewares/validate');
const v = require('./payments.validation');

// FAKE payment
router.post('/fake/create', auth, allow('PATIENT','ADMIN'), ctrl.fakeCreate);
router.post('/fake/confirm', auth, allow('PATIENT','ADMIN','DOCTOR'), ctrl.fakeConfirm);

// tạo MoMo payment cho appointment
router.post('/momo/create', auth, allow('PATIENT'), ctrl.momoCreate);

// IPN từ MoMo (public)
router.post('/momo/notify', ctrl.momoNotify);

// phiếu khám
router.get('/receipt/:id', auth, allow('PATIENT','DOCTOR','ADMIN'), ctrl.receipt);

module.exports = router;
