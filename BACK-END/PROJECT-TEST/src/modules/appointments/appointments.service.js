// src/modules/appointments/appointments.service.js
const prisma = require('../../config/db');

/** Helper: range trong ngày theo local server */
function dayRange(dayISO) {
  const start = new Date(`${dayISO}T00:00:00`);
  const end = new Date(`${dayISO}T23:59:59.999`);
  return { start, end };
}

/**
 * Lấy slot trống của 1 bác sĩ theo ngày
 * @param {string} doctorUserId  // DoctorProfile.userId (== User.id của bác sĩ)
 * @param {string?} dayISO       // YYYY-MM-DD
 */
async function availableSlots(doctorUserId, dayISO) {
  const where = { doctorId: doctorUserId, isBooked: false };
  if (dayISO) {
    const { start, end } = dayRange(dayISO);
    where.start = { gte: start };
    where.end = { lte: end };
  }
  return prisma.doctorSlot.findMany({
    where,
    orderBy: { start: 'asc' }
  });
}

/**
 * Bệnh nhân đặt lịch cho 1 slot (có thể gắn careProfileId)
 * - verify careProfile thuộc owner
 * - verify slot tồn tại, thuộc đúng bác sĩ, chưa book, chưa quá khứ
 * - transaction: lock slot + create appointment
 */
async function book({ patientId, slotId, service, careProfileId }) {
  // (1) verify careProfile thuộc về patient (nếu có)
  if (careProfileId) {
    const cp = await prisma.careProfile.findUnique({
      where: { id: careProfileId },
      select: { ownerId: true }
    });
    if (!cp || cp.ownerId !== patientId) throw new Error('Care profile not found');
  }

  // (2) lấy slot và suy ra bác sĩ (DoctorProfile.userId)
  const slot = await prisma.doctorSlot.findUnique({
    where: { id: slotId },
    include: { doctor: { select: { userId: true } } } // doctor = DoctorProfile
  });
  if (!slot) throw new Error('Slot not found');
  if (slot.isBooked) throw new Error('Slot already booked');
  if (slot.start <= new Date()) throw new Error('Slot is in the past');

  // (3) transaction: chốt slot nếu còn trống rồi tạo appointment
  return prisma.$transaction(async (tx) => {
    const upd = await tx.doctorSlot.updateMany({
      where: { id: slotId, isBooked: false },
      data: { isBooked: true }
    });
    if (upd.count !== 1) throw new Error('Slot already booked');

    return tx.appointment.create({
      data: {
        patientId,
        careProfileId: careProfileId ?? null,
        doctorId: slot.doctor.userId,   // User.id của bác sĩ
        slotId,
        service,
        scheduledAt: slot.start,
        status: 'PENDING',
        paymentStatus: 'REQUIRES_PAYMENT'
      },
      include: {
        patient: { select: { id: true, fullName: true, email: true } },
        doctor:  { select: { id: true, fullName: true, email: true } },
        slot:    { select: { id: true, start: true, end: true, isBooked: true } },
        careProfile: true,
        payment: true
      }
    });
  });
}

/**
 * Bệnh nhân hủy lịch của chính mình
 * - mở lại slot nếu có
 */
async function cancel({ appointmentId, byUserId, reason }) {
  const appt = await prisma.appointment.findUnique({ where: { id: appointmentId } });
  if (!appt) throw new Error('Appointment not found');
  if (appt.patientId !== byUserId) throw new Error('Forbidden');

  return prisma.$transaction(async (tx) => {
    if (appt.slotId) {
      await tx.doctorSlot.update({ where: { id: appt.slotId }, data: { isBooked: false } });
    }
    return tx.appointment.update({
      where: { id: appointmentId },
      data: { status: 'CANCELLED', cancelReason: reason || 'Cancelled by patient' }
    });
  });
}

/**
 * Trả về danh sách ngày (YYYY-MM-DD) trong tháng có slot trống,
 * lọc theo specialty nếu có.
 * Flow FE: chọn tháng + (optional) chuyên khoa → những ngày có thể đặt.
 */
async function daysWithAvailability({ specialty, month }) {
  // Khoảng thời gian [month-01, month+1)
  const start = new Date(`${month}-01T00:00:00.000Z`);
  const end = new Date(start);
  end.setMonth(end.getMonth() + 1);

  // Lọc danh sách bác sĩ theo specialty (MySQL case-insensitive theo collation _ci)
  const doctors = await prisma.doctorProfile.findMany({
    where: specialty ? { specialty: { contains: specialty } } : {},
    select: { userId: true }
  });
  if (!doctors.length) return [];

  // Lọc slots trống của các bác sĩ trong khoảng tháng
  const slots = await prisma.doctorSlot.findMany({
    where: {
      doctorId: { in: doctors.map(d => d.userId) },
      isBooked: false,
      start: { gte: start, lt: end }
    },
    select: { start: true }
  });

  // Kết thành set các ngày (UTC date part)
  const set = new Set(slots.map(s => s.start.toISOString().slice(0, 10)));
  return Array.from(set).sort();
}

module.exports = { availableSlots, book, cancel, daysWithAvailability };