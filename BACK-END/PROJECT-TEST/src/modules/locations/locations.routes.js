const router = require('express').Router();
const ctrl = require('./locations.controller');

// public
router.get('/provinces', ctrl.provinces);
router.get('/districts', ctrl.districts);
router.get('/wards', ctrl.wards);

module.exports = router;