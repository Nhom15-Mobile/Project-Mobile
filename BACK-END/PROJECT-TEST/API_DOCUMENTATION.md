# API Documentation - Medical Appointment System

## Tổng quan

Base URL: `http://localhost:4000/api`

Authentication: Hầu hết endpoints yêu cầu JWT token trong header:
```
Authorization: Bearer {your-jwt-token}
```

---

## 1. AUTHENTICATION APIs (`/api/auth`)

### 1.1. Đăng ký tài khoản
```
POST /api/auth/register
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123",
  "fullName": "Nguyen Van A",
  "phone": "0912345678",
  "role": "PATIENT"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Registration successful",
  "data": {
    "user": {
      "id": "user-id",
      "email": "user@example.com",
      "fullName": "Nguyen Van A",
      "role": "PATIENT"
    },
    "token": "jwt-token-here"
  }
}
```

---

### 1.2. Đăng nhập
```
POST /api/auth/login
```

**Request Body**:
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "user": {
      "id": "user-id",
      "email": "user@example.com",
      "fullName": "Nguyen Van A",
      "role": "PATIENT"
    },
    "token": "jwt-token-here"
  }
}
```

---

### 1.3. Quên mật khẩu
```
POST /api/auth/forgot
```

**Request Body**:
```json
{
  "email": "user@example.com"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Password reset email sent"
}
```

---

### 1.4. Reset mật khẩu
```
POST /api/auth/reset
```

**Request Body**:
```json
{
  "token": "reset-token-from-email",
  "newPassword": "newpassword123"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Password reset successful"
}
```

---

### 1.5. Lấy thông tin user hiện tại
```
GET /api/auth/me
Authorization: Bearer {token}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "user-id",
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "phone": "0912345678",
    "role": "PATIENT",
    "createdAt": "2025-01-01T00:00:00.000Z"
  }
}
```

---

## 2. DOCTORS APIs (`/api/doctors`)

### 2.1. Lấy danh sách chuyên khoa
```
GET /api/doctors/specialties
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "specialties": [
      {
        "name": "BỆNH LÝ CỘT SỐNG",
        "fee": 150000
      },
      {
        "name": "DA LIỄU",
        "fee": 150000
      },
      {
        "name": "HUYẾT HỌC",
        "fee": 150000
      },
      {
        "name": "MẮT",
        "fee": 150000
      },
      {
        "name": "NGOẠI THẦN KINH",
        "fee": 150000
      },
      {
        "name": "TAI MŨI HỌNG",
        "fee": 150000
      },
      {
        "name": "THẦN KINH",
        "fee": 150000
      },
      {
        "name": "TIM MẠCH",
        "fee": 150000
      },
      {
        "name": "TƯ VẤN TÂM LÝ",
        "fee": 150000
      },
      {
        "name": "KHÁM VÀ TƯ VẤN DINH DƯỠNG",
        "fee": 150000
      }
    ]
  }
}
```

---

### 2.2. Tìm kiếm bác sĩ khả dụng
```
GET /api/doctors/available
```

**Query Parameters**:
- `specialty`: Chuyên khoa (optional)
- `date`: Ngày khám (YYYY-MM-DD) (optional)
- `page`: Trang (default: 1)
- `limit`: Số lượng (default: 10)

**Response** (200):
```json
{
  "success": true,
  "data": {
    "doctors": [
      {
        "id": "doctor-id",
        "fullName": "Dr. Nguyen Van B",
        "email": "doctor@example.com",
        "phone": "0987654321",
        "doctor": {
          "specialty": "TIM MẠCH",
          "yearsExperience": 10,
          "clinicName": "Phòng khám ABC",
          "bio": "Bác sĩ chuyên khoa Tim mạch...",
          "availableSlots": 5
        }
      }
    ],
    "pagination": {
      "total": 25,
      "page": 1,
      "limit": 10,
      "totalPages": 3
    }
  }
}
```

---

### 2.3. Tìm kiếm bác sĩ (public)
```
GET /api/doctors
```

**Query Parameters**:
- `specialty`: Chuyên khoa
- `search`: Tìm theo tên
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "doctors": [
      {
        "id": "doctor-id",
        "fullName": "Dr. Nguyen Van B",
        "email": "doctor@example.com",
        "phone": "0987654321",
        "doctor": {
          "specialty": "TIM MẠCH",
          "yearsExperience": 10,
          "clinicName": "Phòng khám ABC",
          "bio": "Bác sĩ chuyên khoa..."
        }
      }
    ],
    "pagination": {
      "total": 25,
      "page": 1,
      "limit": 10,
      "totalPages": 3
    }
  }
}
```

