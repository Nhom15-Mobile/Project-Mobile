const adminService = require('./admin.service');

class AdminController {
  // ============= USER MANAGEMENT =============
  async createUser(req, res) {
    try {
      const user = await adminService.createUser(req.body);
      res.status(201).json({ success: true, data: user });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async getAllUsers(req, res) {
    try {
      const result = await adminService.getAllUsers(req.query);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(500).json({ success: false, message: error.message });
    }
  }

  async deleteUser(req, res) {
    try {
      const result = await adminService.deleteUser(req.params.id);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  // ============= DOCTOR MANAGEMENT =============
  async createDoctor(req, res) {
    try {
      const doctor = await adminService.createDoctor(req.body);
      res.status(201).json({ success: true, data: doctor });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async getAllDoctors(req, res) {
    try {
      const result = await adminService.getAllDoctors(req.query);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(500).json({ success: false, message: error.message });
    }
  }

  // ============= CARE PROFILE MANAGEMENT =============
  async createCareProfile(req, res) {
    try {
      const careProfile = await adminService.createCareProfile(req.body);
      res.status(201).json({ success: true, data: careProfile });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async getAllCareProfiles(req, res) {
    try {
      const result = await adminService.getAllCareProfiles(req.query);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(500).json({ success: false, message: error.message });
    }
  }

  // ============= DOCTOR SLOT MANAGEMENT =============
  async createDoctorSlot(req, res) {
    try {
      const slot = await adminService.createDoctorSlot(req.body);
      res.status(201).json({ success: true, data: slot });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async getDoctorSlots(req, res) {
    try {
      const result = await adminService.getDoctorSlots(req.query);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(500).json({ success: false, message: error.message });
    }
  }

  async deleteDoctorSlot(req, res) {
    try {
      const result = await adminService.deleteDoctorSlot(req.params.id);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  // ============= APPOINTMENT MANAGEMENT =============
  async createAppointment(req, res) {
    try {
      const appointment = await adminService.createAppointment(req.body);
      res.status(201).json({ success: true, data: appointment });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async getAllAppointments(req, res) {
    try {
      const result = await adminService.getAllAppointments(req.query);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(500).json({ success: false, message: error.message });
    }
  }

  async updateAppointmentStatus(req, res) {
    try {
      const { status } = req.body;
      const result = await adminService.updateAppointmentStatus(req.params.id, status);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async updateAppointmentPaymentStatus(req, res) {
    try {
      const { paymentStatus } = req.body;
      const result = await adminService.updateAppointmentPaymentStatus(req.params.id, paymentStatus);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  async deleteAppointment(req, res) {
    try {
      const result = await adminService.deleteAppointment(req.params.id);
      res.json({ success: true, data: result });
    } catch (error) {
      res.status(400).json({ success: false, message: error.message });
    }
  }

  // ============= STATISTICS =============
  async getStatistics(req, res) {
    try {
      const stats = await adminService.getStatistics();
      res.json({ success: true, data: stats });
    } catch (error) {
      res.status(500).json({ success: false, message: error.message });
    }
  }
}

module.exports = new AdminController();
