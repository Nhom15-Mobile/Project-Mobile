// src/modules/payments/payments.service.js
const prisma = require('../../config/db');
const { stripeClient } = require('./providers/stripe.provider');


async function createPayment({ appointmentId, amount, currency = 'VND', provider = 'stripe' }) {
const appt = await prisma.appointment.findUnique({ where: { id: appointmentId }, include: { payment: true } });
if (!appt) throw new Error('Appointment not found');
if (appt.paymentStatus === 'PAID') throw new Error('Already paid');


if (provider === 'stripe') {
const stripe = stripeClient();
const pi = await stripe.paymentIntents.create({ amount, currency: currency.toLowerCase(), metadata: { appointmentId } });


const payment = await prisma.payment.upsert({
where: { appointmentId },
create: { appointmentId, amount, currency, provider, providerRef: pi.id },
update: { amount, currency, provider, providerRef: pi.id }
});


await prisma.appointment.update({ where: { id: appointmentId }, data: { paymentStatus: 'REQUIRES_PAYMENT', paymentId: payment.id } });


return { clientSecret: pi.client_secret, paymentId: payment.id };
}


// VNPay / MoMo: return a payUrl you redirect user to (stubbed)
throw new Error('Unsupported provider in demo');
}


module.exports = { createPayment };