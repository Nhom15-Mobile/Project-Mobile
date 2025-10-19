// src/modules/auth/auth.service.js
const prisma = require('../../config/db');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const config = require('../../config/env');

async function login(email, password) {
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) return null;
  const ok = await bcrypt.compare(password, user.password);
  if (!ok) return null;

  const token = jwt.sign({ sub: user.id, role: user.role }, config.jwt.secret, { expiresIn: config.jwt.expires });
  return { token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } };
}

async function register({ email, password, fullName, role = 'PATIENT', specialty }) {
  // 1) Email phải unique
  const exists = await prisma.user.findUnique({ where: { email } });
  if (exists) {
    const err = new Error('Email already registered');
    err.status = 409;
    throw err;
  }

  // 2) Tạo user
  const hashed = await bcrypt.hash(password, 10);
  const user = await prisma.user.create({
    data: { email, password: hashed, fullName, role }
  });

  // 3) Tạo profile theo role
  if (role === 'PATIENT') {
    await prisma.patientProfile.create({ data: { userId: user.id } });
  } else if (role === 'DOCTOR') {
    // ép chọn 1 trong 10 chuyên khoa cố định
    const allowed = config.specialties.map(s => s.name);
    const chosen = allowed.includes(specialty) ? specialty : allowed[0];

    await prisma.doctorProfile.create({
      data: { userId: user.id, specialty: chosen }
    });
  }

  // 4) Trả token để đăng nhập luôn
  const token = jwt.sign({ sub: user.id, role: user.role }, config.jwt.secret, { expiresIn: config.jwt.expires });
  return { token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } };
}

// Tạo mã 6 số
function gen6() {
  return Math.floor(100000 + Math.random() * 900000).toString(); // 6 digits
}

// Gửi (log) mã — sau này thay bằng gửi email thực
async function sendResetCodeDev(email, code) {
  console.log(`[RESET CODE] email=${email} code=${code}`);
}

async function requestPasswordReset(email) {
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) return { ok: true };

  await prisma.passwordReset.updateMany({
    where: { userId: user.id, usedAt: null, expiresAt: { gt: new Date() } },
    data: { expiresAt: new Date() }
  });

  const code = gen6();
  const codeHash = await bcrypt.hash(code, 10);
  const expiresAt = new Date(Date.now() + 10 * 60 * 1000);

  await prisma.passwordReset.create({
    data: { userId: user.id, codeHash, expiresAt }
  });

  await sendResetCodeDev(email, code);
  return { ok: true };
}

async function resetPassword({ email, code, newPassword }) {
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) return { ok: true };

  const pr = await prisma.passwordReset.findFirst({
    where: { userId: user.id, usedAt: null, expiresAt: { gt: new Date() } },
    orderBy: { createdAt: 'desc' }
  });

  if (!pr) {
    const err = new Error('Invalid or expired code');
    err.status = 400;
    throw err;
  }

  const match = await bcrypt.compare(code, pr.codeHash);
  if (!match) {
    const err = new Error('Invalid or expired code');
    err.status = 400;
    throw err;
  }

  const newHash = await bcrypt.hash(newPassword, 10);

  await prisma.$transaction([
    prisma.passwordReset.update({ where: { id: pr.id }, data: { usedAt: new Date() } }),
    prisma.user.update({ where: { id: user.id }, data: { password: newHash } })
  ]);

  return { ok: true };
}

module.exports = { login, register, requestPasswordReset, resetPassword };