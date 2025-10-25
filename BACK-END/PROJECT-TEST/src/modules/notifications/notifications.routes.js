// src/modules/notifications/notifications.routes.js
const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./notifications.controller');

// tất cả route dưới đây yêu cầu đăng nhập
router.use(auth, allow('PATIENT','DOCTOR','ADMIN'));

// cho app (chuông thông báo)
router.get('/', ctrl.listMine);
router.get('/unread-count', ctrl.unreadCount);
router.post('/:id/read', ctrl.markRead);
router.post('/read-all', ctrl.markAllRead);
router.delete('/:id', ctrl.remove);

// demo/manual (tuỳ chọn)
router.post('/appointments/:id/notify', allow('ADMIN','DOCTOR'), ctrl.notifyAppointmentChange);

// cron reminder (ADMIN gọi định kỳ)
router.post('/send-reminders', allow('ADMIN'), ctrl.sendReminders);

module.exports = router;
