import { useAuth } from '../../contexts/AuthContext';
import { Button } from '../common';

export const Navbar = () => {
  const { user, logout } = useAuth();

  return (
    <nav className="bg-white shadow-md border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <h1 className="text-xl font-bold text-gray-900">
              Admin Panel
            </h1>
          </div>

          <div className="flex items-center gap-4">
            <div className="text-sm">
              <p className="font-medium text-gray-700">{user?.fullName}</p>
              <p className="text-gray-500">{user?.email}</p>
            </div>
            <Button variant="outline" size="sm" onClick={logout}>
              Logout
            </Button>
          </div>
        </div>
      </div>
    </nav>
  );
};