---

### 2.4. Lấy thông tin chi tiết bác sĩ
```
GET /api/doctors/:id
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "doctor-id",
    "fullName": "Dr. Nguyen Van B",
    "email": "doctor@example.com",
    "phone": "0987654321",
    "role": "DOCTOR",
    "doctor": {
      "specialty": "TIM MẠCH",
      "yearsExperience": 10,
      "clinicName": "Phòng khám ABC",
      "bio": "Bác sĩ chuyên khoa Tim mạch với 10 năm kinh nghiệm",
      "workDayBlocks": [
        {
          "dayOfWeek": 1,
          "startTime": "09:00",
          "endTime": "17:00"
        }
      ]
    }
  }
}
```

---

### 2.5. Lấy profile bác sĩ của tôi (Doctor only)
```
GET /api/doctors/me/profile
Authorization: Bearer {token}
Role: DOCTOR, ADMIN
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "doctor-profile-id",
    "userId": "user-id",
    "specialty": "TIM MẠCH",
    "yearsExperience": 10,
    "clinicName": "Phòng khám ABC",
    "bio": "Bác sĩ chuyên khoa...",
    "user": {
      "id": "user-id",
      "fullName": "Dr. Nguyen Van B",
      "email": "doctor@example.com",
      "phone": "0987654321"
    }
  }
}
```

---

### 2.6. Cập nhật profile bác sĩ của tôi (Doctor only)
```
PATCH /api/doctors/me/profile
Authorization: Bearer {token}
Role: DOCTOR, ADMIN
```

**Request Body**:
```json
{
  "specialty": "TIM MẠCH",
  "yearsExperience": 12,
  "clinicName": "Phòng khám ABC Updated",
  "bio": "Updated bio..."
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": "doctor-profile-id",
    "specialty": "TIM MẠCH",
    "yearsExperience": 12,
    "clinicName": "Phòng khám ABC Updated",
    "bio": "Updated bio..."
  }
}
```

---

### 2.7. Đặt lịch làm việc (Doctor only)
```
POST /api/doctors/workday/blocks
Authorization: Bearer {token}
Role: DOCTOR, ADMIN
```

**Request Body**:
```json
{
  "blocks": [
    {
      "dayOfWeek": 1,
      "startTime": "09:00",
      "endTime": "12:00"
    },
    {
      "dayOfWeek": 1,
      "startTime": "14:00",
      "endTime": "17:00"
    },
    {
      "dayOfWeek": 2,
      "startTime": "09:00",
      "endTime": "17:00"
    }
  ]
}
```

**Note**: `dayOfWeek`: 0 = Chủ Nhật, 1 = Thứ Hai, ..., 6 = Thứ Bảy

**Response** (200):
```json
{
  "success": true,
  "message": "Work schedule updated successfully",
  "data": {
    "blocks": [
      {
        "dayOfWeek": 1,
        "startTime": "09:00",
        "endTime": "12:00"
      }
    ]
  }
}
```

---

### 2.8. Xem lịch làm việc của tôi (Doctor only)
```
GET /api/doctors/workday
Authorization: Bearer {token}
Role: DOCTOR, ADMIN
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "blocks": [
      {
        "dayOfWeek": 1,
        "startTime": "09:00",
        "endTime": "12:00"
      },
      {
        "dayOfWeek": 1,
        "startTime": "14:00",
        "endTime": "17:00"
      }
    ]
  }
}
```

---

## 3. PATIENTS APIs (`/api/patients`)

**Role required**: PATIENT

### 3.1. Lấy profile bệnh nhân của tôi
```
GET /api/patients/profile
Authorization: Bearer {token}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "user-id",
    "fullName": "Nguyen Van A",
    "email": "patient@example.com",
    "phone": "0912345678",
    "role": "PATIENT",
    "patientProfile": {
      "address": "123 Nguyen Trai, Q1, TPHCM",
      "dateOfBirth": "1990-01-01",
      "gender": "Male"
    }
  }
}
```

---

### 3.2. Cập nhật profile bệnh nhân
```
POST /api/patients/profile
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "fullName": "Nguyen Van A Updated",
  "phone": "0987654321",
  "address": "456 Le Loi, Q1, TPHCM",
  "dateOfBirth": "1990-01-01",
  "gender": "Male"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Profile updated successfully",
  "data": {
    "id": "user-id",
    "fullName": "Nguyen Van A Updated",
    "phone": "0987654321"
  }
}
```

---

### 3.3. Lấy danh sách lịch khám của tôi
```
GET /api/patients/appointments
Authorization: Bearer {token}
```

