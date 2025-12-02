// src/modules/users/users.service.js
const prisma = require('../../config/db');


async function me(userId) {
return prisma.user.findUnique({ where: { id: userId }, select: { id: true, email: true, fullName: true, role: true } });
}

async function getAll() {
return prisma.user.findMany({
select: { id: true, email: true, fullName: true, role: true, createdAt: true, updatedAt: true }
});
}

async function getById(userId) {
return prisma.user.findUnique({
where: { id: userId },
select: { id: true, email: true, fullName: true, role: true, createdAt: true, updatedAt: true }
});
}


module.exports = { me, getAll, getById };