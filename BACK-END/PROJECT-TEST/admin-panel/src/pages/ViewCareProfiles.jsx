import { useState, useEffect } from 'react';
import { adminAPI } from '../services/api';
import { Card, Button, Alert } from '../components/common';
import { format } from 'date-fns';

const PAGE_SIZE = 25;

export const ViewCareProfiles = () => {
  const [careProfiles, setCareProfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState({ type: '', text: '' });
  const [page, setPage] = useState(1);

  useEffect(() => {
    fetchCareProfiles();
  }, []);

  const fetchCareProfiles = async () => {
    try {
      setLoading(true);
      const response = await adminAPI.getCareProfiles();
      const data = response.data.data || response.data;
      const profilesList = data.careProfiles || data;
      setCareProfiles(Array.isArray(profilesList) ? profilesList : []);
      setPage(1);
    } catch (error) {
      setMessage({
        type: 'error',
        text: error.response?.data?.message || 'Failed to load care profiles',
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

  const totalPages = Math.max(1, Math.ceil(careProfiles.length / PAGE_SIZE));
  const currentPage = Math.min(page, totalPages);
  const startIndex = (currentPage - 1) * PAGE_SIZE;
  const currentItems = careProfiles.slice(startIndex, startIndex + PAGE_SIZE);

  return (
    <div>
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Care Profiles</h1>
        <Button onClick={fetchCareProfiles}>Refresh</Button>
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
                  {currentItems.map((profile) => (
                    <tr key={profile.id} className="hover:bg-gray-50">
                      {/* body giữ nguyên nhưng dùng profile từ currentItems */}
                    </tr>
                  ))}
                </tbody>
              </table>

              {currentItems.length === 0 && (
                <div className="text-center py-8 text-gray-500">
                  No care profiles found. Create one first!
                </div>
              )}
            </div>

            {careProfiles.length > PAGE_SIZE && (
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

      {careProfiles.length > 0 && (
        <div className="mt-4 p-4 bg-gray-50 rounded-lg">
          <p className="text-sm text-gray-600">
            <strong>Total Care Profiles:</strong> {careProfiles.length}
          </p>
          <p className="text-sm text-gray-600 mt-1">
            <strong>Tip:</strong> Click on any ID to copy it to clipboard for creating appointments.
          </p>
        </div>
      )}
    </div>
  );
};
