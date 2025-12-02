import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { Card, Alert } from '../components/common';
import {
  Users,
  UserCheck,
  Stethoscope,
  FolderHeart,
  Calendar,
  Clock,
  CheckCircle,
  DollarSign,
} from 'lucide-react';
import {
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from 'recharts';

const StatCard = ({ title, value, icon: Icon, color, subtitle }) => {
  const colors = {
    blue: 'bg-blue-500',
    green: 'bg-green-500',
    purple: 'bg-purple-500',
    orange: 'bg-orange-500',
    red: 'bg-red-500',
    indigo: 'bg-indigo-500',
    teal: 'bg-teal-500',
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-6 flex items-center space-x-4 hover:shadow-lg transition-shadow">
      <div className={`${colors[color]} p-4 rounded-full text-white`}>
        <Icon size={24} />
      </div>
      <div>
        <p className="text-gray-600 text-sm font-medium">{title}</p>
        <p className="text-3xl font-bold text-gray-900">{value}</p>
        {subtitle && <p className="text-xs text-gray-500 mt-1">{subtitle}</p>}
      </div>
    </div>
  );
};

export const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [statsRes, appointmentsRes] = await Promise.all([
        adminAPI.getStatistics(),
        adminAPI.getAppointments(),
      ]);

      setStats(statsRes.data.data || statsRes.data);
      const apptData = appointmentsRes.data.data || appointmentsRes.data;
      setAppointments(apptData.appointments || apptData || []);

      setError('');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  // Prepare chart data
  const getAppointmentStatusData = () => {
    if (!Array.isArray(appointments)) return [];

    const statusCounts = appointments.reduce((acc, apt) => {
      acc[apt.status] = (acc[apt.status] || 0) + 1;
      return acc;
    }, {});

    return Object.entries(statusCounts).map(([name, value]) => ({
      name,
      value,
    }));
  };

  const getPaymentStatusData = () => {
    if (!Array.isArray(appointments)) return [];

    const paymentCounts = appointments.reduce((acc, apt) => {
      acc[apt.paymentStatus] = (acc[apt.paymentStatus] || 0) + 1;
      return acc;
    }, {});

    return Object.entries(paymentCounts).map(([name, value]) => ({
      name,
      value,
    }));
  };

  const getUserRoleData = () => {
    return [
      { name: 'Admins', value: stats?.adminUsers || 0 },
      { name: 'Doctors', value: stats?.totalDoctors || 0 },
      { name: 'Patients', value: stats?.totalPatients || 0 },
    ];
  };

  const getSlotData = () => {
    return [
      { name: 'Available', value: stats?.availableSlots || 0 },
      { name: 'Booked', value: stats?.bookedSlots || 0 },
    ];
  };

  const COLORS = {
    PENDING: '#F59E0B',
    CONFIRMED: '#3B82F6',
    COMPLETED: '#10B981',
    CANCELLED: '#EF4444',
    PAID: '#10B981',
    UNPAID: '#EF4444',
    REFUNDED: '#8B5CF6',
  };

  const PIE_COLORS = ['#3B82F6', '#10B981', '#F59E0B', '#EF4444', '#8B5CF6', '#EC4899'];

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600"></div>
      </div>
    );
  }

  if (error) {
    return <Alert type="error" message={error} />;
  }

  const paidCount = appointments.filter(a => a.paymentStatus === 'PAID').length;
  const unpaidCount = appointments.filter(a => a.paymentStatus === 'UNPAID').length;

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-900">Dashboard</h1>
        <button
          onClick={fetchData}
          className="px-4 py-2 text-sm bg-blue-600 text-white rounded-lg hover:bg-blue-700"
        >
          Refresh
        </button>
      </div>

      {/* Main Stats Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Users"
          value={stats?.totalUsers || 0}
          icon={Users}
          color="blue"
          subtitle={`${stats?.adminUsers || 0} admins`}
        />
        <StatCard
          title="Total Doctors"
          value={stats?.totalDoctors || 0}
          icon={Stethoscope}
          color="green"
        />
        <StatCard
          title="Total Patients"
          value={stats?.totalPatients || 0}
          icon={UserCheck}
          color="purple"
        />
        <StatCard
          title="Care Profiles"
          value={stats?.totalCareProfiles || 0}
          icon={FolderHeart}
          color="orange"
        />
      </div>

      {/* Appointment Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <StatCard
          title="Total Appointments"
          value={stats?.totalAppointments || 0}
          icon={Calendar}
          color="indigo"
        />
        <StatCard
          title="Pending"
          value={stats?.pendingAppointments || 0}
          icon={Clock}
          color="orange"
        />
        <StatCard
          title="Completed"
          value={stats?.completedAppointments || 0}
          icon={CheckCircle}
          color="green"
        />
        <StatCard
          title="Paid Appointments"
          value={paidCount}
          icon={DollarSign}
          color="teal"
          subtitle={`${unpaidCount} unpaid`}
        />
      </div>

      {/* Charts Section */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Appointment Status Chart */}
        <Card>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Appointment Status</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={getAppointmentStatusData()}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" fill="#3B82F6">
                {getAppointmentStatusData().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[entry.name] || PIE_COLORS[index]} />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </Card>

        {/* Payment Status Chart */}
        <Card>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Payment Status</h2>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={getPaymentStatusData()}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }) => `${name}: ${value}`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {getPaymentStatusData().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={COLORS[entry.name] || PIE_COLORS[index]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Card>

        {/* User Roles Distribution */}
        <Card>
          <h2 className="text-xl font-bold text-gray-900 mb-4">User Roles Distribution</h2>
          <ResponsiveContainer width="100%" height={300}>
            <PieChart>
              <Pie
                data={getUserRoleData()}
                cx="50%"
                cy="50%"
                labelLine={false}
                label={({ name, value }) => `${name}: ${value}`}
                outerRadius={100}
                fill="#8884d8"
                dataKey="value"
              >
                {getUserRoleData().map((entry, index) => (
                  <Cell key={`cell-${index}`} fill={PIE_COLORS[index]} />
                ))}
              </Pie>
              <Tooltip />
            </PieChart>
          </ResponsiveContainer>
        </Card>

        {/* Doctor Slots Status */}
        <Card>
          <h2 className="text-xl font-bold text-gray-900 mb-4">Doctor Slots Status</h2>
          <ResponsiveContainer width="100%" height={300}>
            <BarChart data={getSlotData()}>
              <CartesianGrid strokeDasharray="3 3" />
              <XAxis dataKey="name" />
              <YAxis />
              <Tooltip />
              <Legend />
              <Bar dataKey="value" fill="#3B82F6">
                {getSlotData().map((entry, index) => (
                  <Cell
                    key={`cell-${index}`}
                    fill={entry.name === 'Available' ? '#10B981' : '#EF4444'}
                  />
                ))}
              </Bar>
            </BarChart>
          </ResponsiveContainer>
        </Card>
      </div>

      {/* Quick Stats Table */}
      <Card title="System Overview">
        <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <p className="text-gray-600 text-sm font-medium">Admin Users</p>
            <p className="text-3xl font-bold text-gray-900 mt-2">{stats?.adminUsers || 0}</p>
          </div>
          <div className="text-center p-4 bg-gray-50 rounded-lg">
            <p className="text-gray-600 text-sm font-medium">Doctor Slots</p>
            <p className="text-3xl font-bold text-gray-900 mt-2">{stats?.totalDoctorSlots || 0}</p>
          </div>
          <div className="text-center p-4 bg-green-50 rounded-lg">
            <p className="text-green-600 text-sm font-medium">Available Slots</p>
            <p className="text-3xl font-bold text-green-900 mt-2">{stats?.availableSlots || 0}</p>
          </div>
          <div className="text-center p-4 bg-red-50 rounded-lg">
            <p className="text-red-600 text-sm font-medium">Booked Slots</p>
            <p className="text-3xl font-bold text-red-900 mt-2">{stats?.bookedSlots || 0}</p>
          </div>
        </div>
      </Card>
    </div>
  );
};