**Query Parameters**:
- `status`: Lọc theo trạng thái (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "appointments": [
      {
        "id": "appointment-id",
        "patientId": "patient-id",
        "doctorId": "doctor-id",
        "careProfileId": "profile-id",
        "scheduledAt": "2025-12-01T09:00:00.000Z",
        "status": "CONFIRMED",
        "paymentStatus": "PAID",
        "service": "Khám TIM MẠCH",
        "symptoms": "Đau ngực, khó thở",
        "notes": "Ghi chú",
        "doctor": {
          "fullName": "Dr. Nguyen Van B",
          "email": "doctor@example.com",
          "specialty": "TIM MẠCH"
        },
        "careProfile": {
          "fullName": "Nguyen Van A",
          "relation": "Self"
        }
      }
    ],
    "pagination": {
      "total": 10,
      "page": 1,
      "limit": 10,
      "totalPages": 1
    }
  }
}
```

---

## 4. CARE PROFILES APIs (`/api/care-profiles`)

**Role required**: PATIENT, ADMIN

### 4.1. Lấy danh sách hồ sơ chăm sóc của tôi
```
GET /api/care-profiles
Authorization: Bearer {token}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "careProfiles": [
      {
        "id": "profile-id",
        "ownerId": "user-id",
        "fullName": "Nguyen Van A",
        "relation": "Self",
        "dob": "1990-01-01T00:00:00.000Z",
        "gender": "Male",
        "phone": "0912345678",
        "email": "user@example.com",
        "nationalId": "123456789012",
        "address": "123 Nguyen Trai, Q1",
        "province": "Ho Chi Minh",
        "district": "District 1",
        "occupation": "Engineer",
        "createdAt": "2025-01-01T00:00:00.000Z"
      }
    ]
  }
}
```

---

### 4.2. Tạo hồ sơ chăm sóc mới
```
POST /api/care-profiles
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "fullName": "Nguyen Van A",
  "relation": "Self",
  "dob": "1990-01-01",
  "gender": "Male",
  "phone": "0912345678",
  "email": "user@example.com",
  "nationalId": "123456789012",
  "address": "123 Nguyen Trai, Q1, TPHCM",
  "province": "Ho Chi Minh",
  "district": "District 1",
  "ward": "Ward 1",
  "occupation": "Engineer"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Care profile created successfully",
  "data": {
    "id": "profile-id",
    "ownerId": "user-id",
    "fullName": "Nguyen Van A",
    "relation": "Self",
    "dob": "1990-01-01T00:00:00.000Z",
    "gender": "Male",
    "phone": "0912345678",
    "email": "user@example.com"
  }
}
```

---

### 4.3. Cập nhật hồ sơ chăm sóc
```
PUT /api/care-profiles/:id
Authorization: Bearer {token}
```

**Request Body**:
```json
{
  "fullName": "Nguyen Van A Updated",
  "phone": "0987654321",
  "address": "456 Le Loi, Q1, TPHCM"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Care profile updated successfully",
  "data": {
    "id": "profile-id",
    "fullName": "Nguyen Van A Updated",
    "phone": "0987654321"
  }
}
```

---

### 4.4. Xóa hồ sơ chăm sóc
```
DELETE /api/care-profiles/:id
Authorization: Bearer {token}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Care profile deleted successfully"
}
```

---

## 5. APPOINTMENTS APIs (`/api/appointments`)

### 5.1. Xem lịch công khai (calendar)
```
GET /api/appointments/calendar
```

**Query Parameters**:
- `doctorId`: ID bác sĩ (optional)
- `date`: Ngày (YYYY-MM-DD) (optional)
- `month`: Tháng (YYYY-MM) (optional)

**Response** (200):
```json
{
  "success": true,
  "data": {
    "calendar": [
      {
        "date": "2025-12-01",
        "slots": [
          {
            "time": "09:00",
            "available": true,
            "doctorId": "doctor-id"
          },
          {
            "time": "09:30",
            "available": false,
            "doctorId": "doctor-id"
          }
        ]
      }
    ]
  }
}
```

---

### 5.2. Xem slot khả dụng
```
GET /api/appointments/available
Authorization: Bearer {token}
Role: PATIENT, DOCTOR, ADMIN
```

**Query Parameters**:
- `doctorId`: ID bác sĩ (required)
- `date`: Ngày (YYYY-MM-DD) (required)

**Response** (200):
```json
{
  "success": true,
  "data": {
    "slots": [
      {
        "id": "slot-id",
        "doctorId": "doctor-id",
        "start": "2025-12-01T09:00:00.000Z",
        "end": "2025-12-01T09:30:00.000Z",
        "isBooked": false,
        "doctor": {
          "fullName": "Dr. Nguyen Van B",
          "specialty": "TIM MẠCH"
        }
      }
    ]
  }
}
```

---

### 5.3. Đặt lịch khám (Patient only)
```
POST /api/appointments/book
Authorization: Bearer {token}
Role: PATIENT
```

**Request Body**:
```json
{
  "doctorId": "doctor-id",
  "careProfileId": "profile-id",
  "slotId": "slot-id",
  "service": "Khám TIM MẠCH",
  "symptoms": "Đau ngực, khó thở",
  "notes": "Ghi chú thêm"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Appointment booked successfully",
  "data": {
    "id": "appointment-id",
    "patientId": "patient-id",
    "doctorId": "doctor-id",
    "careProfileId": "profile-id",
    "scheduledAt": "2025-12-01T09:00:00.000Z",
    "status": "PENDING",
    "paymentStatus": "UNPAID",
    "service": "Khám TIM MẠCH",
    "symptoms": "Đau ngực, khó thở",
    "qrCode": "data:image/png;base64,..."
  }
}
```

---

### 5.4. Hủy lịch khám (Patient only)
```
POST /api/appointments/:id/cancel
Authorization: Bearer {token}
Role: PATIENT
```

**Request Body** (optional):
```json
{
  "reason": "Lý do hủy lịch"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Appointment cancelled successfully",
  "data": {
    "id": "appointment-id",
    "status": "CANCELLED",
    "cancelledAt": "2025-12-01T10:00:00.000Z"
  }
}
```

---

## 6. PAYMENTS APIs (`/api/payments`)

### 6.1. Tạo thanh toán MoMo (Patient only)
```
POST /api/payments/momo/create
Authorization: Bearer {token}
Role: PATIENT
```

**Request Body**:
```json
{
  "appointmentId": "appointment-id",
  "amount": 150000,
  "orderInfo": "Thanh toán lịch khám"
}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "payUrl": "https://test-payment.momo.vn/...",
    "qrCodeUrl": "https://...",
    "orderId": "ORDER123456",
    "amount": 150000
  }
}
```

---

### 6.2. MoMo IPN Callback (Public webhook)
```
POST /api/payments/momo/notify
```

**Request Body** (từ MoMo):
```json
{
  "partnerCode": "MOMO",
  "orderId": "ORDER123456",
  "requestId": "REQUEST123",
  "amount": 150000,
  "orderInfo": "Thanh toán lịch khám",
  "orderType": "momo_wallet",
  "transId": 123456789,
  "resultCode": 0,
  "message": "Successful",
  "payType": "qr",
  "responseTime": 1234567890123,
  "extraData": "",
  "signature": "..."
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Payment processed"
}
```

---

### 6.3. MoMo Return URL (Public redirect)
```
GET /api/payments/momo/return
```

**Query Parameters** (từ MoMo sau khi user thanh toán):
- `partnerCode`
- `orderId`
- `requestId`
- `amount`
- `orderInfo`
- `orderType`
- `transId`
- `resultCode`
- `message`
- `payType`
- `responseTime`
- `extraData`
- `signature`

**Response**: Redirect đến frontend với payment status

---

### 6.4. Lấy phiếu khám (Receipt)
```
GET /api/payments/receipt/:id
Authorization: Bearer {token}
Role: PATIENT, DOCTOR, ADMIN
```

`:id` là appointmentId

**Response** (200):
```json
{
  "success": true,
  "data": {
    "appointmentId": "appointment-id",
    "receiptNumber": "REC-20251201-001",
    "patientName": "Nguyen Van A",
    "doctorName": "Dr. Nguyen Van B",
    "service": "Khám TIM MẠCH",
    "amount": 150000,
    "paymentStatus": "PAID",
    "paymentDate": "2025-12-01T10:00:00.000Z",
    "scheduledAt": "2025-12-01T09:00:00.000Z"
  }
}
```

---

## 7. NOTIFICATIONS APIs (`/api/notifications`)

**Role required**: PATIENT, DOCTOR, ADMIN

### 7.1. Lấy danh sách thông báo của tôi
```
GET /api/notifications
Authorization: Bearer {token}
```

**Query Parameters**:
- `isRead`: Lọc theo đã đọc/chưa đọc (true/false)
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "notifications": [
      {
        "id": "notification-id",
        "userId": "user-id",
        "type": "APPOINTMENT_CONFIRMED",
        "title": "Lịch khám đã được xác nhận",
        "message": "Lịch khám của bạn vào ngày 01/12/2025 lúc 09:00 đã được xác nhận",
        "data": {
          "appointmentId": "appointment-id"
        },
        "isRead": false,
        "createdAt": "2025-12-01T08:00:00.000Z"
      }
    ],
    "pagination": {
      "total": 20,
      "page": 1,
      "limit": 10,
      "totalPages": 2
    }
  }
}
```

