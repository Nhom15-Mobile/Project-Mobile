// src/modules/patients/patients.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./patients.service');


async function getProfile(req, res) {
const profile = await svc.getProfile(req.user.id);
return R.ok(res, profile || {});
}


async function updateProfile(req, res) {
const allowed = [
'dob','gender','medicalHistory','medications','allergies','insuranceNumber','address','emergencyContact'
];
const payload = {};
for (const k of allowed) if (k in req.body) payload[k] = req.body[k];
const profile = await svc.upsertProfile(req.user.id, payload);
return R.ok(res, profile, 'Profile saved');
}


async function listAppointments(req, res) {
const items = await svc.getAppointments(req.user.id);
return R.ok(res, items);
}


module.exports = { getProfile, updateProfile, listAppointments };