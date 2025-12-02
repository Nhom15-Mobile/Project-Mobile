import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { Card, Button, Alert } from '../components/common';
import { format } from 'date-fns';

export const ViewAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [statusFilter, setStatusFilter] = useState('all');   // all, pending, confirmed, cancelled, completed
  const [paymentFilter, setPaymentFilter] = useState('all'); // all, paid, unpaid

  useEffect(() => {
    fetchAppointments();
  }, []);

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const response = await adminAPI.getAppointments();
      const data = response.data.data || response.data;
      // Backend returns { data: { appointments: [], pagination: {} } }
      const appointmentsList = data.appointments || data;
      setAppointments(Array.isArray(appointmentsList) ? appointmentsList : []);
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to load appointments',
      });
    } finally {
      setLoading(false);
    }
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text);
    setMessage({ type: 'success', text: 'ID copied to clipboard!' });
    setTimeout(() => setMessage({ type: '', text: '' }), 2000);
  };

  const handleStatusChange = async (appointmentId, newStatus) => {
    try {
      await adminAPI.updateAppointmentStatus(appointmentId, newStatus);
      setMessage({ type: 'success', text: 'Appointment status updated!' });
      fetchAppointments(); // Reload data
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to update status',
      });
    }
  };

  const handleMarkAsPaid = async (appointmentId) => {
    try {
      await adminAPI.updateAppointmentPaymentStatus(appointmentId, 'PAID');
      setMessage({ type: 'success', text: 'Payment status updated to PAID!' });
      fetchAppointments(); // Reload data
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to update payment status',
      });
    }
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PENDING':
        return 'bg-yellow-100 text-yellow-800';
      case 'CONFIRMED':
        return 'bg-blue-100 text-blue-800';
      case 'CANCELLED':
        return 'bg-red-100 text-red-800';
      case 'COMPLETED':
        return 'bg-green-100 text-green-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const getPaymentStatusColor = (status) => {
    switch (status) {
      case 'PAID':
        return 'bg-green-100 text-green-800';
      case 'UNPAID':
      case 'REQUIRES_PAYMENT':   // chưa thanh toán cũng đỏ
        return 'bg-red-100 text-red-800';
      case 'REFUNDED':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // ========== FILTERED LIST ==========
  const filteredAppointments = appointments.filter((apt) => {
    // Filter by status
    if (statusFilter !== 'all' && apt.status !== statusFilter.toUpperCase()) {
      return false;
    }

    // Filter by payment status
    if (paymentFilter === 'paid') {
      // chỉ lấy PAID
      return apt.paymentStatus === 'PAID';
    }

    if (paymentFilter === 'unpaid') {
      // cứ KHÔNG phải PAID (kể cả null) thì coi là unpaid
      return apt.paymentStatus !== 'PAID';
    }

    // paymentFilter === 'all'
    return true;
  });

  // Count cho nút filter
  const paidCount = appointments.filter(a => a.paymentStatus === 'PAID').length;
  const unpaidCount = appointments.filter(a => a.paymentStatus !== 'PAID').length;

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Appointments</h1>
        <Button onClick={fetchAppointments}>Refresh</Button>
      </div>

      {message.text && (
        <div className="mb-4">
          <Alert
            type={message.type}
            message={message.text}
            onClose={() => setMessage({ type: '', text: '' })}
          />
        </div>
      )}

      <div className="mb-4">
        <div className="mb-2">
          <p className="text-sm font-medium text-gray-700 mb-2">Filter by Status:</p>
          <div className="flex gap-2">
            <Button
              variant={statusFilter === 'all' ? 'primary' : 'outline'}
              onClick={() => setStatusFilter('all')}
            >
              All ({appointments.length})
            </Button>
            <Button
              variant={statusFilter === 'pending' ? 'primary' : 'outline'}
              onClick={() => setStatusFilter('pending')}
            >
              Pending ({appointments.filter(a => a.status === 'PENDING').length})
            </Button>
            <Button
              variant={statusFilter === 'confirmed' ? 'primary' : 'outline'}
              onClick={() => setStatusFilter('confirmed')}
            >
              Confirmed ({appointments.filter(a => a.status === 'CONFIRMED').length})
            </Button>
            <Button
              variant={statusFilter === 'completed' ? 'primary' : 'outline'}
              onClick={() => setStatusFilter('completed')}
            >
              Completed ({appointments.filter(a => a.status === 'COMPLETED').length})
            </Button>
            <Button
              variant={statusFilter === 'cancelled' ? 'danger' : 'outline'}
              onClick={() => setStatusFilter('cancelled')}
            >
              Cancelled ({appointments.filter(a => a.status === 'CANCELLED').length})
            </Button>
          </div>
        </div>

        <div>
          <p className="text-sm font-medium text-gray-700 mb-2">Filter by Payment:</p>
          <div className="flex gap-2">
            <Button
              variant={paymentFilter === 'all' ? 'primary' : 'outline'}
              onClick={() => setPaymentFilter('all')}
            >
              All ({appointments.length})
            </Button>
            <Button
              variant={paymentFilter === 'paid' ? 'success' : 'outline'}
              onClick={() => setPaymentFilter('paid')}
            >
              Paid ({paidCount})
            </Button>
            <Button
              variant={paymentFilter === 'unpaid' ? 'danger' : 'outline'}
              onClick={() => setPaymentFilter('unpaid')}
            >
              Unpaid ({unpaidCount})
            </Button>
          </div>
        </div>
      </div>

      <Card>
        {loading ? (
          <div className="flex justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead className="bg-gray-50 border-b">
                <tr>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Patient</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Doctor</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Care Profile</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Service</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Scheduled</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Status</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Payment</th>
                  <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y">
                {filteredAppointments.map((appointment) => (
                  <tr key={appointment.id} className="hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm font-mono">
                      <button
                        onClick={() => copyToClipboard(appointment.id)}
                        className="text-blue-600 hover:text-blue-800 hover:underline"
                        title="Click to copy"
                      >
                        {appointment.id.slice(0, 8)}...
                      </button>
                    </td>
                    <td className="px-4 py-3 text-sm">
                      <div className="font-medium text-gray-900">
                        {appointment.patient?.fullName || 'Unknown'}
                      </div>
                      <div className="text-gray-500 text-xs">
                        {appointment.patient?.email || '-'}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm">
                      <div className="font-medium text-gray-900">
                        {appointment.doctor?.fullName || 'Unknown'}
                      </div>
                      <div className="text-gray-500 text-xs">
                        {appointment.doctor?.email || '-'}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm">
                      <div className="font-medium text-gray-900">
                        {appointment.careProfile?.fullName || 'N/A'}
                      </div>
                      <div className="text-gray-500 text-xs">
                        {appointment.careProfile?.relation || '-'}
                      </div>
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">
                      {appointment.service || '-'}
                    </td>
                    <td className="px-4 py-3 text-sm text-gray-600">
                      {appointment.scheduledAt
                        ? format(new Date(appointment.scheduledAt), 'MMM dd, yyyy HH:mm')
                        : '-'}
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`px-2 py-1 text-xs rounded-full ${getStatusColor(
                          appointment.status
                        )}`}
                      >
                        {appointment.status}
                      </span>
                    </td>
                    <td className="px-4 py-3">
                      <span
                        className={`px-2 py-1 text-xs rounded-full ${getPaymentStatusColor(
                          appointment.paymentStatus
                        )}`}
                      >
                        {appointment.paymentStatus || 'N/A'}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-sm">
                      <div className="flex gap-1 flex-wrap">
                        {appointment.status === 'PENDING' && (
                          <>
                            <Button
                              size="sm"
                              variant="success"
                              onClick={() =>
                                handleStatusChange(appointment.id, 'CONFIRMED')
                              }
                            >
                              Confirm
                            </Button>
                            <Button
                              size="sm"
                              variant="danger"
                              onClick={() =>
                                handleStatusChange(appointment.id, 'CANCELLED')
                              }
                            >
                              Cancel
                            </Button>
                          </>
                        )}
                        {appointment.status === 'CONFIRMED' && (
                          <Button
                            size="sm"
                            variant="success"
                            onClick={() =>
                              handleStatusChange(appointment.id, 'COMPLETED')
                            }
                          >
                            Complete
                          </Button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>

            {filteredAppointments.length === 0 && (
              <div className="text-center py-8 text-gray-500">
                {statusFilter === 'all' && paymentFilter === 'all'
                  ? 'No appointments found. Create one first!'
                  : 'No appointments match the selected filters.'}
              </div>
            )}
          </div>
        )}
      </Card>

      {appointments.length > 0 && (
        <div className="mt-4 p-4 bg-gray-50 rounded-lg">
          <div className="grid grid-cols-5 gap-4 mb-2">
            <div>
              <p className="text-sm text-gray-600">
                <strong>Total:</strong> {appointments.length}
              </p>
            </div>
            <div>
              <p className="text-sm text-yellow-600">
                <strong>Pending:</strong>{' '}
                {appointments.filter(a => a.status === 'PENDING').length}
              </p>
            </div>
            <div>
              <p className="text-sm text-blue-600">
                <strong>Confirmed:</strong>{' '}
                {appointments.filter(a => a.status === 'CONFIRMED').length}
              </p>
            </div>
            <div>
              <p className="text-sm text-green-600">
                <strong>Completed:</strong>{' '}
                {appointments.filter(a => a.status === 'COMPLETED').length}
              </p>
            </div>
            <div>
              <p className="text-sm text-red-600">
                <strong>Cancelled:</strong>{' '}
                {appointments.filter(a => a.status === 'CANCELLED').length}
              </p>
            </div>
          </div>
          <p className="text-sm text-gray-600 mt-2">
            <strong>Tip:</strong> Click on any ID to copy it to clipboard. Use action
            buttons to manage appointment status.
          </p>
        </div>
      )}
    </div>
  );
};
