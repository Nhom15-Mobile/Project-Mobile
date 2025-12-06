const { PrismaClient } = require('@prisma/client');
const bcrypt = require('bcryptjs');

const prisma = new PrismaClient();
const UNPAID_EXPIRE_MINUTES = 10;

class AdminService {
  // ============= USER MANAGEMENT =============
  async createUser(data) {
    const { email, password, fullName, phone, role } = data;

    // Check if user exists
    const exists = await prisma.user.findUnique({ where: { email } });
    if (exists) {
      throw new Error('Email already exists');
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Create user
    const user = await prisma.user.create({
      data: {
        email,
        password: hashedPassword,
        fullName,
        phone,
        role: role || 'PATIENT',
      },
      select: {
        id: true,
        email: true,
        fullName: true,
        phone: true,
        role: true,
        createdAt: true,
      }
    });

    return user;
  }

  async getAllUsers(filters = {}) {
    let { role, search, page = 1, limit = 20 } = filters;

    const pageNum = parseInt(page, 10) || 1;
    const limitNum = parseInt(limit, 10) || 20;
    const skip = (pageNum - 1) * limitNum;

    const where = {};
    if (role) where.role = role;
    if (search) {
      where.OR = [
        { email: { contains: search, mode: 'insensitive' } },
        { fullName: { contains: search, mode: 'insensitive' } },
        { phone: { contains: search, mode: 'insensitive' } },
      ];
    }

    const [users, total] = await Promise.all([
      prisma.user.findMany({
        where,
        skip,
        take: limitNum,
        select: {
          id: true,
          email: true,
          fullName: true,
          phone: true,
          role: true,
          createdAt: true,
          updatedAt: true,
        },
        orderBy: { createdAt: 'desc' }
      }),
      prisma.user.count({ where })
    ]);

    return {
      users,
      pagination: {
        total,
        page: pageNum,
        limit: limitNum,
        pages: Math.max(1, Math.ceil(total / limitNum))
      }
    };
  }

  async deleteUser(userId) {
    await prisma.user.delete({ where: { id: userId } });
    return { message: 'User deleted successfully' };
  }

  // ============= DOCTOR MANAGEMENT =============
  async createDoctor(data) {
    const {
      email,
      password,
      fullName,
      phone,
      specialty,
      bio,
      yearsExperience,
      clinicName
    } = data;

    // Check if user exists
    const exists = await prisma.user.findUnique({ where: { email } });
    if (exists) {
      throw new Error('Email already exists');
    }

    // Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // Create user with doctor profile
    const doctor = await prisma.user.create({
      data: {
        email,
        password: hashedPassword,
        fullName,
        phone,
        role: 'DOCTOR',
        doctor: {
          create: {
            specialty,
            bio,
            yearsExperience: yearsExperience ? parseInt(yearsExperience, 10) : null,
            clinicName,
          }
        }
      },
      include: {
        doctor: true
      }
    });

    return {
      id: doctor.id,
      email: doctor.email,
      fullName: doctor.fullName,
      phone: doctor.phone,
      role: doctor.role,
      doctorProfile: doctor.doctor
    };
  }

  async getAllDoctors(filters = {}) {
    let { specialty, search, page = 1, limit = 20 } = filters;

    const pageNum = parseInt(page, 10) || 1;
    const limitNum = parseInt(limit, 10) || 20;
    const skip = (pageNum - 1) * limitNum;

    const where = { role: 'DOCTOR' };

    if (specialty || search) {
      where.doctor = {};
      if (specialty) {
        where.doctor.specialty = { contains: specialty, mode: 'insensitive' };
      }
    }

    if (search) {
      where.OR = [
        { email: { contains: search, mode: 'insensitive' } },
        { fullName: { contains: search, mode: 'insensitive' } },
        { phone: { contains: search, mode: 'insensitive' } },
      ];
    }

    const [doctors, total] = await Promise.all([
      prisma.user.findMany({
        where,
        skip,
        take: limitNum,
        include: {
          doctor: true
        },
        orderBy: { createdAt: 'desc' }
      }),
      prisma.user.count({ where })
    ]);

    return {
      doctors,
      pagination: {
        total,
        page: pageNum,
        limit: limitNum,
        pages: Math.max(1, Math.ceil(total / limitNum))
      }
    };
  }

  // ============= CARE PROFILE MANAGEMENT =============
  async createCareProfile(data) {
    const {
      ownerId,
      fullName,
      relation,
      dob,
      gender,
      phone,
      email,
      nationalId,
      occupation,
      country,
      ethnicity,
      province,
      district,
      ward,
      address,
      insuranceNo,
      note
    } = data;

    // Verify owner exists
    const owner = await prisma.user.findUnique({ where: { id: ownerId } });
    if (!owner) {
      throw new Error('Owner user not found');
    }

    const careProfile = await prisma.careProfile.create({
      data: {
        ownerId,
        fullName,
        relation,
        dob: dob ? new Date(dob) : null,
        gender,
        phone,
        email,
        nationalId,
        occupation,
        country,
        ethnicity,
        province,
        district,
        ward,
        address,
        insuranceNo,
        note,
      },
      include: {
        owner: {
          select: {
            id: true,
            email: true,
            fullName: true,
          }
        }
      }
    });

    return careProfile;
  }

  async getAllCareProfiles(filters = {}) {
    let { ownerId, search, page = 1, limit = 20 } = filters;

    const pageNum = parseInt(page, 10) || 1;
    const limitNum = parseInt(limit, 10) || 20;
    const skip = (pageNum - 1) * limitNum;

    const where = {};
    if (ownerId) where.ownerId = ownerId;
    if (search) {
      where.OR = [
        { fullName: { contains: search, mode: 'insensitive' } },
        { phone: { contains: search, mode: 'insensitive' } },
        { email: { contains: search, mode: 'insensitive' } },
      ];
    }

    const [careProfiles, total] = await Promise.all([
      prisma.careProfile.findMany({
        where,
        skip,
        take: limitNum,
        include: {
          owner: {
            select: {
              id: true,
              email: true,
              fullName: true,
            }
          }
        },
        orderBy: { createdAt: 'desc' }
      }),
      prisma.careProfile.count({ where })
    ]);

    return {
      careProfiles,
      pagination: {
        total,
        page: pageNum,
        limit: limitNum,
        pages: Math.max(1, Math.ceil(total / limitNum))
      }
    };
  }

  // ============= DOCTOR SLOT MANAGEMENT =============
  async createDoctorSlot(data) {
    const { doctorId, start, end } = data;

    // Verify doctor exists (DoctorProfile linked với User)
    const doctor = await prisma.doctorProfile.findUnique({
      where: { userId: doctorId }
    });
    if (!doctor) {
      throw new Error('Doctor not found');
    }

    // Check for overlapping slots
    const startDate = new Date(start);
    const endDate = new Date(end);

    const overlapping = await prisma.doctorSlot.findFirst({
      where: {
        doctorId,
        OR: [
          {
            start: { lte: startDate },
            end: { gt: startDate }
          },
          {
            start: { lt: endDate },
            end: { gte: endDate }
          }
        ]
      }
    });

    if (overlapping) {
      throw new Error('This time slot overlaps with an existing slot');
    }

    const slot = await prisma.doctorSlot.create({
      data: {
        doctorId,
        start: startDate,
        end: endDate,
      },
      include: {
        doctor: {
          include: {
            user: {
              select: {
                id: true,
                fullName: true,
                email: true,
              }
            }
          }
        }
      }
    });

    return slot;
  }

  async getDoctorSlots(filters = {}) {
    // dọn rác appointment hết hạn trước
    await this.cleanupExpiredUnpaidAppointments();

    let {
      doctorId,
      date,
      isBooked,
      page = 1,
      limit = 50,
    } = filters;

    const pageNum = parseInt(page, 10) || 1;
    const limitNum = parseInt(limit, 10) || 50;
    const skip = (pageNum - 1) * limitNum;

    const where = {};
    if (doctorId) where.doctorId = doctorId;

    // isBooked có thể là 'true'/'false' (string) hoặc boolean
    if (typeof isBooked !== 'undefined') {
      if (typeof isBooked === 'string') {
        where.isBooked = isBooked === 'true';
      } else if (typeof isBooked === 'boolean') {
        where.isBooked = isBooked;
      }
    }

    if (date) {
      const startOfDay = new Date(date);
      startOfDay.setHours(0, 0, 0, 0);

      const endOfDay = new Date(date);
      endOfDay.setHours(23, 59, 59, 999);

      where.start = { gte: startOfDay, lt: endOfDay };
    }

    const [items, total, availableCount, bookedCount] = await Promise.all([
      prisma.doctorSlot.findMany({
        where,
        include: {
          doctor: {
            include: { user: true },
          },
        },
        orderBy: { start: 'asc' },
        skip,
        take: limitNum,
      }),
      prisma.doctorSlot.count({ where }),
      prisma.doctorSlot.count({
        where: { ...where, isBooked: false },
      }),
      prisma.doctorSlot.count({
        where: { ...where, isBooked: true },
      }),
    ]);

    const pages = Math.max(Math.ceil(total / limitNum), 1);

    return {
      slots: items,
      pagination: {
        total,
        availableCount,
        bookedCount,
        page: pageNum,
        pages,
        limit: limitNum,
      },
    };
  }

  async deleteDoctorSlot(slotId) {
    // Check if slot is booked or có appointment
    const slot = await prisma.doctorSlot.findUnique({
      where: { id: slotId },
      include: { appointments: true }
    });

    if (!slot) {
      throw new Error('Slot not found');
    }

    if (slot.appointments.length > 0) {
      throw new Error('Cannot delete slot with existing appointments');
    }

    await prisma.doctorSlot.delete({ where: { id: slotId } });
    return { message: 'Slot deleted successfully' };
  }

  // ============= APPOINTMENT MANAGEMENT =============
  async createAppointment(data) {
    const { careProfileId, slotId, service } = data;

    // Verify care profile exists
    const careProfile = await prisma.careProfile.findUnique({
      where: { id: careProfileId },
      include: { owner: true }
    });
    if (!careProfile) {
      throw new Error('Care profile not found');
    }

    // Verify slot exists and is available
    const slot = await prisma.doctorSlot.findUnique({
      where: { id: slotId }
    });
    if (!slot) {
      throw new Error('Doctor slot not found');
    }
    if (slot.isBooked) {
      throw new Error('This time slot is already booked');
    }

    // Create appointment and mark slot as booked
    const appointment = await prisma.$transaction(async (tx) => {
      // Create appointment
      const appt = await tx.appointment.create({
        data: {
          patientId: careProfile.ownerId,
          doctorId: slot.doctorId,
          careProfileId,
          slotId,
          service,
          scheduledAt: slot.start,
          status: 'PENDING',
        },
        include: {
          patient: {
            select: {
              id: true,
              fullName: true,
              email: true,
            }
          },
          doctor: {
            select: {
              id: true,
              fullName: true,
              email: true,
            }
          },
          careProfile: true,
          slot: true,
        }
      });

      // Mark slot as booked
      await tx.doctorSlot.update({
        where: { id: slotId },
        data: { isBooked: true }
      });

      return appt;
    });

    return appointment;
  }

  async getAllAppointments(filters = {}) {
    await this.cleanupExpiredUnpaidAppointments();

    let { patientId, doctorId, status, page = 1, limit = 20 } = filters;

    const pageNum = parseInt(page, 10) || 1;
    const limitNum = parseInt(limit, 10) || 20;
    const skip = (pageNum - 1) * limitNum;

    const where = {};
    if (patientId) where.patientId = patientId;
    if (doctorId) where.doctorId = doctorId;
    if (status) where.status = status;

    const [appointments, total] = await Promise.all([
      prisma.appointment.findMany({
        where,
        skip,
        take: limitNum,
        include: {
          patient: {
            select: {
              id: true,
              fullName: true,
              email: true,
              phone: true,
            }
          },
          doctor: {
            select: {
              id: true,
              fullName: true,
              email: true,
            }
          },
          careProfile: true,
          slot: true,
          payment: true,
        },
        orderBy: { createdAt: 'desc' }
      }),
      prisma.appointment.count({ where })
    ]);

    return {
      appointments,
      pagination: {
        total,
        page: pageNum,
        limit: limitNum,
        pages: Math.max(1, Math.ceil(total / limitNum))
      }
    };
  }

  async updateAppointmentStatus(appointmentId, status) {
    const appointment = await prisma.appointment.update({
      where: { id: appointmentId },
      data: { status },
      include: {
        patient: {
          select: {
            id: true,
            fullName: true,
            email: true,
          }
        },
        doctor: {
          select: {
            id: true,
            fullName: true,
            email: true,
          }
        },
        careProfile: true,
      }
    });

    return appointment;
  }

  async updateAppointmentPaymentStatus(appointmentId, paymentStatus) {
    const appointment = await prisma.appointment.update({
      where: { id: appointmentId },
      data: { paymentStatus },
      include: {
        patient: {
          select: {
            id: true,
            fullName: true,
            email: true,
          }
        },
        doctor: {
          select: {
            id: true,
            fullName: true,
            email: true,
          }
        },
        careProfile: true,
      }
    });

    return appointment;
  }

  async deleteAppointment(appointmentId) {
    const appointment = await prisma.appointment.findUnique({
      where: { id: appointmentId }
    });

    if (!appointment) {
      throw new Error('Appointment not found');
    }

    // Free up the slot if it exists
    await prisma.$transaction(async (tx) => {
      if (appointment.slotId) {
        await tx.doctorSlot.update({
          where: { id: appointment.slotId },
          data: { isBooked: false }
        });
      }

      await tx.appointment.delete({ where: { id: appointmentId } });
    });

    return { message: 'Appointment deleted successfully' };
  }

  // ============= AUTO CLEANUP UNPAID & EXPIRED APPOINTMENTS =============
  async cleanupExpiredUnpaidAppointments() {
    const cutoff = new Date(Date.now() - UNPAID_EXPIRE_MINUTES * 60 * 1000);

    // tìm các lịch chưa thanh toán đã quá hạn
    const expired = await prisma.appointment.findMany({
      where: {
        status: 'PENDING',
        paymentStatus: 'REQUIRES_PAYMENT',
        createdAt: { lt: cutoff },
      },
      select: {
        id: true,
        slotId: true,
      },
    });

    if (!expired.length) return { count: 0 };

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

          // xóa mọi payment gắn với appointment này
          await tx.payment.deleteMany({
            where: { appointmentId: appt.id },
          });

          // xóa appointment
          await tx.appointment.delete({
            where: { id: appt.id },
          });
        });
      } catch (err) {
        console.error('Failed to cleanup expired appointment', appt.id, err);
      }
    }

    return { count: expired.length };
  }

  // ============= STATISTICS =============
  async getStatistics() {
    const [
      totalUsers,
      totalDoctors,
      totalPatients,
      adminUsers,
      totalCareProfiles,
      totalAppointments,
      pendingAppointments,
      completedAppointments,
      totalDoctorSlots,
      availableSlots,
      bookedSlots,
    ] = await Promise.all([
      prisma.user.count(),
      prisma.user.count({ where: { role: 'DOCTOR' } }),
      prisma.user.count({ where: { role: 'PATIENT' } }),
      prisma.user.count({ where: { role: 'ADMIN' } }),
      prisma.careProfile.count(),
      prisma.appointment.count(),
      prisma.appointment.count({ where: { status: 'PENDING' } }),
      prisma.appointment.count({ where: { status: 'COMPLETED' } }),
      prisma.doctorSlot.count(),
      prisma.doctorSlot.count({ where: { isBooked: false } }),
      prisma.doctorSlot.count({ where: { isBooked: true } }),
    ]);

    return {
      totalUsers,
      totalDoctors,
      totalPatients,
      adminUsers,
      totalCareProfiles,
      totalAppointments,
      pendingAppointments,
      completedAppointments,
      totalDoctorSlots,
      availableSlots,
      bookedSlots,
    };
  }

    // ============= APPOINTMENT RESULT (ADMIN) =============
