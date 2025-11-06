// src/middlewares/auth.js
const jwt = require('jsonwebtoken');
const config = require('../config/env');
const prisma = require('../config/db');
const R = require('../utils/apiResponse');


async function auth(req, res, next) {
try {
const header = req.headers.authorization || '';
const token = header.startsWith('Bearer ') ? header.slice(7) : null;
if (!token) return R.unauthorized(res);


const payload = jwt.verify(token, config.jwt.secret);
const user = await prisma.user.findUnique({ where: { id: payload.sub } });
if (!user) return R.unauthorized(res);


req.user = user;
next();
} catch (e) {
return R.unauthorized(res);
}
}


module.exports = auth;