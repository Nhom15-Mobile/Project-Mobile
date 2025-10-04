// src/utils/logger.js
module.exports = {
log: (...args) => console.log('[LOG]', ...args),
error: (...args) => console.error('[ERR]', ...args)
};