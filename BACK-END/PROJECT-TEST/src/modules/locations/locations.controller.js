const R = require('../../utils/apiResponse');
const svc = require('./locations.service');

async function provinces(req, res) {
  const data = await svc.listProvinces({ q: req.query.q });
  return R.ok(res, data);
}
async function districts(req, res) {
  const { province_code, q } = req.query;
  if (!province_code) return R.badRequest(res, 'province_code is required');
  const data = await svc.listDistricts({ province_code, q });
  return R.ok(res, data);
}
async function wards(req, res) {
  const { district_code, q } = req.query;
  if (!district_code) return R.badRequest(res, 'district_code is required');
  const data = await svc.listWards({ district_code, q });
  return R.ok(res, data);
}

module.exports = { provinces, districts, wards };