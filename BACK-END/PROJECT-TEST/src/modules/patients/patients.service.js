const prisma = require('../../config/db');
const UNPAID_EXPIRE_MINUTES = 10;
const REMINDER_WINDOW_DEFAULT_MINUTES = 5;

async function getProfile(userId) {
  return prisma.patientProfile.findUnique({
    where: { userId },
    include: { user: true },
  });
}

async function upsertProfile(userId, payload) {
  return prisma.patientProfile.upsert({
    where: { userId },
    update: payload,
    create: { userId, ...payload },
  });
}

/// cleanup các lịch PENDING + REQUIRES_PAYMENT đã quá hạn của 1 patient
async function cleanupExpiredForPatient(patientId) {
  const cutoff = new Date(Date.now() - UNPAID_EXPIRE_MINUTES * 60 * 1000);

  const expired = await prisma.appointment.findMany({
    where: {
      patientId,
      status: 'PENDING',
      paymentStatus: 'REQUIRES_PAYMENT',
      createdAt: { lt: cutoff },
    },
    select: {
      id: true,
      slotId: true,
    },
  });

  for (const appt of expired) {
    try {
      await prisma.$transaction(async (tx) => {
        // nhả slot nếu có
        if (appt.slotId) {
          await tx.doctorSlot.update({
            where: { id: appt.slotId },
            data: { isBooked: false },
          });
        }

        // xoá payment liên quan
        await tx.payment.deleteMany({
          where: { appointmentId: appt.id },
        });

        // xoá appointment
        await tx.appointment.delete({
          where: { id: appt.id },
        });
      });
    } catch (e) {
      console.error('cleanupExpiredForPatient error', appt.id, e);
    }
  }
}

async function getAppointments(userId) {
  await cleanupExpiredForPatient(userId);
  return prisma.appointment.findMany({
    where: { patientId: userId },
    include: { doctor: true, payment: true, careProfile: true },
    orderBy: { scheduledAt: 'desc' },
  });
}

// CHỈ appointment đã thanh toán, mới nhất lên trước
async function getPaidAppointments(userId) {
  return prisma.appointment.findMany({
    where: {
      patientId: userId,
      paymentStatus: 'PAID',
    },
    include: {
      doctor: true,
      payment: true,
      careProfile: true,
    },
    // sort theo thời gian cập nhật (thường là lúc thanh toán xong)
    orderBy: { updatedAt: 'desc' },
  });
}

// ========== UPCOMING REMINDERS (nhắc lịch trước X phút) ==========
async function getUpcomingAppointmentReminders(
  patientId,
  withinMinutes = REMINDER_WINDOW_DEFAULT_MINUTES
) {
  // giới hạn trong khoảng 1..60 phút cho safe
  const windowMinutes = Math.min(
    Math.max(parseInt(withinMinutes, 10) || REMINDER_WINDOW_DEFAULT_MINUTES, 1),
    60
  );

  const now = new Date();
  const to = new Date(now.getTime() + windowMinutes * 60 * 1000);

  const items = await prisma.appointment.findMany({
    where: {
      patientId,
      // logic nhắc lịch: thường là lịch đã CONFIRMED + PAID
      status: 'CONFIRMED',
      paymentStatus: 'PAID',
      scheduledAt: {
        gte: now,
        lte: to,
      },
    },
    include: {
      doctor: true,
      careProfile: true,
      payment: true,
    },
    orderBy: { scheduledAt: 'asc' },
  });

  // map ra JSON gọn cho bên app
  return items.map((appt) => {
    const diffMs = appt.scheduledAt.getTime() - now.getTime();
    const diffMinutes = Math.max(Math.floor(diffMs / 60000), 0);

    return {
      id: appt.id,
      service: appt.service,
      scheduledAt: appt.scheduledAt, // ra ISO string khi JSON
      timeUntilStartMinutes: diffMinutes,
      status: appt.status,
      paymentStatus: appt.paymentStatus,
      doctor: appt.doctor
        ? {
            id: appt.doctor.id,
            fullName: appt.doctor.fullName,
            email: appt.doctor.email,
            phone: appt.doctor.phone,
          }
        : null,
      careProfile: appt.careProfile
        ? {
            id: appt.careProfile.id,
            fullName: appt.careProfile.fullName,
            relation: appt.careProfile.relation,
          }
        : null,
      payment: appt.payment
        ? {
            id: appt.payment.id,
            amount: appt.payment.amount,
            currency: appt.payment.currency,
            status: appt.payment.status,
          }
        : null,
    };
  });
}

// ========== LIST CÁC APPOINTMENT ĐÃ CÓ KẾT QUẢ KHÁM ==========
async function getAppointmentResults(patientId) {
  const appts = await prisma.appointment.findMany({
    where: {
      patientId,
      examResult: { not: null }, // chỉ lấy lịch có kết quả
    },
    include: {
      patient: {
        select: { id: true, fullName: true, email: true, phone: true },
      },
      doctor: {
        select: { id: true, fullName: true, email: true, phone: true },
      },
      careProfile: {
        select: { id: true, fullName: true, relation: true },
      },
    },
    orderBy: { scheduledAt: 'desc' },
  });

  // map gọn cho app dùng
  return appts.map((a) => ({
    id: a.id,
    service: a.service,
    examResult: a.examResult,
    examDate: a.scheduledAt,

    status: a.status,
    paymentStatus: a.paymentStatus,

    patient: a.patient
      ? {
          id: a.patient.id,
          fullName: a.patient.fullName, // tên user (owner)
          email: a.patient.email,
          phone: a.patient.phone,
        }
      : null,

    careProfile: a.careProfile
      ? {
          id: a.careProfile.id,
          fullName: a.careProfile.fullName, // tên care profile
          relation: a.careProfile.relation,
        }
      : null,

    doctor: a.doctor
      ? {
          id: a.doctor.id,
          fullName: a.doctor.fullName, // tên bác sĩ
          email: a.doctor.email,
          phone: a.doctor.phone,
        }
      : null,
  }));
}

module.exports = {
  getProfile,
  upsertProfile,
  getAppointments,
  getPaidAppointments,
  getUpcomingAppointmentReminders,
  getAppointmentResults, // <--- nhớ export
};
