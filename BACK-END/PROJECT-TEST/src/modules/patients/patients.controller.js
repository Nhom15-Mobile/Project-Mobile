// src/modules/patients/patients.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./patients.service');
const { validateUpsertProfile } = require('./patients.validation');

async function getProfile(req, res) {
  try {
    const profile = await svc.getProfile(req.user.id);
    return R.ok(res, profile || {});
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function updateProfile(req, res) {
  try {
    const { error, data } = validateUpsertProfile(req.body);
    if (error) return R.badRequest(res, error);

    const profile = await svc.upsertProfile(req.user.id, data);
    return R.ok(res, profile, 'Profile saved');
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

async function listAppointments(req, res) {
  try {
    const items = await svc.getAppointments(req.user.id);
    return R.ok(res, items);
  } catch (e) {
    console.error(e);
    return R.badRequest(res, e.message || 'Bad request');
  }
}

module.exports = { getProfile, updateProfile, listAppointments };