const prisma = require('../../config/db');

async function listMine(ownerId) {
  return prisma.careProfile.findMany({
    where: { ownerId },
    orderBy: { createdAt: 'desc' }
  });
}

async function create(ownerId, payload) {
  // tối thiểu: fullName + relation
  const data = {
    ownerId,
    fullName: payload.fullName,
    relation: payload.relation,
    dob: payload.dob ? new Date(payload.dob) : null,
    gender: payload.gender || null,
    // phone: payload.phone || null,
    insuranceNo: payload.insuranceNo || null,
    note: payload.note || null,
  };
  return prisma.careProfile.create({ data });
}

async function update(ownerId, id, payload) {
  const found = await prisma.careProfile.findUnique({
    where: { id },
    select: { ownerId: true },
  });
  if (!found || found.ownerId !== ownerId) throw new Error('Not found');

  return prisma.careProfile.update({
    where: { id },
    data: {
      fullName: payload.fullName,
      relation: payload.relation,
      dob: payload.dob ? new Date(payload.dob) : undefined,
      gender: payload.gender,
    //   phone: payload.phone,
      insuranceNo: payload.insuranceNo,
      note: payload.note,
    },
  });
}

async function remove(ownerId, id) {
  const found = await prisma.careProfile.findUnique({
    where: { id },
    select: { ownerId: true },
  });
  if (!found || found.ownerId !== ownerId) throw new Error('Not found');

  return prisma.careProfile.delete({ where: { id } });
}

module.exports = { listMine, create, update, remove };