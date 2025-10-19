// src/app.js
require('express-async-errors');
const express = require('express');
const cors = require('cors');
const helmet = require('helmet');
const morgan = require('morgan');
const cookieParser = require('cookie-parser');
const { notFound, errorHandler } = require('./middlewares/error');

// Routers
const authRoutes = require('./modules/auth/auth.routes');
const patientRoutes = require('./modules/patients/patients.routes');
const doctorRoutes = require('./modules/doctors/doctors.routes');
const apptRoutes = require('./modules/appointments/appointments.routes');
const paymentRoutes = require('./modules/payments/payments.routes');
const notifRoutes = require('./modules/notifications/notifications.routes');
const userRoutes = require('./modules/users/users.routes');
const careProfileRoutes = require('./modules/careProfiles/careProfiles.routes');
const locationRoutes = require('./modules/locations/locations.routes');

const app = express();

// Core middlewares
app.use(helmet());
app.use(cors({ origin: true, credentials: true }));
app.use(morgan('dev'));
app.use(cookieParser());
app.use(express.json({ limit: '1mb' }));

// Health
app.get('/api/health', (req, res) => res.json({ ok: true }));

// Mount routes
app.use('/api/auth', authRoutes);
app.use('/api/patient', patientRoutes);
app.use('/api/doctors', doctorRoutes);
app.use('/api/appointments', apptRoutes);
app.use('/api/payments', paymentRoutes);
app.use('/api/notifications', notifRoutes);
app.use('/api/users', userRoutes);
app.use('/api/care-profiles', careProfileRoutes);
app.use('/api/locations', locationRoutes);

// Errors (always last)
app.use(notFound);
app.use(errorHandler);

module.exports = app;