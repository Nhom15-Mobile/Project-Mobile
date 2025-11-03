// src/modules/notifications/notifications.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./notifications.service');

async function listMine(req, res) {
  const { cursor, limit, unreadOnly } = req.query;
  const data = await svc.list(req.user.id, {
    cursor: cursor || undefined,
    limit: limit ? Number(limit) : 20,
    unreadOnly: unreadOnly === '1' || unreadOnly === 'true'
  });
  return R.ok(res, data);
}

async function unreadCount(req, res) {
  const data = await svc.unreadCount(req.user.id);
  return R.ok(res, data);
}

async function markRead(req, res) {
  const data = await svc.markRead(req.user.id, req.params.id);
  return R.ok(res, data, 'Marked as read');
}

async function markAllRead(req, res) {
  const data = await svc.markAllRead(req.user.id);
  return R.ok(res, data, 'All marked as read');
}

async function remove(req, res) {
  const data = await svc.remove(req.user.id, req.params.id);
  return R.ok(res, data, 'Deleted');
}

// Manual trigger (optional demo)
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

// Endpoint cron: gửi reminder cho các lịch sắp diễn ra
async function sendReminders(req, res) {
  const { hoursAhead = 24, minHours = 3 } = req.body || {};
  const sent = await svc.sendUpcomingReminders({ hoursAhead, minHours });
  return R.ok(res, { sent }, 'Reminders sent');
}

module.exports = {
  listMine,
  unreadCount,
  markRead,
  markAllRead,
  remove,
  notifyAppointmentChange,
  sendReminders,
};
