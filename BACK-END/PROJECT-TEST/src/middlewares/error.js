// src/middlewares/error.js
const R = require('../utils/apiResponse');


function notFound(req, res) {
return R.notFound(res, 'Route not found');
}


function errorHandler(err, req, res, next) {
console.error(err);
return R.error(res, err.message || 'Server error');
}


module.exports = { notFound, errorHandler };