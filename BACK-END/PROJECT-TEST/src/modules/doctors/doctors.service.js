const prisma = require('../../config/db');
const { toPagination } = require('../../utils/pagination');

// =============== Helpers (TZ: Asia/Ho_Chi_Minh) ===============
const TZ = '+07:00';
const startOfDayISO = (day) => new Date(`${day}T00:00:00${TZ}`);
const endOfDayISO   = (day) => new Date(`${day}T23:59:59.999${TZ}`);
const toISO         = (day, hm) => new Date(`${day}T${hm}:00${TZ}`);
const dayRange      = (day) => ({ start: startOfDayISO(day), end: endOfDayISO(day) });
function validRange(day, startHM, endHM) {
  const start = toISO(day, startHM);
  const end = toISO(day, endHM);
  if (Number.isNaN(start) || Number.isNaN(end)) throw new Error('Invalid time format HH:mm');
  if (end <= start) throw new Error('end must be after start');
  return { start, end };
}
const rangesOverlap = (a, b) => a.start < b.end && a.end > b.start;

// =============== Profile ===============
async function getProfileByUserId(userId) {
  return prisma.doctorProfile.findUnique({ where: { userId }, include: { user: true } });
}
async function updateProfile(userId, data) {
  const allowed = ['specialty', 'bio', 'yearsExperience', 'clinicName', 'rating'];
  const cleaned = Object.fromEntries(
    Object.entries(data).filter(([k, v]) => allowed.includes(k) && v !== undefined)
  );

  await prisma.doctorProfile.upsert({
    where: { userId },
    update: cleaned,
    create: { userId, ...cleaned }
  });

  return prisma.doctorProfile.findUnique({
    where: { userId },
    include: { user: { select: { id: true, fullName: true, email: true } } }
  });
}

// =============== Search/List ===============
async function search(query) {
  const { q, specialty } = query;
  const { skip, take, page, pageSize } = toPagination(query);

  const where = {
    AND: [
      specialty ? { specialty: { contains: specialty } } : {},
      q
        ? {
            OR: [
              { user: { is: { fullName: { contains: q } } } },
              { bio: { contains: q } }
            ]
          }
        : {}
    ]
  };

  const [itemsRaw, total] = await Promise.all([
    prisma.doctorProfile.findMany({
      where,
      skip,
      take,
      include: { user: true },
      orderBy: { createdAt: 'asc' } // để sau sort theo tên
    }),
    prisma.doctorProfile.count({ where })
  ]);

  const items = [...itemsRaw].sort((a, b) => a.user.fullName.localeCompare(b.user.fullName, 'vi'));
  return { items, page, pageSize, total };
}

async function listSpecialties() {
  const raw = await prisma.doctorProfile.findMany({
    select: { specialty: true },
    distinct: ['specialty'],
    orderBy: { specialty: 'asc' }
  });
  return raw.map(r => r.specialty).filter(s => s && s.trim() !== '');
}

/**
 * Bác sĩ còn slot trong ngày theo chuyên khoa + text + minRating
 * trả về kèm tối đa N slot đầu (slotsPerDoctor)
 */
async function availableByDay({ specialty, day, q = '', minRating, slotsPerDoctor = 3 }) {
  const { start, end } = dayRange(day);

  const where = {
    ...(specialty ? { specialty: { contains: specialty } } : {}),
    ...(minRating != null ? { rating: { gte: minRating } } : {}),
    slots: {
      some: {
        start: { gte: start },
        end:   { lte: end },
        isBooked: false
      }
    },
    ...(q
      ? { user: { is: { fullName: { contains: q } } } }
      : {})
  };

  const doctors = await prisma.doctorProfile.findMany({
    where,
    include: {
      user: { select: { id: true, fullName: true, email: true } },
      slots: {
        where: { start: { gte: start }, end: { lte: end }, isBooked: false },
        orderBy: { start: 'asc' },
        select: { id: true, start: true, end: true },
        take: slotsPerDoctor
      }
    },
    orderBy: [{ rating: 'desc' }, { updatedAt: 'desc' }]
  });

  // đảm bảo thứ tự thân thiện theo tên nếu rating bằng nhau
  doctors.sort((a, b) => {
    if ((a.rating ?? 0) !== (b.rating ?? 0)) return (b.rating ?? 0) - (a.rating ?? 0);
    return a.user.fullName.localeCompare(b.user.fullName, 'vi');
  });

  return doctors;
}

