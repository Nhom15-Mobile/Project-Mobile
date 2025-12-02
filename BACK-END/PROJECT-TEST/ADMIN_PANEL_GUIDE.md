# Admin Panel Guide

## Truy cập Admin Panel

Mở trình duyệt và truy cập:
```
http://localhost:4000/admin.html
```

## Đăng nhập

1. Sử dụng tài khoản có role `ADMIN` để đăng nhập
2. Nhập email và password
3. Click "Login"

**Lưu ý:** Token sẽ được lưu trong localStorage, bạn chỉ cần đăng nhập 1 lần.

## Tính năng đã hoàn thiện

### 1. Dashboard Statistics
- Hiển thị tổng quan thống kê:
  - Total Users
  - Total Doctors
  - Total Patients
  - Care Profiles
  - Total Appointments
  - Pending Appointments
  - Completed Appointments

### 2. Add User (Tab 1)
- Tạo user mới với role: PATIENT, DOCTOR, hoặc ADMIN
- Các trường:
  - Email (required)
  - Password (required)
  - Full Name (required)
  - Phone
  - Role (required)

### 3. Add Doctor (Tab 2)
- Tạo bác sĩ mới (tự động set role = DOCTOR)
- Các trường:
  - Email, Password, Full Name, Phone
  - Specialty (required)
  - Years of Experience
  - Clinic Name
  - Bio

### 4. Add Care Profile (Tab 3)
- Tạo hồ sơ chăm sóc cho bệnh nhân
- Các trường quan trọng:
  - Owner User ID (required)
  - Full Name, Relation (required)
  - DOB, Gender, Phone, Email
  - National ID, Occupation
  - Insurance Number
  - Province, District, Address
  - Note

### 5. Add Doctor Slot (Tab 4)
- Tạo khung giờ làm việc cho bác sĩ
- Các trường:
  - Doctor User ID (required)
  - Start Date & Time (required)
  - End Date & Time (required)

### 6. Add Appointment (Tab 5)
- Tạo lịch hẹn mới
- Các trường:
  - Care Profile ID (required)
  - Doctor Slot ID (required)
  - Service (required)

### 7. Manage Users (Tab 6) ✨ NEW
Quản lý danh sách users với các tính năng:
- **Search:** Tìm kiếm theo name, email, phone
- **Filter by Role:** Lọc theo PATIENT, DOCTOR, ADMIN
- **Delete User:** Xóa user (có confirm)
- Hiển thị thông tin: ID, Full Name, Email, Phone, Role, Created At
- Pagination support

### 8. View All Data (Tab 7)
Xem dữ liệu chi tiết với 4 loại:

#### Appointments
- Hiển thị: ID, Patient, Doctor, Care Profile, Service, Scheduled, Status
- **Update Status:** Click nút "Update Status" để thay đổi trạng thái
  - PENDING
  - CONFIRMED
  - COMPLETED
  - CANCELLED

#### Doctors
- Hiển thị: ID, Name, Email, Specialty, Experience, Clinic

#### Care Profiles
- Hiển thị: ID, Name, Relation, Owner, DOB, Phone

#### Doctor Slots
- Hiển thị: ID, Doctor, Start, End, Booked status

## Các tính năng bảo mật

1. **JWT Authentication:**
   - Tất cả API calls đều yêu cầu Bearer token
   - Token được lưu trong localStorage
   - Auto logout khi token hết hạn

2. **Role-based Access:**
   - Chỉ user có role ADMIN mới được phép đăng nhập
   - Nếu role khác ADMIN sẽ bị từ chối

3. **Logout:**
   - Click nút "Logout" ở góc trên bên phải
   - Token sẽ bị xóa khỏi localStorage

## API Endpoints được sử dụng

```
POST   /api/auth/login                    → Login
GET    /api/admin/statistics              → Dashboard stats
POST   /api/admin/users                   → Create user
GET    /api/admin/users                   → List users (with search & filter)
DELETE /api/admin/users/:id               → Delete user
POST   /api/admin/doctors                 → Create doctor
GET    /api/admin/doctors                 → List doctors
POST   /api/admin/care-profiles           → Create care profile
GET    /api/admin/care-profiles           → List care profiles
POST   /api/admin/doctor-slots            → Create slot
GET    /api/admin/doctor-slots            → List slots
POST   /api/admin/appointments            → Create appointment
GET    /api/admin/appointments            → List appointments
PATCH  /api/admin/appointments/:id/status → Update status
```

## Hướng dẫn test

### 1. Test Login
```bash
# Tạo admin user nếu chưa có
POST http://localhost:4000/api/auth/register
{
  "email": "admin@test.com",
  "password": "Admin@123",
  "fullName": "Admin User",
  "role": "ADMIN"
}
```

### 2. Test Create User
- Vào tab "Add User"
- Điền form và submit
- Check thống kê tăng lên

### 3. Test Manage Users
- Vào tab "Manage Users"
- Test search: nhập tên hoặc email
- Test filter: chọn role
- Test delete: click Delete (có confirm)

### 4. Test View & Update Appointments
- Vào tab "View All Data"
- Chọn "Appointments"
- Click "Update Status" trên bất kỳ appointment nào
- Nhập status mới (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- Check status đã được update

## Lưu ý

1. **API URL:** Mặc định sử dụng `http://localhost:4000`
2. **CORS:** Backend phải enable CORS cho frontend
3. **Database:** Đảm bảo Prisma và MySQL đã được setup
4. **Responsive:** Giao diện responsive, có thể dùng trên mobile
5. **Error Handling:** Tất cả errors đều hiển thị alert màu đỏ
6. **Success Messages:** Success hiển thị alert màu xanh

## Troubleshooting

### Không đăng nhập được
- Check backend đã chạy chưa (port 4000)
- Check user có role ADMIN chưa
- Check network tab trong DevTools

### Không load được data
- Check token trong localStorage
- Check API endpoint trả về data đúng format
- Check console log để xem error

### Update status không hoạt động
- Confirm appointment ID tồn tại
- Check status nhập đúng format (UPPERCASE)
- Check API có xử lý PATCH request không

## Future Enhancements

Các tính năng có thể thêm sau:
- [ ] Pagination controls (prev/next buttons)
- [ ] Advanced filters (date range, status)
- [ ] Export data to CSV/Excel
- [ ] Bulk actions (delete multiple users)
- [ ] Edit user/doctor profiles
- [ ] Upload avatar/images
- [ ] Real-time notifications
- [ ] Charts and graphs for statistics
- [ ] Activity logs
