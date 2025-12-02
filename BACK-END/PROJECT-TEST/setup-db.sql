-- File SQL để setup database
-- Chạy bằng lệnh: sudo mysql -u root < setup-db.sql

-- Tạo user mới
CREATE USER IF NOT EXISTS 'project_user'@'localhost' IDENTIFIED BY 'Project@123456';

-- Tạo database
CREATE DATABASE IF NOT EXISTS project_test;

-- Cấp quyền
GRANT ALL PRIVILEGES ON project_test.* TO 'project_user'@'localhost';
FLUSH PRIVILEGES;

-- Kiểm tra
SELECT user, host, plugin FROM mysql.user WHERE user='project_user';
