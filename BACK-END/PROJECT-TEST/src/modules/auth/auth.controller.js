// src/modules/auth/auth.controller.js
const R = require('../../utils/apiResponse');
const service = require('./auth.service');

async function login(req, res) {
  const { email, password } = req.body;
  if (!email || !password) return R.badRequest(res, 'Email and password are required');
  const result = await service.login(email, password);
  if (!result) return R.unauthorized(res, 'Invalid credentials');
  return R.ok(res, result, 'Login success');
}

async function register(req, res) {
  const { email, password, fullName, role, specialty } = req.body;

  if (!email || !password || !fullName) {
    return R.badRequest(res, 'email, password, fullName are required');
  }

  try {
    const data = await service.register({ email, password, fullName, role, specialty });
    return R.created(res, data, 'Register success');
  } catch (e) {
    if (e.status === 409) return R.badRequest(res, e.message);
    return R.error(res, e.message || 'Registration failed');
  }
}

async function forgotPassword(req, res) {
  const { email } = req.body;
  if (!email) return R.badRequest(res, 'email is required');

  await service.requestPasswordReset(email);
  // Luôn trả OK, không tiết lộ email có tồn tại hay không
  return R.ok(res, { sent: true }, 'If the email exists, a reset code has been sent');
}

async function resetPassword(req, res) {
  const { email, code, newPassword } = req.body;
  if (!email || !code || !newPassword) {
    return R.badRequest(res, 'email, code, newPassword are required');
  }

  try {
    await service.resetPassword({ email, code, newPassword });
    return R.ok(res, { reset: true }, 'Password has been reset');
  } catch (e) {
    const msg = e.status ? e.message : 'Reset failed';
    return e.status ? R.badRequest(res, msg) : R.error(res, msg);
  }
}

module.exports = { login, register, forgotPassword, resetPassword };