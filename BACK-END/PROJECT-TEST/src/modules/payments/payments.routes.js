// src/modules/payments/payments.routes.js
const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./payments.controller');


router.post('/create', auth, allow('PATIENT'), ctrl.create);


// Export webhook handler separately for raw body binding in app.js
module.exports = router;
module.exports.stripeWebhook = ctrl.stripeWebhook;