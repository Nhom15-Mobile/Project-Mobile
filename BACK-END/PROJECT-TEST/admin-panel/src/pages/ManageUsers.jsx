import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { Card, Button, Select, Alert } from '../components/common';
import { Trash2, Search } from 'lucide-react';
import { format } from 'date-fns';

const PAGE_SIZE = 25;

export const ManageUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [page, setPage] = useState(1);

  useEffect(() => {
    fetchUsers();
  }, [search, roleFilter]);

  const fetchUsers = async () => {
    try {
      setLoading(true);
      const params = {};
      if (search) params.search = search;
      if (roleFilter) params.role = roleFilter;

      const response = await adminAPI.getUsers(params);
      const data = response.data.data || response.data;
      setUsers(data.users || data || []);
      setPage(1); // mỗi lần search/filter đổi thì quay lại trang 1
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to load users',
      });
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Are you sure you want to delete this user?')) return;

    try {
      await adminAPI.deleteUser(id);
      setMessage({ type: 'success', text: 'User deleted successfully' });
      fetchUsers();
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to delete user',
      });
    }
  };

  const totalPages = Math.max(1, Math.ceil(users.length / PAGE_SIZE));
  const currentPage = Math.min(page, totalPages);
  const startIndex = (currentPage - 1) * PAGE_SIZE;
  const currentItems = users.slice(startIndex, startIndex + PAGE_SIZE);

  return (
    <div>
      <h1 className="text-3xl font-bold text-gray-900 mb-6">Manage Users</h1>

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
        <div className="mb-6 flex gap-4">
          {/* search + select giữ nguyên */}
        </div>

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
                  {currentItems.map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      {/* body giữ nguyên nhưng dùng user từ currentItems */}
                    </tr>
                  ))}
                </tbody>
              </table>

              {currentItems.length === 0 && (
                <div className="text-center py-8 text-gray-500">
                  No users found
                </div>
              )}
            </div>

            {users.length > PAGE_SIZE && (
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
    </div>
  );
};
