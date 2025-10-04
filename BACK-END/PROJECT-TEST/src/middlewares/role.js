// src/middlewares/role.js
const R = require('../utils/apiResponse');


function allow(...roles) {
return (req, res, next) => {
if (!req.user || !roles.includes(req.user.role)) {
return R.forbidden(res, 'Insufficient role');
}
next();
};
}


module.exports = { allow };