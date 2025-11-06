const Joi = require('joi');
const sendReminders = Joi.object({
  hoursAhead: Joi.number().integer().min(1).max(72).default(24),
  minHours: Joi.number().integer().min(0).max(48).default(3),
});
module.exports = { sendReminders };
