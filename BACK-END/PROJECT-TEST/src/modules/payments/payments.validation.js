const Joi = require('joi');
const momoCreate = Joi.object({ appointmentId: Joi.string().required() });
module.exports = { momoCreate };
