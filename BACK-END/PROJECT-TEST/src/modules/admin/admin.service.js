const { PrismaClient } = require('@prisma/client');
const bcrypt = require('bcryptjs');

const prisma = new PrismaClient();

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
    const { role, search, page = 1, limit = 20 } = filters;
    const skip = (page - 1) * limit;

    const where = {};
    if (role) where.role = role;
    if (search) {
      where.OR = [
        { email: { contains: search } },
        { fullName: { contains: search } },
        { phone: { contains: search } },
      ];
    }

    const [users, total] = await Promise.all([
      prisma.user.findMany({
        where,
        skip,
        take: limit,
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
        page,
        limit,
        pages: Math.ceil(total / limit)
      }
    };
  }

  async deleteUser(userId) {
    await prisma.user.delete({ where: { id: userId } });
    return { message: 'User deleted successfully' };
  }

  // ============= DOCTOR MANAGEMENT =============
  async createDoctor(data) {
    const { email, password, fullName, phone, specialty, bio, yearsExperience, clinicName } = data;

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
            yearsExperience: yearsExperience ? parseInt(yearsExperience) : null,
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
    const { specialty, search, page = 1, limit = 20 } = filters;
    const skip = (page - 1) * limit;

    const where = { role: 'DOCTOR' };

    if (specialty || search) {
      where.doctor = {};
      if (specialty) where.doctor.specialty = { contains: specialty };
    }

    if (search) {
      where.OR = [
        { email: { contains: search } },
        { fullName: { contains: search } },
        { phone: { contains: search } },
      ];
    }

    const [doctors, total] = await Promise.all([
      prisma.user.findMany({
        where,
        skip,
        take: limit,
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
        page,
        limit,
        pages: Math.ceil(total / limit)
      }
    };
  }

  // ============= CARE PROFILE MANAGEMENT =============
  async createCareProfile(data) {
    const { ownerId, fullName, relation, dob, gender, phone, email,
            nationalId, occupation, country, ethnicity, province, district,
            ward, address, insuranceNo, note } = data;

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
    const { ownerId, search, page = 1, limit = 20 } = filters;
    const skip = (page - 1) * limit;

    const where = {};
    if (ownerId) where.ownerId = ownerId;
    if (search) {
      where.OR = [
        { fullName: { contains: search } },
        { phone: { contains: search } },
        { email: { contains: search } },
      ];
    }

    const [careProfiles, total] = await Promise.all([
      prisma.careProfile.findMany({
        where,
        skip,
        take: limit,
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
        page,
        limit,
        pages: Math.ceil(total / limit)
      }
    };
  }

  // ============= DOCTOR SLOT MANAGEMENT =============
  async createDoctorSlot(data) {
    const { doctorId, start, end } = data;

    // Verify doctor exists
    const doctor = await prisma.doctorProfile.findUnique({
      where: { userId: doctorId }
    });
    if (!doctor) {
      throw new Error('Doctor not found');
    }

    // Check for overlapping slots
    const overlapping = await prisma.doctorSlot.findFirst({
      where: {
        doctorId,
        OR: [
          {
            start: { lte: new Date(start) },
            end: { gt: new Date(start) }
          },
          {
            start: { lt: new Date(end) },
            end: { gte: new Date(end) }
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
        start: new Date(start),
        end: new Date(end),
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

  // async getDoctorSlots(filters = {}) {
  //   const { doctorId, date, isBooked, page = 1, limit = 50 } = filters;
  //   const skip = (page - 1) * limit;

  //   const where = {};
  //   if (doctorId) where.doctorId = doctorId;
  //   if (typeof isBooked !== 'undefined') where.isBooked = isBooked === 'true';

  //   if (date) {
  //     const startOfDay = new Date(date);
  //     startOfDay.setHours(0, 0, 0, 0);
  //     const endOfDay = new Date(date);
  //     endOfDay.setHours(23, 59, 59, 999);

  //     where.start = { gte: startOfDay, lte: endOfDay };
  //   }

  //   const [slots, total] = await Promise.all([
  //     prisma.doctorSlot.findMany({
  //       where,
  //       skip,
  //       take: limit,
  //       include: {
  //         doctor: {
  //           include: {
  //             user: {
  //               select: {
  //                 id: true,
  //                 fullName: true,
  //                 email: true,
  //               }
  //             }
  //           }
  //         }
  //       },
  //       orderBy: { start: 'asc' }
  //     }),
  //     prisma.doctorSlot.count({ where })
  //   ]);

  //   return {
  //     slots,
  //     pagination: {
  //       total,
  //       page,
  //       limit,
  //       pages: Math.ceil(total / limit)
  //     }
  //   };
  // }
// ============= DOCTOR SLOT MANAGEMENT =============
async getDoctorSlots(filters = {}) {
  let { doctorId, date, isBooked, page = 1, limit = 50 } = filters;

  // query string luôn là string -> ép về number
  const pageNum = parseInt(page, 10) || 1;
  const limitNum = parseInt(limit, 10) || 50;
  const skip = (pageNum - 1) * limitNum;

  const where = {};
  if (doctorId) where.doctorId = doctorId;
  if (typeof isBooked !== 'undefined') where.isBooked = isBooked === 'true';

  if (date) {
    const startOfDay = new Date(date);
    startOfDay.setHours(0, 0, 0, 0);
    const endOfDay = new Date(date);
    endOfDay.setHours(23, 59, 59, 999);

    where.start = { gte: startOfDay, lte: endOfDay };
  }

  const [slots, total] = await Promise.all([
    prisma.doctorSlot.findMany({
      where,
      skip,
      take: limitNum,   // <- dùng number
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
      },
      orderBy: { start: 'asc' }
    }),
    prisma.doctorSlot.count({ where })
  ]);

  return {
    slots,
    pagination: {
      total,
      page: pageNum,
      limit: limitNum,
      pages: Math.ceil(total / limitNum)
    }
  };
}

  async deleteDoctorSlot(slotId) {
    // Check if slot is booked
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
    const { patientId, doctorId, status, page = 1, limit = 20 } = filters;
    const skip = (page - 1) * limit;

    const where = {};
    if (patientId) where.patientId = patientId;
    if (doctorId) where.doctorId = doctorId;
    if (status) where.status = status;

    const [appointments, total] = await Promise.all([
      prisma.appointment.findMany({
        where,
        skip,
        take: limit,
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
        page,
        limit,
        pages: Math.ceil(total / limit)
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
    adminUsers,          // dùng cho User Roles + System Overview
    totalCareProfiles,
    totalAppointments,
    pendingAppointments,
    completedAppointments,
    totalDoctorSlots,    // System Overview: Doctor Slots
    availableSlots,      // System Overview + Doctor Slots Status chart
    bookedSlots,
  };
}
}

module.exports = new AdminService();