---

### 7.2. Gửi thông báo thay đổi lịch khám (Doctor/Admin only)
```
POST /api/notifications/appointments/:id/notify
Authorization: Bearer {token}
Role: ADMIN, DOCTOR
```

`:id` là appointmentId

**Request Body**:
```json
{
  "message": "Lịch khám của bạn đã được thay đổi"
}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Notification sent successfully"
}
```

---

## 8. LOCATIONS APIs (`/api/locations`)

**Public APIs** - Không cần authentication

### 8.1. Lấy danh sách tỉnh/thành phố
```
GET /api/locations/provinces
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "provinces": [
      {
        "code": "01",
        "name": "Hà Nội"
      },
      {
        "code": "79",
        "name": "Hồ Chí Minh"
      }
    ]
  }
}
```

---

### 8.2. Lấy danh sách quận/huyện
```
GET /api/locations/districts
```

**Query Parameters**:
- `provinceCode`: Mã tỉnh/thành phố (required)

**Response** (200):
```json
{
  "success": true,
  "data": {
    "districts": [
      {
        "code": "001",
        "name": "Quận 1",
        "provinceCode": "79"
      },
      {
        "code": "002",
        "name": "Quận 2",
        "provinceCode": "79"
      }
    ]
  }
}
```

---

### 8.3. Lấy danh sách phường/xã
```
GET /api/locations/wards
```

