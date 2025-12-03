// import { useState, useEffect } from 'react';
// import { adminAPI } from '../services/api';
// import { Card, Button, Input, Select, Alert } from '../components/common';
// import { Trash2, Search } from 'lucide-react';
// import { format } from 'date-fns';

// export const ManageUsers = () => {
//   const [users, setUsers] = useState([]);
//   const [loading, setLoading] = useState(false);
//   const [message, setMessage] = useState({ type: '', text: '' });
//   const [search, setSearch] = useState('');
//   const [roleFilter, setRoleFilter] = useState('');

//   useEffect(() => {
//     fetchUsers();
//   }, [search, roleFilter]);

//   const fetchUsers = async () => {
//     try {
//       setLoading(true);

//       const params = {};
//       if (search) params.search = search;
//       if (roleFilter) params.role = roleFilter;

//       const response = await adminAPI.getUsers(params);
//       const data = response.data.data || response.data;

//       setUsers(data.users || data || []);
//     } catch (error) {
//       setMessage({
//         type: 'error',
//         text: error.response?.data?.message || 'Failed to load users',
//       });
//     } finally {
//       setLoading(false);
//     }
//   };

//   const handleDelete = async (id) => {
//     if (!confirm('Are you sure you want to delete this user?')) return;

//     try {
//       await adminAPI.deleteUser(id);
//       setMessage({ type: 'success', text: 'User deleted successfully' });
//       fetchUsers();
//     } catch (error) {
//       setMessage({
//         type: 'error',
//         text: error.response?.data?.message || 'Failed to delete user',
//       });
//     }
//   };

//   return (
//     <div>
//       <h1 className="text-3xl font-bold text-gray-900 mb-6">Manage Users</h1>

//       {message.text && (
//         <div className="mb-4">
//           <Alert
//             type={message.type}
//             message={message.text}
//             onClose={() => setMessage({ type: '', text: '' })}
//           />
//         </div>
//       )}

//       <Card>
//         {/* Filters */}
//         <div className="mb-6 flex gap-4">
//           <div className="flex-1 relative">
//             <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
//             <input
//               type="text"
//               placeholder="Search by name, email, or phone..."
//               className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
//               value={search}
//               onChange={(e) => setSearch(e.target.value)}
//             />
//           </div>

//           <select
//             className="border border-gray-300 rounded-lg px-3 py-2"
//             value={roleFilter}
//             onChange={(e) => setRoleFilter(e.target.value)}
//           >
//             <option value="">All Roles</option>
//             <option value="PATIENT">Patient</option>
//             <option value="DOCTOR">Doctor</option>
//             <option value="ADMIN">Admin</option>
//           </select>
//         </div>

//         {/* Table */}
//         <div className="overflow-x-auto">
//           {loading ? (
//             <div className="flex justify-center py-8">
//               <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
//             </div>
//           ) : (
//             <table className="w-full">
//               <thead className="bg-gray-50 border-b">
//                 <tr>
//                   <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
//                   <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Name</th>
//                   <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Email</th>
//                   <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Role</th>
//                   <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Actions</th>
//                 </tr>
//               </thead>

//               <tbody className="divide-y">
//                 {users.map((u) => (
//                   <tr key={u.id} className="hover:bg-gray-50">
//                     <td className="px-4 py-3 text-sm font-mono">{u.id.slice(0, 8)}...</td>
//                     <td className="px-4 py-3 text-sm">{u.fullName}</td>
//                     <td className="px-4 py-3 text-sm">{u.email}</td>

//                     <td className="px-4 py-3 text-sm">
//                       <span className="px-2 py-1 bg-blue-100 text-blue-800 rounded-full text-xs">
//                         {u.role}
//                       </span>
//                     </td>

//                     <td className="px-4 py-3 text-sm">
//                       <Button
//                         size="sm"
//                         variant="danger"
//                         onClick={() => handleDelete(u.id)}
//                       >
//                         <Trash2 size={16} />
//                         Delete
//                       </Button>
//                     </td>
//                   </tr>
//                 ))}
//               </tbody>
//             </table>
//           )}

//           {users.length === 0 && !loading && (
//             <div className="text-center py-8 text-gray-500">
//               No users found.
//             </div>
//           )}
//         </div>
//       </Card>
//     </div>
//   );
// };
import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { Card, Button, Input, Select, Alert } from '../components/common';
import { Trash2, Search } from 'lucide-react';
import { format } from 'date-fns';

