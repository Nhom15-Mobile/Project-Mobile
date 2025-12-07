# main.py
import os
import tempfile

from fastapi import FastAPI, UploadFile, File
from fastapi.responses import JSONResponse

from ocr_services import run_ocr   # import hàm bạn đã viết

app = FastAPI(
    title="CCCD OCR API",
    description="API nhận ảnh CCCD và trả về các trường thông tin",
    version="1.0"
)


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/ocr-cccd")
async def ocr_cccd(file: UploadFile = File(...)):
    """
    Nhận 1 file ảnh (multipart/form-data, field name = 'file')
    → chạy pipeline run_ocr → trả về JSON thông tin CCCD.
    """
    try:
        # 1. Đọc bytes từ file upload
        content = await file.read()

        # 2. Lưu tạm ra file để tái sử dụng hàm run_ocr(img_path)
        # suffix để OpenCV đoán được loại file (.jpg, .png, ...)
        suffix = os.path.splitext(file.filename)[1] or ".jpg"
        with tempfile.NamedTemporaryFile(delete=False, suffix=suffix) as tmp:
            tmp.write(content)
            tmp_path = tmp.name

        # 3. Gọi pipeline OCR hiện tại
        info, texts = run_ocr(tmp_path)

        # 4. Xoá file tạm
        try:
            os.remove(tmp_path)
        except OSError:
            pass

        # 5. Trả kết quả dạng JSON
        return JSONResponse(
            content={
                "filename": file.filename,
                "data": info,  # {'fullName', 'gender', 'dateOfBirth', 'address', ...}
                "texts": texts  # Danh sách các đoạn text nhận dạng được
            }
        )

    except Exception as e:
        return JSONResponse(
            status_code=500,
            content={"error": str(e)}
        )
