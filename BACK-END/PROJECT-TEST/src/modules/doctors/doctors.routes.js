// src/modules/doctors/doctors.routes.js
const router = require('express').Router();
const ctrl = require('./doctors.controller');


router.get('/', ctrl.search); // open for browsing in app
router.get('/:id', ctrl.getOne);


module.exports = router;