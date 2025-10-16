const R = require('../../utils/apiResponse');
const svc = require('./careProfiles.service');

async function list(req, res) {
  const data = await svc.listMine(req.user.id);
  return R.ok(res, data);
}

async function create(req, res) {
  const data = await svc.create(req.user.id, req.body);
  return R.created(res, data, 'Care profile created');
}

async function update(req, res) {
  const data = await svc.update(req.user.id, req.params.id, req.body);
  return R.ok(res, data, 'Care profile updated');
}

async function remove(req, res) {
  await svc.remove(req.user.id, req.params.id);
  return R.ok(res, { ok: true }, 'Care profile removed');
}

module.exports = { list, create, update, remove };