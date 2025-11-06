const Joi = require('joi');

// Regex CUID (Prisma): "c" + 24 ký tự chữ thường hoặc số
const cuid = Joi.string().pattern(/^c[0-9a-z]{24}$/);

const available = Joi.object({
  doctorProfileId: cuid.required(), // nếu muốn nới lỏng, đổi thành Joi.string().required()
  day: Joi.string().pattern(/^\d{4}-\d{2}-\d{2}$/).required(),
});

const book = Joi.object({
  slotId: Joi.string().required(),           // id slot có thể là cuid, nhưng nếu DB có thể khác thì để string
  service: Joi.string().min(2).required(),
  careProfileId: Joi.string().optional(),    // nếu careProfile.id là cuid, có thể dùng cuid.optional()
});

const cancel = Joi.object({
  reason: Joi.string().allow('', null),
});

const calendar = Joi.object({
  month: Joi.string().pattern(/^\d{4}-\d{2}$/).required(),
  specialty: Joi.string().allow('', null),
});

module.exports = { available, book, cancel, calendar };
