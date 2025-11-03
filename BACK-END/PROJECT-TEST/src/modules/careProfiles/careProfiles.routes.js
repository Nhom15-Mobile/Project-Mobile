const router = require('express').Router();
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');
const ctrl = require('./careProfiles.controller');
const validate = require('../../middlewares/validate');
const v = require('./careProfiles.validation');
router.use(auth, allow('PATIENT','ADMIN'));
router.get('/', ctrl.list);
router.post('/', ctrl.create);
router.put('/:id', ctrl.update);
router.delete('/:id', ctrl.remove);

module.exports = router;