export const ManageUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');

  // ====== PHÂN TRANG ======
  const [page, setPage] = useState(1);
  const [pagination, setPagination] = useState({
    page: 1,
    limit: 20,
    total: 0,
    totalPages: 1,
  });
  const PAGE_SIZE = 20;

  // đổi search / role → reset về page 1
  useEffect(() => {
    setPage(1);
  }, [search, roleFilter]);

  // load users mỗi khi search / role / page đổi
  useEffect(() => {
    fetchUsers();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [search, roleFilter, page]);

  const fetchUsers = async () => {
    try {
      setLoading(true);

      const params = { page, limit: PAGE_SIZE };
      if (search) params.search = search;
      if (roleFilter) params.role = roleFilter;

      const response = await adminAPI.getUsers(params);
      const data = response.data.data || response.data;

      const list = data.users || data.items || data || [];
      setUsers(Array.isArray(list) ? list : []);

      const pg = data.pagination || {
        page,
        limit: PAGE_SIZE,
        total: Array.isArray(list) ? list.length : 0,
        totalPages: Math.max(
          1,
          Math.ceil((Array.isArray(list) ? list.length : 0) / PAGE_SIZE)
        ),
      };
      setPagination(pg);
    } catch (error) {
      console.error(error);
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
      console.error(error);
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to delete user',
      });
    }
  };

  const total = pagination.total || 0;
  const currentPage = pagination.page || 1;
  const totalPages = pagination.totalPages || 1;
  const from = total === 0 ? 0 : (currentPage - 1) * pagination.limit + 1;
  const to = total === 0 ? 0 : Math.min(currentPage * pagination.limit, total);

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
        {/* Filters */}
        <div className="mb-6 flex gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400" size={20} />
            <input
              type="text"
              placeholder="Search by name, email, or phone..."
              className="w-full pl-10 pr-4 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>

          <Select
            value={roleFilter}
            onChange={(e) => setRoleFilter(e.target.value)}
            options={[
              { value: '', label: 'All Roles' },
              { value: 'PATIENT', label: 'Patient' },
              { value: 'DOCTOR', label: 'Doctor' },
              { value: 'ADMIN', label: 'Admin' },
            ]}
            className="w-48"
          />
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          {loading ? (
            <div className="flex justify-center py-8">
              <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
          ) : (
            <>
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">ID</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Name</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Email</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Phone</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Role</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">Created</th>
                    <th className="px-4 py-3 text-left text-sm font-semibold text-gray-700">
                      Actions
                    </th>
                  </tr>
                </thead>

                <tbody className="divide-y">
                  {users.map((user) => (
                    <tr key={user.id} className="hover:bg-gray-50">
                      <td className="px-4 py-3 text-sm text-gray-600 font-mono">
                        {user.id.slice(0, 8)}...
                      </td>
                      <td className="px-4 py-3 text-sm font-medium text-gray-900">
                        {user.fullName}
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-600">{user.email}</td>
                      <td className="px-4 py-3 text-sm text-gray-600">{user.phone || '-'}</td>
                      <td className="px-4 py-3">
                        <span
                          className={`px-2 py-1 text-xs rounded-full ${
                            user.role === 'ADMIN'
                              ? 'bg-red-100 text-red-800'
                              : user.role === 'DOCTOR'
                              ? 'bg-blue-100 text-blue-800'
                              : 'bg-green-100 text-green-800'
                          }`}
                        >
                          {user.role}
                        </span>
                      </td>
                      <td className="px-4 py-3 text-sm text-gray-600">
                        {user.createdAt
                          ? format(new Date(user.createdAt), 'MMM dd, yyyy')
                          : '-'}
                      </td>
                      <td className="px-4 py-3">
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleDelete(user.id)}
                        >
                          <Trash2 size={16} />
                        </Button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>

              {total === 0 && (
                <div className="text-center py-8 text-gray-500">No users found</div>
              )}

              {total > 0 && (
                <div className="flex items-center justify-between mt-4 text-sm text-gray-700">
                  <p>
                    Showing {from}–{to} of {total} users
                  </p>
                  <div className="flex items-center gap-2">
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={currentPage <= 1}
                      onClick={() => setPage((p) => Math.max(1, p - 1))}
                    >
                      Previous
                    </Button>
                    <span>
                      Page {currentPage} / {totalPages}
                    </span>
                    <Button
                      variant="outline"
                      size="sm"
                      disabled={currentPage >= totalPages}
                      onClick={() => setPage((p) => Math.min(totalPages, p + 1))}
                    >
                      Next
                    </Button>
                  </div>
                </div>
              )}
            </>
          )}
        </div>
      </Card>
    </div>
  );
};
