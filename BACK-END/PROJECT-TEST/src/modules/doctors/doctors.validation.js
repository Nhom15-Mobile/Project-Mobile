// src/modules/doctors/doctors.validation.js
const config = require('../../config/env');

const allowedKeys = ['specialty', 'bio', 'yearsExperience', 'clinicName', 'rating'];
const ALLOWED_SPECIALTIES = config.specialties.map(s => s.name);

function validateUpdateProfile(body = {}) {
  const data = {};
  for (const k of allowedKeys) {
    if (Object.prototype.hasOwnProperty.call(body, k) && body[k] !== undefined) {
      data[k] = body[k];
    }
  }

  // specialty: bắt buộc thuộc whitelist nếu có gửi lên
  if (data.specialty != null) {
    if (typeof data.specialty !== 'string' || data.specialty.length < 2) {
      return { error: 'specialty: must be a string with length >= 2' };
    }
    if (!ALLOWED_SPECIALTIES.includes(data.specialty)) {
      return { error: `specialty: must be one of [${ALLOWED_SPECIALTIES.join(', ')}]` };
    }
  }

  if (data.bio != null && typeof data.bio !== 'string') {
    return { error: 'bio: must be a string' };
  }
  if (data.yearsExperience != null) {
    const n = Number(data.yearsExperience);
    if (!Number.isInteger(n) || n < 0 || n > 80) return { error: 'yearsExperience: must be an integer 0..80' };
    data.yearsExperience = n;
  }
  if (data.clinicName != null && typeof data.clinicName !== 'string') {
    return { error: 'clinicName: must be a string' };
  }
  if (data.rating != null) {
    const r = Number(data.rating);
    if (Number.isNaN(r) || r < 0 || r > 5) return { error: 'rating: must be a number 0..5' };
    data.rating = r;
  }

  return { data };
}

module.exports = { validateUpdateProfile };