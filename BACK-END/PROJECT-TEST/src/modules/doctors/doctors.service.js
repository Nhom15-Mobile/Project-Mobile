// src/modules/doctors/doctors.service.js
const prisma = require('../../config/db');
const { toPagination } = require('../../utils/pagination');

async function search(query) {
  const { q, specialty } = query;
  const { skip, take, page, pageSize } = toPagination(query);

  const where = {
    AND: [
      specialty ? { specialty: { contains: specialty } } : {},
      q
        ? {
            OR: [
              // filter theo tên bác sĩ (bảng User – quan hệ 1-1 -> dùng is: {})
              { user: { is: { fullName: { contains: q } } } },
              // filter theo bio của DoctorProfile
              { bio: { contains: q } }
            ]
          }
        : {}
    ]
  };

  const [items, total] = await Promise.all([
    prisma.doctorProfile.findMany({
      where,
      skip,
      take,
      include: { user: true },
      orderBy: { createdAt: 'desc' }
    }),
    prisma.doctorProfile.count({ where })
  ]);

  return { items, page, pageSize, total };
}

module.exports = { search, getById: async (id) => prisma.doctorProfile.findUnique({
  where: { id },
  include: { user: true }
}) };