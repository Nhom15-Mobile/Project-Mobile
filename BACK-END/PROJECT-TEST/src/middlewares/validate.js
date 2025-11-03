// Generic Joi validator
module.exports = (schema, where = 'body') => (req, res, next) => {
  const data = req[where];
  const { error, value } = schema.validate(data, { abortEarly: false, stripUnknown: true });
  if (error) {
    const details = error.details.map(d => d.message);
    return res.status(422).json({ success: false, message: 'Validation error', details });
  }
  req[where] = value; // đã strip các field thừa
  next();
};

