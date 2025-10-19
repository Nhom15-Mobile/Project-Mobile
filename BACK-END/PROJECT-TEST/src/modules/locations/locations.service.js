const axios = require('axios');

const BASE = 'https://provinces.open-api.vn'; // dùng domain gốc
const cache = { provinces: null, tPro: 0, districts: new Map(), wards: new Map() };
const TTL = 1000 * 60 * 60; // 1h

const now = () => Date.now();

function sortVi(a, b) {
  return a.name.localeCompare(b.name, 'vi');
}

async function listProvinces({ q } = {}) {
  if (!cache.provinces || now() - cache.tPro > TTL) {
    const { data } = await axios.get(`${BASE}/api/p/`); // all provinces
    cache.provinces = data.map(x => ({
      code: String(x.code).padStart(2, '0'),
      name: x.name
    }));
    cache.tPro = now();
  }
  let items = cache.provinces;
  if (q) {
    const n = String(q).toLowerCase();
    items = items.filter(x => x.name.toLowerCase().includes(n));
  }
  return items.sort(sortVi);
}

async function listDistricts({ province_code, q } = {}) {
  if (!province_code) throw new Error('province_code is required');
  const key = String(parseInt(province_code, 10)); // API dùng số không padding
  if (!cache.districts.has(key)) {
    const { data } = await axios.get(`${BASE}/api/p/${key}?depth=2`);
    const arr = (data?.districts || []).map(d => ({
      code: String(d.code),
      name: d.name,
      province_code: String(data.code).padStart(2, '0')
    }));
    cache.districts.set(key, arr);
  }
  let items = cache.districts.get(key);
  if (q) {
    const n = String(q).toLowerCase();
    items = items.filter(x => x.name.toLowerCase().includes(n));
  }
  return items.sort(sortVi);
}

async function listWards({ district_code, q } = {}) {
  if (!district_code) throw new Error('district_code is required');
  const key = String(parseInt(district_code, 10));
  if (!cache.wards.has(key)) {
    const { data } = await axios.get(`${BASE}/api/d/${key}?depth=2`);
    const arr = (data?.wards || []).map(w => ({
      code: String(w.code),
      name: w.name,
      district_code: String(data.code)
    }));
    cache.wards.set(key, arr);
  }
  let items = cache.wards.get(key);
  if (q) {
    const n = String(q).toLowerCase();
    items = items.filter(x => x.name.toLowerCase().includes(n));
  }
  return items.sort(sortVi);
}

module.exports = { listProvinces, listDistricts, listWards };