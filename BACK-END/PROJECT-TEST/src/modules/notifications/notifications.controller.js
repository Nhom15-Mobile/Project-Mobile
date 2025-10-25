// src/modules/notifications/notifications.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./notifications.service');
const emitter = require('./notifications.emitter');

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

// (giữ nguyên) manual trigger demo
async function notifyAppointmentChange(req, res) {
  const { id } = req.params;
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

/**
 * SSE stream: FE mở kết nối để nhận realtime
 * - YÊU CẦU: đã đăng nhập (auth middleware)
 */
async function stream(req, res) {
  // headers SSE
  res.setHeader('Content-Type', 'text/event-stream');
  res.setHeader('Cache-Control', 'no-cache, no-transform');
  res.setHeader('Connection', 'keep-alive');
  res.flushHeaders?.();

  const userId = req.user.id;
  const send = (event, payload) => {
    res.write(`event: ${event}\n`);
    res.write(`data: ${JSON.stringify(payload)}\n\n`);
  };

  // Gửi ping mỗi 25s để giữ kết nối sống (tránh proxy đóng)
  const ping = setInterval(() => send('ping', { t: Date.now() }), 25000);

  // Lắng nghe emitter theo userId
  const onNew    = (n) => send('notification', n);
  const onRead   = (n) => send('read', n);
  const onReadAll= (p) => send('read-all', p);
  const onDelete = (p) => send('deleted', p);

  emitter.on(`notify:${userId}`, onNew);
  emitter.on(`notify:${userId}:read`, onRead);
  emitter.on(`notify:${userId}:read-all`, onReadAll);
  emitter.on(`notify:${userId}:deleted`, onDelete);

  // cleanup
  req.on('close', () => {
    clearInterval(ping);
    emitter.off(`notify:${userId}`, onNew);
    emitter.off(`notify:${userId}:read`, onRead);
    emitter.off(`notify:${userId}:read-all`, onReadAll);
    emitter.off(`notify:${userId}:deleted`, onDelete);
    res.end();
  });
}

module.exports = {
  listMine,
  unreadCount,
  markRead,
  markAllRead,
  remove,
  notifyAppointmentChange,
  stream,
};