**Query Parameters**:
- `districtCode`: Mã quận/huyện (required)

**Response** (200):
```json
{
  "success": true,
  "data": {
    "wards": [
      {
        "code": "00001",
        "name": "Phường Bến Nghé",
        "districtCode": "001"
      },
      {
        "code": "00002",
        "name": "Phường Bến Thành",
        "districtCode": "001"
      }
    ]
  }
}
```

---

## 9. USERS APIs (`/api/users`)

**Role required**: Authenticated users

### 9.1. Lấy thông tin user hiện tại
```
GET /api/users/me
Authorization: Bearer {token}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "user-id",
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "phone": "0912345678",
    "role": "PATIENT",
    "createdAt": "2025-01-01T00:00:00.000Z"
  }
}
```

---

### 9.2. Lấy danh sách tất cả users
```
GET /api/users
Authorization: Bearer {token}
```

**Query Parameters**:
- `role`: Lọc theo role (PATIENT, DOCTOR, ADMIN)
- `search`: Tìm kiếm theo tên hoặc email
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "id": "user-id",
        "email": "user@example.com",
        "fullName": "Nguyen Van A",
        "phone": "0912345678",
        "role": "PATIENT",
        "createdAt": "2025-01-01T00:00:00.000Z"
      }
    ],
    "pagination": {
      "total": 100,
      "page": 1,
      "limit": 10,
      "totalPages": 10
    }
  }
}
```

---

### 9.3. Lấy thông tin user theo ID
```
GET /api/users/:id
Authorization: Bearer {token}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "id": "user-id",
    "email": "user@example.com",
    "fullName": "Nguyen Van A",
    "phone": "0912345678",
    "role": "PATIENT",
    "createdAt": "2025-01-01T00:00:00.000Z"
  }
}
```

---

## 10. ADMIN APIs (`/api/admin`)

**Role required**: ADMIN (tất cả endpoints)

### 10.1. Lấy thống kê hệ thống
```
GET /api/admin/statistics
Authorization: Bearer {admin-token}
```

**Response** (200):
```json
{
  "success": true,
  "data": {
    "totalUsers": 100,
    "totalDoctors": 25,
    "totalPatients": 70,
    "adminUsers": 5,
    "totalCareProfiles": 50,
    "totalAppointments": 200,
    "pendingAppointments": 30,
    "confirmedAppointments": 50,
    "completedAppointments": 100,
    "cancelledAppointments": 20,
    "totalDoctorSlots": 500,
    "availableSlots": 300,
    "bookedSlots": 200,
    "totalRevenue": 30000000,
    "monthlyRevenue": 5000000
  }
}
```

---

### 10.2. Tạo user mới
```
POST /api/admin/users
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "email": "newuser@example.com",
  "password": "password123",
  "fullName": "New User",
  "phone": "0912345678",
  "role": "PATIENT"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "User created successfully",
  "data": {
    "id": "user-id",
    "email": "newuser@example.com",
    "fullName": "New User",
    "role": "PATIENT"
  }
}
```

---

### 10.3. Lấy danh sách tất cả users
```
GET /api/admin/users
Authorization: Bearer {admin-token}
```

**Query Parameters**:
- `role`: Lọc theo role (PATIENT, DOCTOR, ADMIN)
- `search`: Tìm kiếm
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "users": [
      {
        "id": "user-id",
        "email": "user@example.com",
        "fullName": "Nguyen Van A",
        "phone": "0912345678",
        "role": "PATIENT",
        "createdAt": "2025-01-01T00:00:00.000Z"
      }
    ],
    "pagination": {
      "total": 100,
      "page": 1,
      "limit": 10,
      "totalPages": 10
    }
  }
}
```

