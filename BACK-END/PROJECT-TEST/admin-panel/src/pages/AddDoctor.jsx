import { useState } from 'react';
import { adminAPI } from '../services/api';
import { Card, Button, Input, Select, Alert } from '../components/common';

const SPECIALTIES = [
  'Tim mạch',
  'Thần kinh',
  'Nhi khoa',
  'Sản phụ khoa',
  'Da liễu',
  'Mắt',
  'Tai mũi họng',
  'Nội khoa tổng quát',
  'Ngoại khoa',
  'Chỉnh hình',
  'Răng hàm mặt',
  'Tiêu hóa',
  'Hô hấp',
  'Thận - Tiết niệu',
  'Nội tiết',
  'Ung bướu',
  'Phục hồi chức năng',
  'Y học cổ truyền',
  'Dinh dưỡng',
  'Tâm thần',
];

export const AddDoctor = () => {
  const [formData, setFormData] = useState({
    email: '',
    password: '',
    fullName: '',
    phone: '',
    specialty: '',
    yearsExperience: '',
    clinicName: '',
    bio: '',
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setMessage({ type: '', text: '' });

    try {
      const data = {
        ...formData,
        yearsExperience: formData.yearsExperience ? parseInt(formData.yearsExperience) : undefined,
      };
      await adminAPI.createDoctor(data);
      setMessage({ type: 'success', text: 'Doctor created successfully!' });
      setFormData({
        email: '',
        password: '',
        fullName: '',
        phone: '',
        specialty: '',
        yearsExperience: '',
        clinicName: '',
        bio: '',
      });
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to create doctor',
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl">
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Add New Doctor</h1>

      {message.text && (
        <div className="mb-4">
          <Alert
            type={message.type}
            message={message.text}
            onClose={() => setMessage({ type: '', text: '' })}
          />
        </div>
      )}

      <Card>
        <form onSubmit={handleSubmit} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Email"
              type="email"
              required
              value={formData.email}
              onChange={(e) => setFormData({ ...formData, email: e.target.value })}
            />
            <Input
              label="Password"
              type="password"
              required
              value={formData.password}
              onChange={(e) => setFormData({ ...formData, password: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Full Name"
              required
              value={formData.fullName}
              onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
            />
            <Input
              label="Phone"
              value={formData.phone}
              onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <Select
              label="Chuyên khoa"
              required
              value={formData.specialty}
              onChange={(e) => setFormData({ ...formData, specialty: e.target.value })}
              options={[
                { value: '', label: 'Chọn chuyên khoa...' },
                ...SPECIALTIES.map((specialty) => ({
                  value: specialty,
                  label: specialty,
                })),
              ]}
            />
            <Input
              label="Số năm kinh nghiệm"
              type="number"
              value={formData.yearsExperience}
              onChange={(e) => setFormData({ ...formData, yearsExperience: e.target.value })}
            />
          </div>

          <Input
            label="Clinic Name"
            value={formData.clinicName}
            onChange={(e) => setFormData({ ...formData, clinicName: e.target.value })}
          />

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Bio</label>
            <textarea
              className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-transparent"
              rows="4"
              value={formData.bio}
              onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
              placeholder="Brief biography..."
            />
          </div>

          <Button type="submit" loading={loading} className="w-full">
            Create Doctor
          </Button>
        </form>
      </Card>
    </div>
  );
};
