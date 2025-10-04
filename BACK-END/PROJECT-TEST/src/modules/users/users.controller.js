// src/modules/users/users.controller.js
const R = require('../../utils/apiResponse');
const svc = require('./users.service');


async function me(req, res) {
const data = await svc.me(req.user.id);
return R.ok(res, data);
}


module.exports = { me };