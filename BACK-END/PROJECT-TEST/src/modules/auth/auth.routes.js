// src/modules/auth/auth.routes.js
const router = require('express').Router();
const ctrl = require('./auth.controller');
const validate = require('../../middlewares/validate');
const v = require('./auth.validation');
router.post('/login', ctrl.login);
router.post('/register', ctrl.register); // 👈 thêm dòng này

router.post('/forgot', ctrl.forgotPassword);
router.post('/reset', ctrl.resetPassword);
module.exports = router;