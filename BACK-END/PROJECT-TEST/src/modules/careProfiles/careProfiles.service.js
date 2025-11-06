const prisma = require('../../config/db');
const loc = require('../locations/locations.service'); // dùng proxy tới provinces.open-api.vn

function pickCareProfilePayload(payload, { forUpdate = false } = {}) {
  const map = {
    fullName: 'fullName',
    relation: 'relation',
    dob:      'dob',
    gender:   'gender',

    phone:    'phone',
    email:    'email',
    nationalId: 'nationalId',
    occupation: 'occupation',

    country:  'country',
    ethnicity:'ethnicity',

    province: 'province',
    district: 'district',
    ward:     'ward',

    address:  'address',
    insuranceNo: 'insuranceNo',
    note:       'note'
  };

  const data = {};
  Object.keys(map).forEach((k) => {
    if (payload[k] === undefined) return;
    if (k === 'dob') {
      data.dob = payload.dob ? new Date(payload.dob) : null;
    } else {
      data[map[k]] = payload[k] === '' ? null : payload[k];
    }
  });

  if (forUpdate) {
    Object.keys(map).forEach((k) => {
      if (payload[k] === undefined) data[map[k]] = undefined;
    });
  }
  return data;
}

// Nếu nhận provinceCode/districtCode/wardCode → điền tên tương ứng
async function resolveLocationFromCodes(payload = {}) {
  const out = {};
  if (payload.provinceCode) {
    const provs = await loc.listProvinces();
    const pcInt = String(parseInt(String(payload.provinceCode), 10));
    const p = provs.find(x => x.code === pcInt.padStart(2, '0') || x.code === pcInt);
    if (!p) throw new Error('Invalid province_code');
    out.province = p.name;

    if (payload.districtCode) {
      const dists = await loc.listDistricts({ province_code: pcInt });
      const dcInt = String(parseInt(String(payload.districtCode), 10));
      const d = dists.find(x => String(x.code) === dcInt);
      if (!d) throw new Error('Invalid district_code');
      out.district = d.name;

      if (payload.wardCode) {
        const wards = await loc.listWards({ district_code: dcInt });
        const wcInt = String(parseInt(String(payload.wardCode), 10));
        const w = wards.find(x => String(x.code) === wcInt);
        if (!w) throw new Error('Invalid ward_code');
        out.ward = w.name;
      }
    }
  }
  return out;
}

async function listMine(ownerId) {
  return prisma.careProfile.findMany({
    where: { ownerId },
    orderBy: { createdAt: 'desc' }
  });
}

async function create(ownerId, payload) {
  const base = pickCareProfilePayload(payload);
  // Nếu có codes → resolve thành tên & ghi đè
  const resolved = await resolveLocationFromCodes(payload);
  const data = { ...base, ...resolved };

  // guard tối thiểu (controller đã validate, thêm lần nữa)
  if (!data.fullName || !data.relation) throw new Error('fullName & relation are required');

  return prisma.careProfile.create({ data: { ownerId, ...data } });
}

async function update(ownerId, id, payload) {
  const found = await prisma.careProfile.findUnique({
    where: { id },
    select: { ownerId: true },
  });
  if (!found || found.ownerId !== ownerId) throw new Error('Not found');

  const base = pickCareProfilePayload(payload, { forUpdate: true });
  const resolved = await resolveLocationFromCodes(payload);
  const data = { ...base, ...resolved };

  return prisma.careProfile.update({ where: { id }, data });
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