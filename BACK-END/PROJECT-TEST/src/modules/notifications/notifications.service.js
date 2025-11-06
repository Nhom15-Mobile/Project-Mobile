// src/modules/notifications/notifications.service.js
const prisma = require('../../config/db');

// ======================= CRUD cơ bản =======================
async function list(userId, { cursor, limit = 20, unreadOnly } = {}) {
  const where = { userId, ...(unreadOnly ? { readAt: null } : {}) };
  const items = await prisma.notification.findMany({
    where,
    orderBy: { createdAt: 'desc' },
    ...(cursor ? { skip: 1, cursor: { id: cursor } } : {}),
    take: Number(limit) || 20
  });
  const nextCursor = items.length ? items[items.length - 1].id : null;
  return { items, nextCursor };
}

async function unreadCount(userId) {
  const count = await prisma.notification.count({ where: { userId, readAt: null } });
  return { count };
}

async function create({ userId, type, title, body, data }) {
  return prisma.notification.create({ data: { userId, type, title, body, data } });
}

async function markRead(userId, id) {
  const n = await prisma.notification.findUnique({ where: { id } });
  if (!n || n.userId !== userId) throw new Error('Not found');
  return prisma.notification.update({ where: { id }, data: { readAt: new Date() } });
}

async function markAllRead(userId) {
  const res = await prisma.notification.updateMany({
    where: { userId, readAt: null },
    data: { readAt: new Date() }
  });
  return { updated: res.count };
}

async function remove(userId, id) {
  const n = await prisma.notification.findUnique({ where: { id } });
  if (!n || n.userId !== userId) throw new Error('Not found');
  await prisma.notification.delete({ where: { id } });
  return { ok: true };
}

// ======================= Helpers nội dung =======================
function fmtDate(dt) {
  // hiển thị yyyy-mm-dd hh:mm theo giờ VN cho dễ đọc
  const tz = 'Asia/Ho_Chi_Minh';
  const fmt = new Intl.DateTimeFormat('sv-SE', { timeZone: tz, dateStyle: 'short', timeStyle: 'short' });
  return fmt.format(new Date(dt));
}

// 1) Khi thanh toán thành công (IPN MoMo → gọi từ payments.service)
async function notifyBooked({ patientId, doctorId, appointment }) {
  const when = fmtDate(appointment.scheduledAt);

  // Thông báo cho PATIENT
  await create({
    userId: patientId,
    type: 'PAYMENT_SUCCESS',
    title: 'Thanh toán thành công',
    body: `Bạn đã thanh toán thành công lịch khám ${appointment.service} lúc ${when}.`,
    data: {
      appointmentId: appointment.id,
      scheduledAt: appointment.scheduledAt,
      service: appointment.service,
    }
  });

  // Thông báo cho DOCTOR
  await create({
    userId: doctorId,
    type: 'APPOINTMENT_NEW',
    title: 'Có lịch khám mới',
    body: `Bạn có lịch khám mới: ${appointment.patientName || 'Người bệnh'} - ${appointment.service} lúc ${when}.`,
    data: {
      appointmentId: appointment.id,
      scheduledAt: appointment.scheduledAt,
      service: appointment.service,
      patientName: appointment.patientName || null
    }
  });
}

// 2) Khi hủy lịch ( gọi từ appointments.service.cancel )
async function notifyCancelled({ patientId, doctorId, appointment, reason }) {
  const when = fmtDate(appointment.scheduledAt);

  await create({
    userId: patientId,
    type: 'APPOINTMENT_CANCELLED',
    title: 'Đã hủy lịch khám',
    body: `Bạn đã hủy lịch khám ${appointment.service} lúc ${when}. Lý do: ${reason || 'Không rõ'}.`,
    data: {
      appointmentId: appointment.id,
      scheduledAt: appointment.scheduledAt,
      reason: reason || null
    }
  });

  await create({
    userId: doctorId,
    type: 'APPOINTMENT_CANCELLED',
    title: 'Lịch khám bị hủy',
    body: `Lịch khám của ${appointment.patientName || 'người bệnh'} lúc ${when} đã bị hủy. Lý do: ${reason || 'Không rõ'}.`,
    data: {
      appointmentId: appointment.id,
      scheduledAt: appointment.scheduledAt,
      reason: reason || null,
      patientName: appointment.patientName || null
    }
  });
}

// 3) Reminder (gần lịch khám)
async function notifyReminder({ userId, appointment, forWhom }) {
  const when = fmtDate(appointment.scheduledAt);
  const title = forWhom === 'DOCTOR' ? 'Nhắc lịch khám sắp diễn ra' : 'Nhắc lịch khám';
  const body  = forWhom === 'DOCTOR'
    ? `Bạn sắp có lịch khám với ${appointment.patientName || 'người bệnh'} vào ${when}.`
    : `Bạn có lịch khám ${appointment.service} vào ${when}.`;

  await create({
    userId,
    type: 'APPOINTMENT_REMINDER',
    title,
    body,
    data: {
      appointmentId: appointment.id,
      scheduledAt: appointment.scheduledAt,
      service: appointment.service,
      patientName: appointment.patientName || null,
      doctorName: appointment.doctorName || null
    }
  });
}

// 4) Cron job – gửi reminder (gọi qua endpoint /send-reminders)
function addHours(d, h) { const x = new Date(d); x.setHours(x.getHours() + h); return x; }

async function sendUpcomingReminders({ hoursAhead = 24, minHours = 3 } = {}) {
  const now = new Date();
  const from = addHours(now, minHours);   // >= 3h nữa
  const to   = addHours(now, hoursAhead); // <= 24h tới

  const appts = await prisma.appointment.findMany({
    where: {
      status: 'CONFIRMED',
      paymentStatus: 'PAID',
      scheduledAt: { gte: from, lte: to },
    },
    include: {
      patient: { select: { id: true, fullName: true } },
      doctor:  { select: { id: true, fullName: true } },
    }
  });

  let sent = 0;
  const since = addHours(now, -36);

  for (const a of appts) {
    // chống bắn trùng: nếu 36h gần đây user đã có nhắc lịch thì bỏ
    const [recentPatient, recentDoctor] = await Promise.all([
      prisma.notification.findFirst({
        where: { userId: a.patientId, type: 'APPOINTMENT_REMINDER', createdAt: { gte: since } },
        orderBy: { createdAt: 'desc' }
      }),
      prisma.notification.findFirst({
        where: { userId: a.doctorId, type: 'APPOINTMENT_REMINDER', createdAt: { gte: since } },
        orderBy: { createdAt: 'desc' }
      })
    ]);

    if (!recentPatient) {
      await notifyReminder({
        userId: a.patientId,
        forWhom: 'PATIENT',
        appointment: {
          id: a.id,
          scheduledAt: a.scheduledAt,
          service: a.service,
          doctorName: a.doctor?.fullName || null
        }
      });
      sent++;
    }

    if (!recentDoctor) {
      await notifyReminder({
        userId: a.doctorId,
        forWhom: 'DOCTOR',
        appointment: {
          id: a.id,
          scheduledAt: a.scheduledAt,
          service: a.service,
          patientName: a.patient?.fullName || null
        }
      });
      sent++;
    }
  }

  return sent;
}

module.exports = {
  // CRUD cho app
  list,
  unreadCount,
  create,
  markRead,
  markAllRead,
  remove,

  // helper cho flow thanh toán/đặt lịch
  notifyBooked,
  notifyCancelled,
  notifyReminder,

  // cron reminders
  sendUpcomingReminders,
};
