// src/modules/auth/auth.routes.js
const router = require('express').Router();
const ctrl = require('./auth.controller');
const auth = require('../../middlewares/auth');

router.post('/login', ctrl.login);
router.post('/register', ctrl.register);

router.post('/forgot', ctrl.forgotPassword);
router.post('/reset', ctrl.resetPassword);

router.get('/me', auth, ctrl.me);

module.exports = router;