---

### 10.4. Xóa user
```
DELETE /api/admin/users/:id
Authorization: Bearer {admin-token}
```

**Response** (200):
```json
{
  "success": true,
  "message": "User deleted successfully"
}
```

---

### 10.5. Tạo bác sĩ mới
```
POST /api/admin/doctors
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "email": "doctor@example.com",
  "password": "password123",
  "fullName": "Dr. Nguyen Van B",
  "phone": "0987654321",
  "specialty": "TIM MẠCH",
  "yearsExperience": 10,
  "clinicName": "Phòng khám ABC",
  "bio": "Bác sĩ chuyên khoa Tim mạch với 10 năm kinh nghiệm"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Doctor created successfully",
  "data": {
    "id": "user-id",
    "email": "doctor@example.com",
    "fullName": "Dr. Nguyen Van B",
    "role": "DOCTOR",
    "doctor": {
      "specialty": "TIM MẠCH",
      "yearsExperience": 10,
      "clinicName": "Phòng khám ABC"
    }
  }
}
```

---

### 10.6. Lấy danh sách tất cả bác sĩ
```
GET /api/admin/doctors
Authorization: Bearer {admin-token}
```

**Query Parameters**:
- `specialty`: Lọc theo chuyên khoa
- `search`: Tìm kiếm
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "doctors": [
      {
        "id": "user-id",
        "email": "doctor@example.com",
        "fullName": "Dr. Nguyen Van B",
        "phone": "0987654321",
        "role": "DOCTOR",
        "doctor": {
          "specialty": "TIM MẠCH",
          "yearsExperience": 10,
          "clinicName": "Phòng khám ABC",
          "bio": "Bác sĩ chuyên khoa..."
        }
      }
    ],
    "pagination": {
      "total": 25,
      "page": 1,
      "limit": 10,
      "totalPages": 3
    }
  }
}
```

---

### 10.7. Tạo hồ sơ chăm sóc
```
POST /api/admin/care-profiles
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "ownerId": "user-id",
  "fullName": "Nguyen Van A",
  "relation": "Self",
  "dob": "1990-01-01",
  "gender": "Male",
  "phone": "0912345678",
  "email": "user@example.com",
  "nationalId": "123456789012",
  "address": "123 Nguyen Trai, Q1, TPHCM",
  "province": "Ho Chi Minh",
  "district": "District 1",
  "ward": "Ward 1",
  "occupation": "Engineer"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Care profile created successfully",
  "data": {
    "id": "profile-id",
    "ownerId": "user-id",
    "fullName": "Nguyen Van A",
    "relation": "Self",
    "dob": "1990-01-01T00:00:00.000Z",
    "gender": "Male"
  }
}
```

---

### 10.8. Lấy danh sách tất cả hồ sơ chăm sóc
```
GET /api/admin/care-profiles
Authorization: Bearer {admin-token}
```

**Query Parameters**:
- `ownerId`: Lọc theo owner
- `search`: Tìm kiếm
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "careProfiles": [
      {
        "id": "profile-id",
        "ownerId": "user-id",
        "fullName": "Nguyen Van A",
        "relation": "Self",
        "dob": "1990-01-01T00:00:00.000Z",
        "gender": "Male",
        "phone": "0912345678",
        "owner": {
          "fullName": "Nguyen Van A",
          "email": "user@example.com"
        }
      }
    ],
    "pagination": {
      "total": 50,
      "page": 1,
      "limit": 10,
      "totalPages": 5
    }
  }
}
```

---

### 10.9. Tạo doctor slot mới
```
POST /api/admin/doctor-slots
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "doctorId": "doctor-id",
  "start": "2025-12-01T09:00:00Z",
  "end": "2025-12-01T09:30:00Z"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Doctor slot created successfully",
  "data": {
    "id": "slot-id",
    "doctorId": "doctor-id",
    "start": "2025-12-01T09:00:00.000Z",
    "end": "2025-12-01T09:30:00.000Z",
    "isBooked": false
  }
}
```

---

