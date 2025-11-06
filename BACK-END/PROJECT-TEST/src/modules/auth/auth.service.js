// src/modules/auth/auth.service.js
const prisma = require('../../config/db');
const bcrypt = require('bcryptjs');
const jwt = require('jsonwebtoken');
const config = require('../../config/env');
const mailer = require('../../utils/mailer'); // <<< ADD

async function login(email, password) {
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) return null;
  const ok = await bcrypt.compare(password, user.password);
  if (!ok) return null;

  const token = jwt.sign({ sub: user.id, role: user.role }, config.jwt.secret, { expiresIn: config.jwt.expires });
  return { token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } };
}

async function register({ email, password, fullName, role = 'PATIENT', specialty }) {
  const exists = await prisma.user.findUnique({ where: { email } });
  if (exists) {
    const err = new Error('Email already registered');
    err.status = 409;
    throw err;
  }

  const hashed = await bcrypt.hash(password, 10);
  const user = await prisma.user.create({
    data: { email, password: hashed, fullName, role }
  });

  if (role === 'PATIENT') {
    await prisma.patientProfile.create({ data: { userId: user.id } });
  } else if (role === 'DOCTOR') {
    const allowed = config.specialties.map(s => s.name);
    const chosen = allowed.includes(specialty) ? specialty : allowed[0];
    await prisma.doctorProfile.create({ data: { userId: user.id, specialty: chosen } });
  }

  const token = jwt.sign({ sub: user.id, role: user.role }, config.jwt.secret, { expiresIn: config.jwt.expires });
  return { token, user: { id: user.id, email: user.email, fullName: user.fullName, role: user.role } };
}

// ===== Reset Password (Email code) =====

// 6 digits
function gen6() {
  return Math.floor(100000 + Math.random() * 900000).toString();
}

async function sendResetCodeEmail(email, code) {
  const subject = 'Mã đặt lại mật khẩu - UIT Healthcare';
  const text = `Mã xác nhận đặt lại mật khẩu của bạn là: ${code}. Mã có hiệu lực trong 10 phút.`;
  const html = `
    <div style="font-family:Arial,sans-serif;line-height:1.6">
      <h2>Đặt lại mật khẩu</h2>
      <p>Xin chào <b>${email}</b>,</p>
      <p>Mã xác nhận của bạn là:</p>
      <div style="font-size:28px;font-weight:700;letter-spacing:3px;margin:8px 0 14px">${code}</div>
      <p>Mã có hiệu lực trong <b>10 phút</b>. Nếu bạn không yêu cầu, vui lòng bỏ qua email này.</p>
      <hr/>
      <small>UIT Healthcare</small>
    </div>
  `;
  await mailer.sendMail({ to: email, subject, text, html });
}

async function requestPasswordReset(email) {
  const user = await prisma.user.findUnique({ where: { email } });
  if (!user) {
    // Không tiết lộ tồn tại hay không
    return { ok: true };
  }

  // Hủy hiệu lực các mã cũ còn hạn
  await prisma.passwordReset.updateMany({
    where: { userId: user.id, usedAt: null, expiresAt: { gt: new Date() } },
    data: { expiresAt: new Date() } // expire ngay
  });

  const code = gen6();
  const codeHash = await bcrypt.hash(code, 10);
  const expiresAt = new Date(Date.now() + 10 * 60 * 1000); // 10 phút

  await prisma.passwordReset.create({
    data: { userId: user.id, codeHash, expiresAt }
  });

  // Gửi email
  try {
    await sendResetCodeEmail(email, code);
  } catch (e) {
    console.error('Send reset mail error:', e?.response?.data || e.message);
    // Không ném lỗi ra ngoài để tránh lộ thông tin. Vẫn trả OK.
  }

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
