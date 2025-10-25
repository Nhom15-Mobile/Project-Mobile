// src/modules/notifications/notifications.service.js
const prisma = require('../../config/db');
const emitter = require('./notifications.emitter');

async function list(userId, { cursor, limit = 20, unreadOnly } = {}) {
  const where = {
    userId,
    ...(unreadOnly ? { readAt: null } : {})
  };
  const items = await prisma.notification.findMany({
    where,
    orderBy: { createdAt: 'desc' },
    ...(cursor ? { skip: 1, cursor: { id: cursor } } : {}),
    take: Number(limit) || 20
  });
  // cursor-based
  const nextCursor = items.length ? items[items.length - 1].id : null;
  return { items, nextCursor };
}

async function unreadCount(userId) {
  const count = await prisma.notification.count({ where: { userId, readAt: null } });
  return { count };
}

async function create({ userId, type, title, body, data }) {
  const n = await prisma.notification.create({ data: { userId, type, title, body, data } });
  // phát sự kiện realtime (SSE) cho user đó
  emitter.emit(`notify:${userId}`, n);
  return n;
}

async function markRead(userId, id) {
  const n = await prisma.notification.findUnique({ where: { id } });
  if (!n || n.userId !== userId) throw new Error('Not found');
  const updated = await prisma.notification.update({ where: { id }, data: { readAt: new Date() } });
  emitter.emit(`notify:${userId}:read`, updated);
  return updated;
}

async function markAllRead(userId) {
  const res = await prisma.notification.updateMany({
    where: { userId, readAt: null },
    data: { readAt: new Date() }
  });
  emitter.emit(`notify:${userId}:read-all`, { count: res.count });
  return { updated: res.count };
}

async function remove(userId, id) {
  const n = await prisma.notification.findUnique({ where: { id } });
  if (!n || n.userId !== userId) throw new Error('Not found');
  await prisma.notification.delete({ where: { id } });
  emitter.emit(`notify:${userId}:deleted`, { id });
  return { ok: true };
}

/** (các helper đặt lịch bạn đã có—giữ nguyên hoặc dùng tiếp) */
async function notifyBooked({ patientId, doctorId, appointment }) { /* ... như trước ... */ }
async function notifyCancelled({ patientId, doctorId, appointment, reason }) { /* ... như trước ... */ }
async function notifyReminder({ userId, appointment, forWhom }) { /* ... như trước ... */ }

/** Hàm cron nhắc lịch (nếu bạn đã gộp ở notifications) — giữ nguyên */
async function sendUpcomingReminders(args) { /* ... như trước ... */ }

module.exports = {
  list,
  unreadCount,
  create,
  markRead,
  markAllRead,
  remove,
  notifyBooked,
  notifyCancelled,
  notifyReminder,
  sendUpcomingReminders,
};