## Sau khi clone (cài đặt nhanh)

1. Clone repo:
```bash
git clone https://github.com/your-username/your-repo.git
cd your-repo
2. Tạo file .env từ mẫu:
# macOS / Linux
cp .env.sample .env
# Windows (PowerShell)
copy .env.sample .env

3. Cài dependencies (tạo node_modules):
npm install

4. Chạy ở chế độ phát triển:
npm run dev
# hoặc
npm start