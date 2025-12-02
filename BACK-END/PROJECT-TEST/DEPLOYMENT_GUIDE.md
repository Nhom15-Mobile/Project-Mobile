# Hướng dẫn Cấu hình và Deploy - Medical Appointment System

## Mục lục
1. [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
2. [Cài đặt môi trường Development](#cài-đặt-môi-trường-development)
3. [Cấu hình Database](#cấu-hình-database)
4. [Cấu hình Environment Variables](#cấu-hình-environment-variables)
5. [Chạy ứng dụng Development](#chạy-ứng-dụng-development)
6. [Build và Deploy Production](#build-và-deploy-production)
7. [Troubleshooting](#troubleshooting)

---

## Yêu cầu hệ thống

### Backend
- Node.js >= 18.x
- npm hoặc yarn
- MySQL >= 8.0
- Git

### Frontend (Admin Panel)
- Node.js >= 18.x
- npm hoặc yarn

---

## Cài đặt môi trường Development

### 1. Clone Repository

```bash
git clone <repository-url>
cd PROJECT-TEST
```

### 2. Cài đặt Dependencies

#### Backend
```bash
# Cài đặt dependencies cho backend
npm install

# Hoặc dùng yarn
yarn install
```

#### Frontend Admin Panel
```bash
cd admin-panel
npm install

# Hoặc dùng yarn
yarn install
```

---

## Cấu hình Database

### 1. Cài đặt MySQL

#### Ubuntu/Debian
```bash
sudo apt update
sudo apt install mysql-server
sudo mysql_secure_installation
```

#### macOS (với Homebrew)
```bash
brew install mysql
brew services start mysql
```

#### Windows
Download và cài đặt từ: https://dev.mysql.com/downloads/mysql/

### 2. Tạo Database

Đăng nhập vào MySQL:
```bash
mysql -u root -p
```

Tạo database và user:
```sql
CREATE DATABASE medical_appointment;

CREATE USER 'medical_user'@'localhost' IDENTIFIED BY 'your_password';

GRANT ALL PRIVILEGES ON medical_appointment.* TO 'medical_user'@'localhost';

FLUSH PRIVILEGES;

EXIT;
```

### 3. Chạy Prisma Migrations

```bash
# Generate Prisma Client
npx prisma generate

# Chạy migrations để tạo tables
npx prisma migrate deploy

# Hoặc dùng migrate dev trong development
npx prisma migrate dev
```

### 4. Seed Database (Optional)

Tạo dữ liệu mẫu:
```bash
# Tạo 100 test users
node create-test-users.js

# Hoặc dùng Prisma seed
npm run seed
```

---

## Cấu hình Environment Variables

### Backend (.env)

Tạo file `.env` trong thư mục root:

```env
# Database
DATABASE_URL="mysql://medical_user:your_password@localhost:3306/medical_appointment"

# Server
PORT=4000
NODE_ENV=development

# JWT
JWT_SECRET=your-super-secret-jwt-key-change-this-in-production
JWT_EXPIRES_IN=7d

# MoMo Payment (Development)
MOMO_PARTNER_CODE=your_momo_partner_code
MOMO_ACCESS_KEY=your_momo_access_key
MOMO_SECRET_KEY=your_momo_secret_key
MOMO_ENDPOINT=https://test-payment.momo.vn/v2/gateway/api/create
MOMO_RETURN_URL=http://localhost:4000/api/payments/momo/return
MOMO_IPN_URL=http://localhost:4000/api/payments/momo/ipn

# Email Configuration (Optional)
SMTP_HOST=smtp.gmail.com
SMTP_PORT=587
SMTP_USER=your-email@gmail.com
SMTP_PASSWORD=your-app-password
EMAIL_FROM=noreply@medical-appointment.com

# Frontend URL (for CORS)
FRONTEND_URL=http://localhost:5173
ADMIN_PANEL_URL=http://localhost:5175
```

### Frontend Admin Panel (.env)

Tạo file `.env` trong thư mục `admin-panel`:

```env
VITE_API_URL=http://localhost:4000/api
```

---

## Chạy ứng dụng Development

### 1. Khởi động Backend

```bash
# Trong thư mục root
npm run dev

# Server sẽ chạy tại http://localhost:4000
```

### 2. Khởi động Admin Panel

```bash
# Trong thư mục admin-panel
cd admin-panel
npm run dev

# Admin panel sẽ chạy tại http://localhost:5173 hoặc 5174, 5175...
```

### 3. Kiểm tra API

```bash
# Test health check
curl http://localhost:4000/api/health

# Hoặc mở trình duyệt
http://localhost:4000/api/health
```

---

## Build và Deploy Production

### Backend

#### 1. Chuẩn bị Production Environment

Cập nhật `.env` cho production:
```env
NODE_ENV=production
PORT=4000
DATABASE_URL="mysql://user:password@production-host:3306/database"
JWT_SECRET=super-strong-secret-key-for-production
FRONTEND_URL=https://your-domain.com
ADMIN_PANEL_URL=https://admin.your-domain.com
```

#### 2. Build và Run

```bash
# Cài đặt dependencies production only
npm ci --production

# Chạy Prisma migrations
npx prisma migrate deploy

# Khởi động server với PM2
npm install -g pm2
pm2 start src/server.js --name medical-api
pm2 save
pm2 startup
```

#### 3. Setup Nginx Reverse Proxy

```nginx
server {
    listen 80;
    server_name api.your-domain.com;

    location / {
        proxy_pass http://localhost:4000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

Kích hoạt SSL với Let's Encrypt:
```bash
sudo apt install certbot python3-certbot-nginx
sudo certbot --nginx -d api.your-domain.com
```

### Frontend Admin Panel

#### 1. Build Production

```bash
cd admin-panel

# Update .env.production
echo "VITE_API_URL=https://api.your-domain.com/api" > .env.production

# Build
npm run build

# Kết quả sẽ ở thư mục dist/
```

#### 2. Deploy với Nginx

```nginx
server {
    listen 80;
    server_name admin.your-domain.com;
    root /var/www/admin-panel/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    # Gzip compression
    gzip on;
    gzip_types text/plain text/css application/json application/javascript text/xml application/xml application/xml+rss text/javascript;
}
```

Copy build files:
```bash
sudo mkdir -p /var/www/admin-panel
sudo cp -r dist/* /var/www/admin-panel/
sudo chown -R www-data:www-data /var/www/admin-panel
```

Setup SSL:
```bash
sudo certbot --nginx -d admin.your-domain.com
```

### Deploy với Docker (Tùy chọn)

#### Backend Dockerfile

Tạo `Dockerfile` trong thư mục root:

```dockerfile
FROM node:18-alpine

WORKDIR /app

# Copy package files
COPY package*.json ./
COPY prisma ./prisma/

# Install dependencies
RUN npm ci --production

# Copy source code
COPY . .

# Generate Prisma Client
RUN npx prisma generate

EXPOSE 4000

CMD ["npm", "start"]
```

#### docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    restart: always
    environment:
      MYSQL_DATABASE: medical_appointment
      MYSQL_USER: medical_user
      MYSQL_PASSWORD: secure_password
      MYSQL_ROOT_PASSWORD: root_password
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"

  backend:
    build: .
    restart: always
    ports:
      - "4000:4000"
    environment:
      DATABASE_URL: mysql://medical_user:secure_password@mysql:3306/medical_appointment
      JWT_SECRET: your-jwt-secret
      NODE_ENV: production
    depends_on:
      - mysql
    command: sh -c "npx prisma migrate deploy && npm start"

  admin-panel:
    build: ./admin-panel
    restart: always
    ports:
      - "80:80"
    depends_on:
      - backend

volumes:
  mysql_data:
```

Build và chạy:
```bash
docker-compose up -d
```

---

## Troubleshooting

### Lỗi kết nối Database

```bash
# Kiểm tra MySQL đang chạy
sudo systemctl status mysql

# Kiểm tra port
sudo netstat -tlnp | grep 3306

# Test connection
mysql -u medical_user -p -h localhost medical_appointment
```

### Lỗi Prisma

```bash
# Reset Prisma Client
rm -rf node_modules/.prisma
npx prisma generate

# Reset database (WARNING: xóa tất cả dữ liệu)
npx prisma migrate reset

# Xem logs chi tiết
DEBUG=* npx prisma migrate dev
```

### Lỗi Port đã được sử dụng

```bash
# Tìm process đang dùng port
lsof -ti:4000

# Kill process
kill -9 $(lsof -ti:4000)

# Hoặc thay đổi PORT trong .env
PORT=4001
```

### Lỗi CORS

Kiểm tra `FRONTEND_URL` và `ADMIN_PANEL_URL` trong `.env` backend:
```env
FRONTEND_URL=http://localhost:5173
ADMIN_PANEL_URL=http://localhost:5175
```

### Lỗi JWT Token

```bash
# Generate new JWT secret
node -e "console.log(require('crypto').randomBytes(64).toString('hex'))"

# Update JWT_SECRET trong .env
```

### Admin Panel không kết nối được API

Kiểm tra `VITE_API_URL` trong `admin-panel/.env`:
```env
VITE_API_URL=http://localhost:4000/api
```

Restart dev server:
```bash
cd admin-panel
npm run dev
```

---

## Monitoring và Logs

### PM2 Logs

```bash
# Xem logs
pm2 logs medical-api

# Xem logs realtime
pm2 logs medical-api --lines 100

# Clear logs
pm2 flush
```

### Nginx Logs

```bash
# Access logs
sudo tail -f /var/log/nginx/access.log

# Error logs
sudo tail -f /var/log/nginx/error.log
```

### Database Logs

```bash
# MySQL error log
sudo tail -f /var/log/mysql/error.log
```

---

## Backup và Restore

### Database Backup

```bash
# Backup
mysqldump -u medical_user -p medical_appointment > backup_$(date +%Y%m%d_%H%M%S).sql

# Restore
mysql -u medical_user -p medical_appointment < backup_20250101_120000.sql
```

### Automated Backup (Cron)

Tạo script `backup.sh`:
```bash
#!/bin/bash
BACKUP_DIR="/backup/mysql"
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u medical_user -p'password' medical_appointment > $BACKUP_DIR/backup_$DATE.sql
find $BACKUP_DIR -type f -mtime +7 -delete
```

Thêm vào crontab:
```bash
crontab -e
# Chạy backup mỗi ngày lúc 2 giờ sáng
0 2 * * * /path/to/backup.sh
```

---

## Security Checklist

- [ ] Đổi tất cả default passwords
- [ ] Sử dụng strong JWT_SECRET
- [ ] Enable HTTPS với SSL certificate
- [ ] Configure firewall (chỉ mở port cần thiết)
- [ ] Enable rate limiting
- [ ] Regular backup database
- [ ] Update dependencies thường xuyên
- [ ] Monitor logs và errors
- [ ] Sử dụng environment variables (không hardcode secrets)
- [ ] Enable database encryption
- [ ] Configure CORS đúng cách
- [ ] Implement proper error handling

---

## Performance Optimization

### Database Indexes

Prisma đã tạo sẵn indexes trong schema. Để thêm indexes:
```prisma
model User {
  // ...
  @@index([email])
  @@index([role])
}
```

### Redis Caching (Optional)

```bash
# Install Redis
sudo apt install redis-server

# Install node-redis
npm install redis
```

### Load Balancing

Sử dụng Nginx làm load balancer cho multiple instances:
```nginx
upstream backend {
    least_conn;
    server localhost:4000;
    server localhost:4001;
    server localhost:4002;
}

server {
    location / {
        proxy_pass http://backend;
    }
}
```

---

## Support

Nếu gặp vấn đề:
1. Kiểm tra logs
2. Xem lại cấu hình environment variables
3. Tham khảo API Documentation
4. Kiểm tra Troubleshooting section

---

## Useful Commands

```bash
# Backend
npm run dev          # Development mode
npm start            # Production mode
npm run seed         # Seed database
npx prisma studio    # Open Prisma Studio GUI

# Admin Panel
npm run dev          # Development mode
npm run build        # Build for production
npm run preview      # Preview production build

# Database
npx prisma migrate dev        # Create and apply migration
npx prisma migrate deploy     # Apply migrations in production
npx prisma db push            # Push schema changes without migration
npx prisma db pull            # Pull schema from database
npx prisma studio             # Open database GUI

# PM2
pm2 list             # List all processes
pm2 restart all      # Restart all processes
pm2 stop all         # Stop all processes
pm2 delete all       # Delete all processes
pm2 monit            # Monitor processes
```
