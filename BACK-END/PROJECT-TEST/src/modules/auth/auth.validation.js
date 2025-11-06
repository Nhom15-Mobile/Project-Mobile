const Joi = require('joi');

const login = Joi.object({
  email: Joi.string().email().required(),
  password: Joi.string().min(6).required(),
});

const register = Joi.object({
  email: Joi.string().email().required(),
  password: Joi.string().min(6).required(),
  fullName: Joi.string().min(2).required(),
  role: Joi.string().valid('PATIENT','DOCTOR','ADMIN').default('PATIENT'),
  specialty: Joi.string().min(2).when('role', { is: 'DOCTOR', then: Joi.required(), otherwise: Joi.forbidden() }),
});

const requestReset = Joi.object({ email: Joi.string().email().required() });
const reset = Joi.object({
  email: Joi.string().email().required(),
  code: Joi.string().length(6).required(),
  newPassword: Joi.string().min(6).required(),
});

module.exports = { login, register, requestReset, reset };
