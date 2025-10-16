// src/modules/patients/patients.validation.js

// Helpers
function isEmpty(v) {
  return v === undefined || v === null || (typeof v === 'string' && v.trim() === '');
}
function trimOrNull(v) {
  if (v == null) return null;
  if (typeof v === 'string') {
    const t = v.trim();
    return t === '' ? null : t;
  }
  return v;
}
function parseISODate(d) {
  if (d == null || d === '') return null;
  if (d instanceof Date) return d;
  // chấp nhận "YYYY-MM-DD" hoặc ISO
  if (typeof d === 'string') {
    const s = d.trim();
    // YYYY-MM-DD -> gắn T00:00:00+07:00 để TZ ổn định
    const m = /^(\d{4})-(\d{2})-(\d{2})$/.exec(s);
    if (m) return new Date(`${s}T00:00:00+07:00`);
    // ISO string
    const dt = new Date(s);
    if (!isNaN(dt)) return dt;
  }
  const dt = new Date(d);
  if (isNaN(dt)) throw new Error('dob: invalid date format');
  return dt;
}
function parseJsonMaybe(v, fieldName) {
  if (v == null) return null;
  if (typeof v === 'string') {
    const s = v.trim();
    if (s === '') return null;
    try {
      return JSON.parse(s);
    } catch (e) {
      throw new Error(`${fieldName}: must be valid JSON string`);
    }
  }
  // cho phép object/array
  if (typeof v === 'object') return v;
  throw new Error(`${fieldName}: must be object/array or JSON string`);
}

// Validate for POST /profile (upsert)
function validateUpsertProfile(body = {}) {
  // whitelist
  const allowed = [
    'dob','gender','medicalHistory','medications','allergies',
    'insuranceNumber','address','emergencyContact'
  ];
  const src = {};
  for (const k of allowed) if (Object.prototype.hasOwnProperty.call(body, k)) src[k] = body[k];

  const out = {};

  // dob
  if (!isEmpty(src.dob)) {
    try {
      out.dob = parseISODate(src.dob);
    } catch (e) {
      return { error: e.message };
    }
  }

  // gender: cho phép string tự do, nhưng trim & empty -> null
  if (!isEmpty(src.gender)) {
    if (typeof src.gender !== 'string') return { error: 'gender: must be a string' };
    out.gender = trimOrNull(src.gender);
  }

  // JSON fields: medicalHistory, medications, allergies
  try {
    if (!isEmpty(src.medicalHistory)) out.medicalHistory = parseJsonMaybe(src.medicalHistory, 'medicalHistory');
    if (!isEmpty(src.medications))    out.medications    = parseJsonMaybe(src.medications, 'medications');
    if (!isEmpty(src.allergies))      out.allergies      = parseJsonMaybe(src.allergies, 'allergies');
  } catch (e) {
    return { error: e.message };
  }

  // strings
  if (!isEmpty(src.insuranceNumber))  out.insuranceNumber = trimOrNull(src.insuranceNumber);
  if (!isEmpty(src.address))          out.address         = trimOrNull(src.address);
  if (!isEmpty(src.emergencyContact)) out.emergencyContact= trimOrNull(src.emergencyContact);

  return { data: out };
}

module.exports = { validateUpsertProfile };