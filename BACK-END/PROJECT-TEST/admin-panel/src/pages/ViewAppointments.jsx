import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { Card, Button, Alert } from '../components/common';
import { format } from 'date-fns';

const PAGE_SIZE = 20;

export const ViewAppointments = () => {
  const [appointments, setAppointments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [statusFilter, setStatusFilter] = useState('all');   // all, pending, confirmed, cancelled, completed
  const [paymentFilter, setPaymentFilter] = useState('all'); // all, paid, unpaid
  const [page, setPage] = useState(1);

  useEffect(() => {
    fetchAppointments();
  }, []);

  // khi đổi filter thì nhảy về trang 1
  useEffect(() => {
    setPage(1);
  }, [statusFilter, paymentFilter]);

  const fetchAppointments = async () => {
    try {
      setLoading(true);
      const response = await adminAPI.getAppointments();
      const data = response.data.data || response.data;
      const appointmentsList = data.appointments || data;
      setAppointments(Array.isArray(appointmentsList) ? appointmentsList : []);
      setPage(1); // lần load mới cũng về trang 1
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
      case 'REQUIRES_PAYMENT':
        return 'bg-red-100 text-red-800';
      case 'REFUNDED':
        return 'bg-purple-100 text-purple-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  // ========== FILTERED LIST ==========
  const filteredAppointments = appointments.filter((apt) => {
    if (statusFilter !== 'all' && apt.status !== statusFilter.toUpperCase()) {
      return false;
    }

    if (paymentFilter === 'paid') {
      return apt.paymentStatus === 'PAID';
    }

    if (paymentFilter === 'unpaid') {
      return apt.paymentStatus !== 'PAID';
    }

    return true;
  });

  const paidCount = appointments.filter(a => a.paymentStatus === 'PAID').length;
  const unpaidCount = appointments.filter(a => a.paymentStatus !== 'PAID').length;

  // ========== PAGINATION ==========
  const totalPages = Math.max(1, Math.ceil(filteredAppointments.length / PAGE_SIZE));
  const currentPage = Math.min(page, totalPages); // lỡ filter làm ít đi
  const startIndex = (currentPage - 1) * PAGE_SIZE;
  const currentItems = filteredAppointments.slice(startIndex, startIndex + PAGE_SIZE);

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

      {/* FILTERS ... (giữ nguyên đoạn filter như code của m) */}

      {/* --- đoạn filter giữ đúng như m gửi, t không lặp lại cho đỡ dài --- */}

      <Card>
        {loading ? (
          <div className="flex justify-center py-8">
            <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
          </div>
        ) : (
          <>
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  {/* thead giữ nguyên */}
                </thead>
                <tbody className="divide-y">
                  {currentItems.map((appointment) => (
                    <tr key={appointment.id} className="hover:bg-gray-50">
                      {/* body giữ nguyên nhưng dùng appointment từ currentItems */}
                    </tr>
                  ))}
                </tbody>
              </table>

              {currentItems.length === 0 && (
                <div className="text-center py-8 text-gray-500">
                  {statusFilter === 'all' && paymentFilter === 'all'
                    ? 'No appointments found. Create one first!'
                    : 'No appointments match the selected filters.'}
                </div>
              )}
            </div>

            {filteredAppointments.length > PAGE_SIZE && (
              <div className="flex items-center justify-between mt-4">
                <span className="text-sm text-gray-600">
                  Page {currentPage} of {totalPages}
                </span>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={currentPage === 1}
                    onClick={() => setPage((p) => Math.max(1, p - 1))}
                  >
                    Previous
                  </Button>
                  <Button
                    variant="outline"
                    size="sm"
                    disabled={currentPage === totalPages}
                    onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                  >
                    Next
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </Card>

      {/* đoạn summary Total / Pending / Confirmed... giữ nguyên */}
    </div>
  );
};
