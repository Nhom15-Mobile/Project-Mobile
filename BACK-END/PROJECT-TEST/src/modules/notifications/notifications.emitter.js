// src/modules/notifications/notifications.emitter.js
const { EventEmitter } = require('events');
const emitter = new EventEmitter();
// tăng max listeners nếu nhiều user
emitter.setMaxListeners(0);
module.exports = emitter;