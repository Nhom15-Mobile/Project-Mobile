// src/modules/users/users.routes.js
const router = require('express').Router();
const auth = require('../../middlewares/auth');
const ctrl = require('./users.controller');


router.get('/me', auth, ctrl.me);
router.get('/', auth, ctrl.getAll);
router.get('/:id', auth, ctrl.getById);


module.exports = router;