// =============== Workday (blocks + capacity) ===============
async function setWorkDayBlocks({ doctorProfileId, day, blocks, replaceUnbooked = false }) {
  if (!doctorProfileId) throw new Error('doctorProfileId missing (internal)');

  const normalized = blocks.map(b => {
    if (!b.start || !b.end) throw new Error('Each block requires start & end');
    const cap = Number(b.capacity ?? 1);
    if (!Number.isInteger(cap) || cap < 1) throw new Error('capacity must be >= 1');
    const { start, end } = validRange(day, b.start, b.end);
    return { start, end, capacity: cap };
  });

  const sorted = [...normalized].sort((a,b)=> a.start - b.start || a.end - b.end);
  for (let i=1; i<sorted.length; i++) {
    const prev = sorted[i-1], cur = sorted[i];
    const sameRange = prev.start.getTime() === cur.start.getTime() && prev.end.getTime() === cur.end.getTime();
    if (!sameRange && rangesOverlap(prev, cur)) {
      throw new Error('Blocks overlap each other; merge or separate them');
    }
  }

  const dayStart = startOfDayISO(day);
  const dayEnd   = endOfDayISO(day);

  if (replaceUnbooked) {
    await prisma.doctorSlot.deleteMany({
      where: { doctorId: doctorProfileId, start: { gte: dayStart }, end: { lte: dayEnd }, isBooked: false }
    });
  }

  const existing = await prisma.doctorSlot.findMany({
    where: { doctorId: doctorProfileId, start: { gte: dayStart }, end: { lte: dayEnd } },
    select: { id: true, start: true, end: true, isBooked: true }
  });

  const toCreate = [];
  for (const blk of normalized) {
    const sameRangeCount = existing.filter(e =>
      e.start.getTime() === blk.start.getTime() && e.end.getTime() === blk.end.getTime()
    ).length;

    let need = blk.capacity - sameRangeCount;
    if (need <= 0) continue;

    if (!replaceUnbooked) {
      for (const e of existing) {
        const sameRange = e.start.getTime() === blk.start.getTime() && e.end.getTime() === blk.end.getTime();
        if (!sameRange && rangesOverlap(blk, e)) {
          throw new Error('Block overlaps existing slot with different range');
        }
      }
      for (const c of toCreate) {
        const sameRange = c.start.getTime() === blk.start.getTime() && c.end.getTime() === blk.end.getTime();
        if (!sameRange && rangesOverlap(blk, c)) {
          throw new Error('Block overlaps new slot being created');
        }
      }
    }

    for (let i=0; i<need; i++) toCreate.push({ start: new Date(blk.start), end: new Date(blk.end) });
  }

  if (!toCreate.length) return { created: 0, slots: [] };

  const created = await prisma.$transaction(async (tx) => {
    const items = [];
    for (const s of toCreate) {
      const it = await tx.doctorSlot.create({
        data: { doctorId: doctorProfileId, start: s.start, end: s.end }
      });
      items.push(it);
    }
    return items;
  });

  return { created: created.length, slots: created };
}

async function getWorkDay({ doctorProfileId, day }) {
  if (!doctorProfileId) throw new Error('doctorProfileId missing (internal)');
  const dayStart = startOfDayISO(day);
  const dayEnd   = endOfDayISO(day);
  return prisma.doctorSlot.findMany({
    where: { doctorId: doctorProfileId, start: { gte: dayStart }, end: { lte: dayEnd } },
    orderBy: { start: 'asc' }
  });
}

// =============== Get by id (profile) ===============
const getById = async (userId) =>
  prisma.doctorProfile.findUnique({ where: { userId }, include: { user: true } });

module.exports = {
  // profile
  getProfileByUserId,
  updateProfile,

  // workday
  setWorkDayBlocks,
  getWorkDay,

  // search & availability
  listSpecialties,
  availableByDay,
  search,
  getById
};