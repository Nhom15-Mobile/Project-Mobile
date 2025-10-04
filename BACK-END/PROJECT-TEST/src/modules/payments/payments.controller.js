// src/modules/payments/payments.controller.js
const R = require('../../utils/apiResponse');
const prisma = require('../../config/db');
const config = require('../../config/env');
const { stripeClient } = require('./providers/stripe.provider');
const { generateAppointmentQR } = require('../../utils/qr');


async function create(req, res) {
const { appointmentId, amount, currency, provider } = req.body;
if (!appointmentId || !amount) return R.badRequest(res, 'appointmentId and amount are required');
const svc = require('./payments.service');
const result = await svc.createPayment({ appointmentId, amount, currency, provider });
return R.ok(res, result, 'Payment created');
}


// Stripe webhook (demo): set endpoint in Stripe dashboard
async function stripeWebhook(req, res) {
const stripe = stripeClient();
const sig = req.headers['stripe-signature'];
let event;
try {
event = require('stripe').webhooks.constructEvent(req.rawBody, sig, config.stripe.webhookSecret);
} catch (err) {
return res.status(400).send(`Webhook Error: ${err.message}`);
}


if (event.type === 'payment_intent.succeeded') {
const pi = event.data.object;
const appointmentId = pi.metadata.appointmentId;


// Mark payment/appointment, generate QR
const appt = await prisma.appointment.update({
where: { id: appointmentId },
data: { status: 'CONFIRMED', paymentStatus: 'PAID' },
include: { patient: true, doctor: true }
});


const qr = await generateAppointmentQR({
appointmentId: appt.id,
patientName: appt.patient.fullName,
doctorName: appt.doctor.fullName,
scheduledAt: appt.scheduledAt
});


await prisma.appointment.update({ where: { id: appt.id }, data: { qrCode: qr } });


// Create a notification to patient
await prisma.notification.create({
data: {
userId: appt.patientId,
type: 'APPOINTMENT_CONFIRMED',
title: 'Lịch khám đã xác nhận',
body: 'Thanh toán thành công. Lịch khám của bạn đã được xác nhận.',
data: { appointmentId: appt.id }
}
});
}


res.json({ received: true });
}


module.exports = { create, stripeWebhook };