async updateAppointmentResult(appointmentId, resultText, treatmentText) {
  if (!resultText || !resultText.trim()) {
    throw new Error('Result text is required');
  }

  const appt = await prisma.appointment.update({
    where: { id: appointmentId },
    data: {
      // KHÔNG đụng status, chỉ lưu kết quả & hướng dẫn điều trị
      examResult: resultText.trim(),
      treatmentPlan: treatmentText && treatmentText.trim() ? treatmentText.trim() : null,
    },
    include: {
      patient: {
        select: { id: true, fullName: true, email: true, phone: true },
      },
      doctor: {
        select: { id: true, fullName: true, email: true },
      },
      careProfile: {
        select: { id: true, fullName: true, relation: true },
      },
      slot: true,
      payment: true,
    },
  });

  // format “đẹp” để FE/app xài
  return {
    id: appt.id,
    examResult: appt.examResult || '',
    treatmentPlan: appt.treatmentPlan || '',

    service: appt.service,
    status: appt.status,
    paymentStatus: appt.paymentStatus,
    scheduledAt: appt.scheduledAt,

    doctor: appt.doctor
      ? {
          id: appt.doctor.id,
          fullName: appt.doctor.fullName,
          email: appt.doctor.email,
        }
      : null,

    patient: appt.patient
      ? {
          id: appt.patient.id,
          fullName: appt.patient.fullName,
          email: appt.patient.email,
          phone: appt.patient.phone,
        }
      : null,

    careProfile: appt.careProfile
      ? {
          id: appt.careProfile.id,
          fullName: appt.careProfile.fullName,
          relation: appt.careProfile.relation,
        }
      : null,

    slot: appt.slot
      ? { id: appt.slot.id, start: appt.slot.start, end: appt.slot.end }
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
}


}

module.exports = new AdminService();
