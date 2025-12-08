# 📡 Complete API Routes List

## Base URL
```
http://localhost:4000/api
```

---

## 🔐 AUTH Module (`/api/auth`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | `/login` | ❌ | Đăng nhập |
| POST | `/register` | ❌ | Đăng ký user mới |
| POST | `/forgot` | ❌ | Yêu cầu reset password |
| POST | `/reset` | ❌ | Reset password với code |

**Total: 4 endpoints**

---

## 👥 USERS Module (`/api/users`) ✨

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/me` | ✅ | Lấy profile user hiện tại |
| GET | `/` | ✅ | Lấy danh sách tất cả users |
| GET | `/:id` | ✅ | Lấy user theo ID |

**Total: 3 endpoints** (NEW)

---

## 👨‍⚕️ DOCTORS Module (`/api/doctors`)

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/specialties` | ❌ | | Lấy danh sách chuyên khoa |
| GET | `/available` | ❌ | | Lấy bác sĩ khả dụng |
| GET | `/me/profile` | ✅ | DOCTOR/ADMIN | Lấy profile bác sĩ hiện tại |
| PATCH | `/me/profile` | ✅ | DOCTOR/ADMIN | Cập nhật profile |
| POST | `/workday/blocks` | ✅ | DOCTOR/ADMIN | Set work day blocks |
| GET | `/workday` | ✅ | DOCTOR/ADMIN | Lấy my work day |
| GET | `/` | ❌ | | Tìm kiếm bác sĩ |
| GET | `/:id` | ❌ | | Lấy thông tin bác sĩ |

**Total: 8 endpoints**

---

## 🏥 PATIENTS Module (`/api/patient`)

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/profile` | ✅ | PATIENT | Lấy profile bệnh nhân |
| POST | `/profile` | ✅ | PATIENT | Cập nhật profile |
| GET | `/appointments` | ✅ | PATIENT | Lấy danh sách lịch khám |

**Total: 3 endpoints**

---

## 📅 APPOINTMENTS Module (`/api/appointments`)

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/available` | ✅ | PATIENT/DOCTOR/ADMIN | Lấy khung giờ trống |
| POST | `/book` | ✅ | PATIENT | Đặt lịch khám |
| POST | `/:id/cancel` | ✅ | PATIENT | Hủy lịch khám |
| GET | `/calendar` | ❌ | | Lấy lịch (calendar view) |

**Total: 4 endpoints**

---

## 💳 PAYMENTS Module (`/api/payments`)

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/momo/create` | ✅ | PATIENT | Tạo payment Momo |
| POST | `/momo/notify` | ❌ | | Webhook notify từ Momo |
| GET | `/momo/return` | ❌ | | Return URL từ Momo |
| GET | `/receipt/:id` | ✅ | PATIENT/DOCTOR/ADMIN | Lấy hóa đơn |

**Total: 4 endpoints**

---

## 🔔 NOTIFICATIONS Module (`/api/notifications`)

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/` | ✅ | PATIENT/DOCTOR/ADMIN | Lấy thông báo của user |
| POST | `/appointments/:id/notify` | ✅ | ADMIN/DOCTOR | Gửi thông báo thay đổi lịch |

**Total: 2 endpoints**

---

## 👨‍👩‍👧 CARE PROFILES Module (`/api/care-profiles`)

| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/` | ✅ | PATIENT/ADMIN | Lấy danh sách care profiles |
| POST | `/` | ✅ | PATIENT/ADMIN | Tạo care profile |
| PUT | `/:id` | ✅ | PATIENT/ADMIN | Cập nhật care profile |
| DELETE | `/:id` | ✅ | PATIENT/ADMIN | Xóa care profile |

**Total: 4 endpoints**

---

## 📍 LOCATIONS Module (`/api/locations`)

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| GET | `/provinces` | ❌ | Lấy danh sách tỉnh/thành phố |
| GET | `/districts` | ❌ | Lấy danh sách quận/huyện |
| GET | `/wards` | ❌ | Lấy danh sách xã/phường |

**Total: 3 endpoints**

---

## 🛡️ ADMIN Module (`/api/admin`)

### Statistics & Dashboard
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| GET | `/statistics` | ✅ | ADMIN | Lấy thống kê dashboard |

### Users Management
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/users` | ✅ | ADMIN | Tạo user mới |
| GET | `/users` | ✅ | ADMIN | Lấy danh sách users |
| DELETE | `/users/:id` | ✅ | ADMIN | Xóa user |

### Doctors Management
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/doctors` | ✅ | ADMIN | Tạo doctor mới |
| GET | `/doctors` | ✅ | ADMIN | Lấy danh sách doctors |

### Care Profiles Management
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/care-profiles` | ✅ | ADMIN | Tạo care profile |
| GET | `/care-profiles` | ✅ | ADMIN | Lấy danh sách care profiles |

