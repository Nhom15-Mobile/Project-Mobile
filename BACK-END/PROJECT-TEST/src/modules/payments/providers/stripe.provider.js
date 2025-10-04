// src/modules/payments/providers/stripe.provider.js
const config = require('../../../config/env');
const Stripe = require('stripe');


function stripeClient() {
if (!config.stripe.secret) throw new Error('Stripe secret not configured');
return new Stripe(config.stripe.secret);
}


module.exports = { stripeClient };