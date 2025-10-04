// src/modules/notifications/notifications.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./notifications.service');


async function listMine(req, res) {
const items = await svc.list(req.user.id);
return R.ok(res, items);
}


// Manual trigger for demo (e.g., admin cancellations)
async function notifyAppointmentChange(req, res) {
const { id } = req.params; // appointment id
const { toUserId, reason, status } = req.body;
await svc.create({
userId: toUserId,
type: 'APPOINTMENT_UPDATED',
title: `Lịch khám cập nhật: ${status || 'Thay đổi'}`,
body: reason || 'Lịch khám của bạn đã thay đổi.',
data: { appointmentId: id, status, reason }
});
return R.ok(res, {}, 'Notification sent');
}


module.exports = { listMine, notifyAppointmentChange };