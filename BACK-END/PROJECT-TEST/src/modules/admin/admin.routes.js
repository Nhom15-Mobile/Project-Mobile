const express = require('express');
const adminController = require('./admin.controller');
const auth = require('../../middlewares/auth');
const { allow } = require('../../middlewares/role');

const router = express.Router();

// Apply auth and admin role check to all admin routes
router.use(auth);
router.use(allow('ADMIN'));

// Statistics
router.get('/statistics', adminController.getStatistics);

// User Management
router.post('/users', adminController.createUser);
router.get('/users', adminController.getAllUsers);
router.delete('/users/:id', adminController.deleteUser);

// Doctor Management
router.post('/doctors', adminController.createDoctor);
router.get('/doctors', adminController.getAllDoctors);

// Care Profile Management
router.post('/care-profiles', adminController.createCareProfile);
router.get('/care-profiles', adminController.getAllCareProfiles);

// Doctor Slot Management
router.post('/doctor-slots', adminController.createDoctorSlot);
router.get('/doctor-slots', adminController.getDoctorSlots);
router.delete('/doctor-slots/:id', adminController.deleteDoctorSlot);

// Appointment Management
router.post('/appointments', adminController.createAppointment);
router.get('/appointments', adminController.getAllAppointments);
router.patch(
  '/appointments/:id/status',
  adminController.updateAppointmentStatus
);
router.patch(
  '/appointments/:id/payment-status',
  adminController.updateAppointmentPaymentStatus
);
router.delete('/appointments/:id', adminController.deleteAppointment);

// >>> NEW: cập nhật kết quả khám (không thay đổi status) <<<
router.patch(
  '/appointments/:id/result',
  adminController.setAppointmentResult
);

module.exports = router;
