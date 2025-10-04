const prisma = require('../../config/db');

async function availableSlots(doctorProfileId, dateISO) {
  const where = { doctorId: doctorProfileId };
  if (dateISO) {
    const start = new Date(dateISO);
    const end = new Date(dateISO);
    end.setHours(23, 59, 59, 999);
    where.start = { gte: start };
    where.end = { lte: end };
  }
  return prisma.doctorSlot.findMany({
    where: { ...where, isBooked: false },
    orderBy: { start: 'asc' }
  });
}

async function book({ patientId, doctorProfileId, slotId, service }) {
  // 1) Lấy DoctorProfile để biết userId của bác sĩ
  const dp = await prisma.doctorProfile.findUnique({ where: { id: doctorProfileId } });
  if (!dp) throw new Error('Doctor not found');

  // 2) Kiểm tra slot thuộc đúng bác sĩ và còn trống
  const slot = await prisma.doctorSlot.findUnique({ where: { id: slotId } });
  if (!slot || slot.doctorId !== doctorProfileId || slot.isBooked) {
    throw new Error('Slot unavailable');
  }

  // 3) Transaction: giữ slot + tạo appointment
  return prisma.$transaction(async (tx) => {
    await tx.doctorSlot.update({
      where: { id: slotId },
      data: { isBooked: true }
    });
    const appt = await tx.appointment.create({
      data: {
        patientId,
        doctorId: dp.userId,           // <-- lưu User.id của bác sĩ
        slotId,
        service,
        scheduledAt: slot.start,
        status: 'PENDING',
        paymentStatus: 'REQUIRES_PAYMENT'
      }
    });
    return appt;
  });
}

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

module.exports = { availableSlots, book, cancel };