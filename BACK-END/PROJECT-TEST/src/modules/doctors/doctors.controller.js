// src/modules/doctors/doctors.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./doctors.service');


async function search(req, res) {
const result = await svc.search(req.query);
return R.ok(res, result);
}


async function getOne(req, res) {
const item = await svc.getById(req.params.id);
if (!item) return R.notFound(res, 'Doctor not found');
return R.ok(res, item);
}


module.exports = { search, getOne };