import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Users,
  UserPlus,
  Stethoscope,
  FolderHeart,
  Calendar,
  CalendarPlus,
  ClipboardList,
  FileText,
  Clock,
  CalendarCheck,
} from 'lucide-react';

const menuItems = [
  { path: '/', icon: LayoutDashboard, label: 'Dashboard' },
  { path: '/users', icon: Users, label: 'Manage Users' },
  { path: '/add-user', icon: UserPlus, label: 'Add User' },
  { path: '/add-doctor', icon: Stethoscope, label: 'Add Doctor' },
  { path: '/add-care-profile', icon: FolderHeart, label: 'Add Care Profile' },
  { path: '/care-profiles', icon: FileText, label: 'View Care Profiles' },
  { path: '/add-doctor-slot', icon: Calendar, label: 'Add Doctor Slot' },
  { path: '/doctor-slots', icon: Clock, label: 'View Doctor Slots' },
  { path: '/add-appointment', icon: CalendarPlus, label: 'Add Appointment' },
  { path: '/appointments', icon: CalendarCheck, label: 'View Appointments' },
  // { path: '/view-data', icon: ClipboardList, label: 'View All Data' },
];

export const Sidebar = () => {
  return (
    <aside className="w-64 bg-gray-900 text-white min-h-screen">
      <div className="p-6">
        <h2 className="text-2xl font-bold mb-8">Medical Admin</h2>
        <nav className="space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink
                key={item.path}
                to={item.path}
                className={({ isActive }) =>
                  `flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                    isActive
                      ? 'bg-blue-600 text-white'
                      : 'text-gray-300 hover:bg-gray-800 hover:text-white'
                  }`
                }
              >
                <Icon size={20} />
                <span>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
      </div>
    </aside>
  );
};
