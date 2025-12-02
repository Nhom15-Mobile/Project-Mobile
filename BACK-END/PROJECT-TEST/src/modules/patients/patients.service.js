// src/modules/patients/patients.service.js
const prisma = require('../../config/db');

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

async function getAppointments(userId) {
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

module.exports = {
  getProfile,
  upsertProfile,
  getAppointments,
  getPaidAppointments,
};