### Doctor Slots Management
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/doctor-slots` | ✅ | ADMIN | Tạo doctor slot |
| GET | `/doctor-slots` | ✅ | ADMIN | Lấy danh sách slots |
| DELETE | `/doctor-slots/:id` | ✅ | ADMIN | Xóa doctor slot |

### Appointments Management
| Method | Endpoint | Auth | Role | Description |
|--------|----------|------|------|-------------|
| POST | `/appointments` | ✅ | ADMIN | Tạo appointment |
| GET | `/appointments` | ✅ | ADMIN | Lấy danh sách appointments |
| PATCH | `/appointments/:id/status` | ✅ | ADMIN | Cập nhật status appointment |
| DELETE | `/appointments/:id` | ✅ | ADMIN | Xóa appointment |

**Total: 17 endpoints**

---

## 📊 Summary Statistics

### By Module
| Module | Count | Auth Required |
|--------|-------|---|
| Auth | 4 | ❌ All public |
| Users | 3 | ✅ All authenticated |
| Doctors | 8 | Mixed |
| Patients | 3 | ✅ All authenticated |
| Appointments | 4 | Mixed |
| Payments | 4 | Mixed |
| Notifications | 2 | ✅ All authenticated |
| Care Profiles | 4 | ✅ All authenticated |
| Locations | 3 | ❌ All public |
| Admin | 17 | ✅ All admin only |

### Total
- **Total Endpoints:** 52
- **Authenticated Only:** 26 (50%)
- **Public:** 16 (30%)
- **Mixed:** 10 (20%)
- **Admin Only:** 17

---

## 🔐 Authentication & Authorization

### Authentication
```bash
Header: Authorization: Bearer {token}
```

### Roles
- `PATIENT` - Bệnh nhân
- `DOCTOR` - Bác sĩ
- `ADMIN` - Quản trị viên

### Middleware
- `auth` - Require authentication
- `allow('ROLE1', 'ROLE2')` - Require specific roles

---

## 🧪 Testing Quick Commands

### Health Check
```bash
curl http://localhost:4000/api/health
```

### Register
```bash
curl -X POST http://localhost:4000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@123456","fullName":"Test"}'
```

### Login (Get Token)
```bash
curl -X POST http://localhost:4000/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"test@example.com","password":"Test@123456"}'
```

### Get All Users
```bash
curl http://localhost:4000/api/users \
  -H "Authorization: Bearer {TOKEN}"
```

---

## 📁 Module Files

```
src/modules/
├── auth/
│   ├── auth.routes.js
│   ├── auth.controller.js
│   └── auth.service.js
│
├── users/                    ✨ NEW
│   ├── users.routes.js
│   ├── users.controller.js
│   └── users.service.js
│
├── doctors/
│   ├── doctors.routes.js
│   ├── doctors.controller.js
│   └── doctors.service.js
│
├── patients/
│   ├── patients.routes.js
│   ├── patients.controller.js
│   └── patients.service.js
│
├── appointments/
│   ├── appointments.routes.js
│   ├── appointments.controller.js
│   └── appointments.service.js
│
├── payments/
│   ├── payments.routes.js
│   ├── payments.controller.js
│   └── payments.service.js
│
├── notifications/
│   ├── notifications.routes.js
│   ├── notifications.controller.js
│   └── notifications.service.js
│
├── careProfiles/
│   ├── careProfiles.routes.js
│   ├── careProfiles.controller.js
│   └── careProfiles.service.js
│
├── locations/
│   ├── locations.routes.js
│   ├── locations.controller.js
│   └── locations.service.js
│
└── admin/
    ├── admin.routes.js
    ├── admin.controller.js
    └── admin.service.js
```

---

## 🔄 API Request Flow

```
Client Request
    ↓
Express Server (port 4000)
    ↓
Routes Matching (/api/...)
    ↓
Middleware Stack
    ├── auth (JWT verification)
    ├── allow (role-based access)
    └── validate (input validation)
    ↓
Controller (handle business logic)
    ↓
Service (data operations)
    ↓
Prisma ORM
    ↓
MySQL Database
    ↓
Response (JSON format)
```

---

## ✨ Newly Added Features

### Users Module
```
GET  /api/users        → Lấy tất cả users
GET  /api/users/:id    → Lấy user theo ID
GET  /api/users/me     → Lấy profile user hiện tại
```

These endpoints allow:
- View all registered users
- Get specific user info
- Check current user profile

All require JWT authentication.

---

## 📞 Response Format

### Success Response (200, 201)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { ... }
}
```

### Error Response (400, 401, 403, 404, 500)
```json
{
  "success": false,
  "message": "Error description",
  "statusCode": 400,
  "error": { ... }
}
```

---


