// src/modules/users/users.service.js
const prisma = require('../../config/db');


async function me(userId) {
return prisma.user.findUnique({ where: { id: userId }, select: { id: true, email: true, fullName: true, role: true } });
}


module.exports = { me };