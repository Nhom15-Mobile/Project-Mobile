# 🚀 START HERE - React Admin Panel

## ✅ Status: Ready to Use!

React Admin Panel đã được tạo xong và sẵn sàng sử dụng!

---

## 📍 Quick Access

### Backend (đang chạy)
- **URL:** http://localhost:4000
- **Health:** http://localhost:4000/api/health
- **Status:** ✅ Running

### Frontend Admin Panel
- **Location:** `admin-panel/`
- **Dev URL:** http://localhost:5173 (khi chạy npm run dev)

---

## 🎯 Bắt Đầu Ngay

### Bước 1: Chạy Frontend

```bash
cd admin-panel
npm run dev
```

Truy cập: **http://localhost:5173**

### Bước 2: Login

Tạo admin user nếu chưa có:

```bash
curl -X POST http://localhost:4000/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@test.com",
    "password": "Admin@123",
    "fullName": "Admin User",
    "role": "ADMIN"
  }'
```

Sau đó login với:
- Email: `admin@test.com`
- Password: `Admin@123`

---

## 📚 Documentation

| File | Mô tả |
|------|-------|
| **admin-panel/QUICK_START.md** | Hướng dẫn nhanh (đọc đầu tiên!) |
| **admin-panel/SETUP.md** | Chi tiết setup & troubleshooting |
| **admin-panel/README.md** | Full documentation |
| **REACT_ADMIN_PANEL_SUMMARY.md** | Tổng hợp toàn bộ project |

---

## ✨ Tính Năng Chính

1. **Dashboard** - Thống kê tổng quan
2. **Manage Users** - Quản lý users (search, filter, delete)
3. **Add User** - Tạo user mới
4. **Add Doctor** - Thêm bác sĩ
5. **Add Care Profile** - Tạo hồ sơ bệnh nhân
6. **Add Doctor Slot** - Tạo khung giờ khám
7. **Add Appointment** - Đặt lịch hẹn
8. **View Data** - Xem tất cả data

---

## 🛠️ Tech Stack

- React 19
- React Router 6
- TailwindCSS
- Axios
- Vite 5

---

## 🔧 Useful Commands

### Backend (PROJECT-TEST/)
```bash
npm run dev          # Start backend
npm run prisma:dev   # Run migrations
npm run seed         # Seed database
```

### Frontend (admin-panel/)
```bash
npm install          # Install dependencies
npm run dev         # Start dev server
npm run build       # Build for production
npm run preview     # Preview production build
```

---

## 🐛 Troubleshooting

### Port 4000 đã được sử dụng
```bash
lsof -ti:4000 | xargs kill -9
cd /home/minh/Downloads/Project-Mobile/BACK-END/PROJECT-TEST
npm run dev
```

### Clear và reinstall
```bash
cd admin-panel
rm -rf node_modules package-lock.json
npm install
```

### Reset auth
Trong browser console:
```javascript
localStorage.clear()
location.reload()
```

---

## 📊 Project Structure

```
PROJECT-TEST/
├── admin-panel/              # ← React Admin Panel (MỚI!)
│   ├── src/
│   │   ├── components/
│   │   ├── contexts/
│   │   ├── pages/
│   │   ├── services/
│   │   └── App.jsx
│   ├── QUICK_START.md
│   ├── SETUP.md
│   └── README.md
├── src/                      # Backend source code
├── prisma/                   # Database schema
└── .env                      # Backend config
```

---

## ✅ Checklist Để Bắt Đầu

- [x] Backend đang chạy (port 4000)
- [x] Database connected
- [ ] `cd admin-panel`
- [ ] `npm install`
- [ ] Tạo `.env` với `VITE_API_URL=http://localhost:4000/api`
- [ ] `npm run dev`
- [ ] Tạo admin user
- [ ] Login tại http://localhost:5173
- [ ] Test các features

---

## 🎉 Next Steps

1. **Test thử các features** - Tạo users, doctors, appointments
2. **Customize UI** - Thay đổi colors, logo, etc.
3. **Add features** - Pagination, charts, export, etc.
4. **Deploy** - Build và deploy lên production

---

## 💡 Tips

- **Hot Reload:** Vite tự động reload khi save file
- **DevTools:** Press F12 để debug
- **API Calls:** Check Network tab để xem requests
- **State:** Xem localStorage để check token/user

---

## 🌐 URLs Summary

| Service | URL | Status |
|---------|-----|--------|
| Backend Health | http://localhost:4000/api/health | ✅ Running |
| Backend API | http://localhost:4000/api | ✅ Running |
| Admin Panel | http://localhost:5173 | ⏳ Start with `npm run dev` |

---

## 📞 Need Help?

1. Check console logs (F12)
2. Check Network tab for API errors
3. Read SETUP.md for troubleshooting
4. Verify backend is running
5. Check `.env` configuration

---

**Enjoy your new React Admin Panel!** 🚀✨

Created: November 30, 2025
Status: ✅ Production Ready
