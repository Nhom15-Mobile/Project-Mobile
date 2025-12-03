# Fix MySQL Authentication Issue

## Vấn đề hiện tại
MySQL root user đang sử dụng `auth_socket` plugin thay vì password authentication, nên không thể kết nối bằng password.

## Giải pháp

### Cách 1: Tạo user mới cho project (KHUYẾN KHÍCH)

Chạy các lệnh sau trong MySQL:

```bash
sudo mysql -u root
```

Trong MySQL shell, chạy:

```sql
-- Tạo user mới
CREATE USER 'project_user'@'localhost' IDENTIFIED BY 'Project@123456';

-- Tạo database nếu chưa có
CREATE DATABASE IF NOT EXISTS project_test;

-- Cấp quyền
GRANT ALL PRIVILEGES ON project_test.* TO 'project_user'@'localhost';
FLUSH PRIVILEGES;

-- Kiểm tra
SELECT user, host, plugin FROM mysql.user WHERE user='project_user';

-- Thoát
EXIT;
```

Sau đó update file `.env`:

```env
DATABASE_URL="mysql://project_user:Project@123456@localhost:3306/project_test"
```

### Cách 2: Đổi authentication method cho root user

```bash
sudo mysql -u root
```

Trong MySQL shell:

```sql
-- Đổi sang mysql_native_password
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Chophuhai130423@';
FLUSH PRIVILEGES;
EXIT;
```

Sau đó giữ nguyên file `.env`:

```env
DATABASE_URL="mysql://root:Chophuhai130423%40@localhost:3306/project_test"
```

### Sau khi fix xong

1. Chạy migration:
```bash
cd /home/minh/Downloads/Project-Mobile/BACK-END/PROJECT-TEST
node node_modules/prisma/build/index.js db push
```

2. Restart server:
```bash
node src/server.js
```

3. Test API:
```bash
curl http://localhost:4000/api/admin/statistics
```

4. Mở browser tại: http://localhost:4000/admin.html
