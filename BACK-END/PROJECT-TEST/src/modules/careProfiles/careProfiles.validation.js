// Các field bắt buộc theo UI (dấu *)
// fullName, phone, country, gender, dob, province, district, ward, address, relation
function isISODate(s) {
  if (typeof s !== 'string') return false;
  const d = new Date(s);
  return !Number.isNaN(d.valueOf()) && /^\d{4}-\d{2}-\d{2}/.test(s);
}

function validateCreate(body = {}) {
  const required = ['fullName', 'relation', 'phone', 'country', 'gender', 'dob', 'province', 'district', 'ward', 'address'];
  const errors = [];

  // Nếu FE gửi mã (provinceCode/districtCode/wardCode) thì có thể bỏ province/district/ward tên,
  // nhưng tối thiểu phải có bộ mã hợp lệ => service sẽ resolve ra tên.
  const hasCodes = body.provinceCode || body.districtCode || body.wardCode;

  for (const k of required) {
    if (['province', 'district', 'ward'].includes(k) && hasCodes) continue; // sẽ fill từ code
    if (body[k] == null || String(body[k]).trim() === '') errors.push(`${k} is required`);
  }

  if (body.dob && !isISODate(body.dob)) errors.push('dob must be ISO date (YYYY-MM-DD)');
  if (body.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(body.email)) errors.push('email is invalid');
  if (body.phone && !/^[0-9+\s\-().]{6,20}$/.test(body.phone)) errors.push('phone is invalid');

  return { ok: errors.length === 0, errors };
}

function validateUpdate(body = {}) {
  const errors = [];
  if (body.dob && !isISODate(body.dob)) errors.push('dob must be ISO date (YYYY-MM-DD)');
  if (body.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(body.email)) errors.push('email is invalid');
  if (body.phone && !/^[0-9+\s\-().]{6,20}$/.test(body.phone)) errors.push('phone is invalid');
  return { ok: errors.length === 0, errors };
}

module.exports = { validateCreate, validateUpdate };