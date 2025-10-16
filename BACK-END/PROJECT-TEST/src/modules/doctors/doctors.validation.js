// src/modules/doctors/doctors.validation.js
// Tối giản: chỉ whitelist & kiểm tra cơ bản, không dùng zod để tránh phụ thuộc

const allowedKeys = ['specialty', 'bio', 'yearsExperience', 'clinicName', 'rating'];

function validateUpdateProfile(body = {}) {
  const data = {};
  for (const k of allowedKeys) {
    if (Object.prototype.hasOwnProperty.call(body, k) && body[k] !== undefined) {
      data[k] = body[k];
    }
  }

  // kiểm tra cơ bản theo schema DoctorProfile tối giản
  if (data.specialty != null && (typeof data.specialty !== 'string' || data.specialty.length < 2)) {
    return { error: 'specialty: must be a string with length >= 2' };
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