### 10.10. Lấy danh sách tất cả doctor slots
```
GET /api/admin/doctor-slots
Authorization: Bearer {admin-token}
```

**Query Parameters**:
- `doctorId`: Lọc theo bác sĩ
- `date`: Lọc theo ngày (YYYY-MM-DD)
- `isBooked`: Lọc theo trạng thái (true/false)
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "slots": [
      {
        "id": "slot-id",
        "doctorId": "doctor-id",
        "start": "2025-12-01T09:00:00.000Z",
        "end": "2025-12-01T09:30:00.000Z",
        "isBooked": false,
        "doctor": {
          "fullName": "Dr. Nguyen Van B",
          "email": "doctor@example.com",
          "specialty": "TIM MẠCH"
        }
      }
    ],
    "pagination": {
      "total": 500,
      "page": 1,
      "limit": 10,
      "totalPages": 50
    }
  }
}
```

---

### 10.11. Xóa doctor slot
```
DELETE /api/admin/doctor-slots/:id
Authorization: Bearer {admin-token}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Doctor slot deleted successfully"
}
```

---

### 10.12. Tạo appointment mới
```
POST /api/admin/appointments
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "patientId": "patient-id",
  "doctorId": "doctor-id",
  "careProfileId": "profile-id",
  "doctorSlotId": "slot-id",
  "scheduledAt": "2025-12-01T09:00:00Z",
  "service": "Khám TIM MẠCH",
  "symptoms": "Đau ngực, khó thở",
  "notes": "Ghi chú"
}
```

**Response** (201):
```json
{
  "success": true,
  "message": "Appointment created successfully",
  "data": {
    "id": "appointment-id",
    "patientId": "patient-id",
    "doctorId": "doctor-id",
    "careProfileId": "profile-id",
    "scheduledAt": "2025-12-01T09:00:00.000Z",
    "status": "PENDING",
    "paymentStatus": "UNPAID",
    "service": "Khám TIM MẠCH"
  }
}
```

---

### 10.13. Lấy danh sách tất cả appointments
```
GET /api/admin/appointments
Authorization: Bearer {admin-token}
```

**Query Parameters**:
- `patientId`: Lọc theo bệnh nhân
- `doctorId`: Lọc theo bác sĩ
- `status`: Lọc theo trạng thái (PENDING, CONFIRMED, COMPLETED, CANCELLED)
- `paymentStatus`: Lọc theo trạng thái thanh toán (PAID, UNPAID, REFUNDED)
- `date`: Lọc theo ngày (YYYY-MM-DD)
- `page`: Trang
- `limit`: Số lượng

**Response** (200):
```json
{
  "success": true,
  "data": {
    "appointments": [
      {
        "id": "appointment-id",
        "patientId": "patient-id",
        "doctorId": "doctor-id",
        "careProfileId": "profile-id",
        "scheduledAt": "2025-12-01T09:00:00.000Z",
        "status": "CONFIRMED",
        "paymentStatus": "PAID",
        "service": "Khám TIM MẠCH",
        "symptoms": "Đau ngực, khó thở",
        "patient": {
          "fullName": "Nguyen Van A",
          "email": "patient@example.com"
        },
        "doctor": {
          "fullName": "Dr. Nguyen Van B",
          "email": "doctor@example.com",
          "specialty": "TIM MẠCH"
        },
        "careProfile": {
          "fullName": "Nguyen Van A",
          "relation": "Self"
        }
      }
    ],
    "pagination": {
      "total": 200,
      "page": 1,
      "limit": 10,
      "totalPages": 20
    }
  }
}
```

---

### 10.14. Cập nhật trạng thái appointment
```
PATCH /api/admin/appointments/:id/status
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "status": "CONFIRMED"
}
```

**Valid status values**: `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`

**Response** (200):
```json
{
  "success": true,
  "message": "Appointment status updated successfully",
  "data": {
    "id": "appointment-id",
    "status": "CONFIRMED",
    "updatedAt": "2025-12-01T10:00:00.000Z"
  }
}
```

---

### 10.15. Cập nhật trạng thái thanh toán
```
PATCH /api/admin/appointments/:id/payment-status
Authorization: Bearer {admin-token}
```

**Request Body**:
```json
{
  "paymentStatus": "PAID"
}
```

**Valid paymentStatus values**: `PAID`, `UNPAID`, `REFUNDED`

**Response** (200):
```json
{
  "success": true,
  "message": "Payment status updated successfully",
  "data": {
    "id": "appointment-id",
    "paymentStatus": "PAID",
    "updatedAt": "2025-12-01T10:00:00.000Z"
  }
}
```

---

### 10.16. Xóa appointment
```
DELETE /api/admin/appointments/:id
Authorization: Bearer {admin-token}
```

**Response** (200):
```json
{
  "success": true,
  "message": "Appointment deleted successfully"
}
```

---

## Error Responses

Tất cả API errors trả về format:

```json
{
  "success": false,
  "message": "Error message here"
}
```

### HTTP Status Codes:
- `200` - OK: Request thành công
- `201` - Created: Tạo resource thành công
- `400` - Bad Request: Request không hợp lệ
- `401` - Unauthorized: Chưa đăng nhập hoặc token không hợp lệ
- `403` - Forbidden: Không có quyền truy cập
- `404` - Not Found: Không tìm thấy resource
- `409` - Conflict: Xung đột dữ liệu (duplicate email, slot already booked, etc.)
- `500` - Internal Server Error: Lỗi server

### Common Error Examples:

**401 Unauthorized**:
```json
{
  "success": false,
  "message": "No token provided"
}
```

**403 Forbidden**:
```json
{
  "success": false,
  "message": "Access denied. ADMIN role required."
}
```

**404 Not Found**:
```json
{
  "success": false,
  "message": "User not found"
}
```

**409 Conflict**:
```json
{
  "success": false,
  "message": "Email already exists"
}
```

---

## Enums và Constants

### User Roles
- `PATIENT` - Bệnh nhân
- `DOCTOR` - Bác sĩ
- `ADMIN` - Quản trị viên

### Appointment Status
- `PENDING` - Chờ xác nhận
- `CONFIRMED` - Đã xác nhận
- `COMPLETED` - Hoàn thành
- `CANCELLED` - Đã hủy

### Payment Status
- `UNPAID` - Chưa thanh toán
- `PAID` - Đã thanh toán
- `REFUNDED` - Đã hoàn tiền

### Gender
- `Male` - Nam
- `Female` - Nữ
- `Other` - Khác

### Notification Types
- `APPOINTMENT_CREATED` - Lịch khám mới
- `APPOINTMENT_CONFIRMED` - Lịch khám đã xác nhận
- `APPOINTMENT_CANCELLED` - Lịch khám đã hủy
- `APPOINTMENT_REMINDER` - Nhắc nhở lịch khám
- `PAYMENT_SUCCESS` - Thanh toán thành công

### Care Profile Relations
- `Self` - Bản thân
- `Parent` - Cha/Mẹ
- `Child` - Con
- `Spouse` - Vợ/Chồng
- `Sibling` - Anh/Chị/Em
- `Other` - Khác

### Day of Week (for doctor workday)
- `0` - Chủ Nhật
- `1` - Thứ Hai
- `2` - Thứ Ba
- `3` - Thứ Tư
- `4` - Thứ Năm
- `5` - Thứ Sáu
- `6` - Thứ Bảy

### Specialties (10 chuyên khoa cố định)
- `BỆNH LÝ CỘT SỐNG` - Fee: 150,000 VNĐ
- `DA LIỄU` - Fee: 150,000 VNĐ
- `HUYẾT HỌC` - Fee: 150,000 VNĐ
- `MẮT` - Fee: 150,000 VNĐ
- `NGOẠI THẦN KINH` - Fee: 150,000 VNĐ
- `TAI MŨI HỌNG` - Fee: 150,000 VNĐ
- `THẦN KINH` - Fee: 150,000 VNĐ
- `TIM MẠCH` - Fee: 150,000 VNĐ
- `TƯ VẤN TÂM LÝ` - Fee: 150,000 VNĐ
- `KHÁM VÀ TƯ VẤN DINH DƯỠNG` - Fee: 150,000 VNĐ

---

## Notes

1. Tất cả timestamps sử dụng ISO 8601 format (UTC)
2. Pagination mặc định: `page=1`, `limit=10`
3. Tất cả responses đều có field `success`
4. Token hết hạn sau 7 ngày
5. MoMo payment chỉ hỗ trợ VNĐ
6. QR code được tạo tự động khi booking appointment
7. Doctor slots có thời lượng 30 phút
8. Appointment phải được tạo ít nhất 1 giờ trước thời gian khám
9. Patient chỉ có thể hủy appointment trước 2 giờ

---

## Tổng số Endpoints: 66 APIs

- Authentication: 5 endpoints
- Doctors: 8 endpoints
- Patients: 3 endpoints
- Care Profiles: 4 endpoints
- Appointments: 4 endpoints
- Payments: 4 endpoints
- Notifications: 2 endpoints
- Locations: 3 endpoints
- Users: 3 endpoints
- Admin: 16 endpoints
- Health Check: 1 endpoint (GET /api/health)
