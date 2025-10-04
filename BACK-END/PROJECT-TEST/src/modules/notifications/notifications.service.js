// src/modules/notifications/notifications.service.js
const prisma = require('../../config/db');


async function list(userId) {
return prisma.notification.findMany({ where: { userId }, orderBy: { createdAt: 'desc' } });
}


async function create({ userId, type, title, body, data }) {
return prisma.notification.create({ data: { userId, type, title, body, data } });
}


module.exports = { list, create };