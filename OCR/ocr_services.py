from paddleocr import TextDetection
import matplotlib.pyplot as plt
from PIL import Image

from vietocr.tool.predictor import Predictor
from vietocr.tool.config import Cfg
from util import *;

import numpy as np
import cv2
import json
import os
import shutil
import re

def load_recognizer_model():
    config = Cfg.load_config_from_name('vgg_seq2seq')

    config['weights'] = './models/vgg_seq2seq.pth'
    config['cnn']['pretrained']=False
    config['device'] = 'cpu'

    recognizer = Predictor(config)
    return recognizer

def recognize(recognizer: Predictor, src: str):
    texts = []   # lưu các dòng text theo thứ tự bbox

    for image_path in os.listdir(src):
        full_path = os.path.join(src, image_path)

        img = Image.open(full_path)
        s = recognizer.predict(img)   # giả sử model trả về string

        texts.append(s.strip())


    # folder_path = os.path.dirname(src)

    # if os.path.exists(folder_path):
    #     shutil.rmtree(folder_path)
    #     print("✔ Folder deleted!")
    # else:
    #     print("❌ Folder does not exist!")

    return texts

def detect(image: np.ndarray):

    model = TextDetection(model_name="PP-OCRv5_mobile_det")
    output = model.predict(image)
    for res in output:
        res.print()
        res.save_to_img(save_path="./output/")
        res.save_to_json(save_path="./output/res.json")

def crop_image(image: np.array, 
               json_path: str, 
               save_path: str):
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    h, w = image.shape[:2]
    dt_polys = data["dt_polys"]   # list các bbox (4 điểm)
    os.makedirs(save_path, exist_ok=True)
    for idx, poly in enumerate(dt_polys):
        poly = np.array(poly, dtype=np.int32)

        # Lấy min/max để tạo bounding box chuẩn
        x_min = max(0, np.min(poly[:, 0]))
        y_min = max(0, np.min(poly[:, 1]))
        x_max = min(w, np.max(poly[:, 0]))
        y_max = min(h, np.max(poly[:, 1]))

        crop = image[y_min:y_max, x_min:x_max]

        out_path = os.path.join(save_path, f"crop_{idx:03d}.png")
        cv2.imwrite(out_path, crop)

    print(f"Đã cắt {len(dt_polys)} ảnh và lưu vào thư mục: {save_path}")


    t = text.lower()
    addr_keywords = [
        "phường", "phuong",
        "xã ", "xa ",
        "ấp", "ap ",
        "thôn", "thon",
        "quận", "quan",
        "huyện", "huyen",
        "thành phố", "thanh pho",
        "tỉnh", "tinh",
        "đường", "duong",
    ]
    if any(k in t for k in addr_keywords):
        return True
    # nhiều trường hợp địa chỉ có nhiều dấu phẩy
    if "," in text and len(text.split()) >= 3:
        return True
    return False


    full_name = None
    country = None
    gender = None
    date_of_birth = None
    address = None

    lower_lines = [l.lower() for l in lines]

    # ==== FULL NAME ====
    for i, txt in enumerate(lower_lines):
        if "họ và tên" in txt or "ho va ten" in txt or "full name" in txt:
            if i > 0:
                cand = lines[i-1].strip(" :")
                if cand:
                    full_name = cand
            if not full_name and i + 1 < len(lines):
                full_name = lines[i+1].strip(" :")
            break

    if not full_name:
        for t in lines:
            if len(t.split()) >= 2 and t == t.upper() and not any(c.isdigit() for c in t):
                full_name = t.strip()
                break

    # ==== GENDER & COUNTRY ====
    for t in lines:
        t_low = t.lower()
        if "giới tính" in t_low or "gioi tinh" in t_low or "sex" in t_low:
            if re.search(r"\bnam\b", t_low):
                gender = "Nam"
            elif "nữ" in t_low or re.search(r"\bnu\b", t_low):
                gender = "Nữ"

            if "việt nam" in t_low or "viet nam" in t_low or "vietnam" in t_low:
                country = "Việt Nam"
            break

    # ==== DATE OF BIRTH ====
    date_pat = re.compile(r"(\d{2}/\d{2}/\d{4})")

    for t in lines:
        t_low = t.lower()
        if "ngày sinh" in t_low or "ngay sinh" in t_low or "date of birth" in t_low:
            m = date_pat.search(t)
            if m:
                date_of_birth = m.group(1)
                break

    if not date_of_birth:
        for t in lines:
            m = date_pat.search(t)
            if m:
                date_of_birth = m.group(1)
                break

    # ==== ADDRESS (Nơi thường trú) ====
    # 1) Lấy dòng có "Nơi thường trú / Place of residence"
    for i, t in enumerate(lines):
        if "Nơi thường trú" in t or "Place of residence" in t:
            label_idx = i
            # cắt từ ký tự số đầu tiên -> lấy "117/10B Lê Thi"
            m = re.search(r"\d", t)
            if m:
                main_addr = t[m.start():].strip()
            else:
                main_addr = t.strip()
            break
            # 2) Tìm dòng tiếp theo có chữ "Phường / Quận / Huyện / Xã / Thị xã / TP / Thành phố"
    continuation = ""
    if label_idx is not None:
        for t in lines:
            if t == lines[label_idx]:
                continue
            # bỏ các dòng label khác
            if any(k in t for k in ['Có giá trị', 'Date of expiry',
                                    'Quê quán', 'Place of origin']):
                continue
            if re.search(r"(Phường|Quận|Huyện|Xã|Thị trấn|Thị xã|TP\.?|Thành phố)", t):
                continuation = t.strip()
                break

    if main_addr:
        addr = (main_addr + " " + continuation).strip()
        # fix một số lỗi OCR hay gặp
        addr = addr.replace("Le Thi ", "Le Thị ").replace("Lê Thi ", "Lê Thị ")
        address = addr

    return {
        "fullName": full_name,
        "country": country,
        "gender": gender,
        "dateOfBirth": date_of_birth,
        "address": address,
    }



def recog(img_path: str,
          detect_json_path: str,
          output_json_path: str,
          save_path: str = "./temp_crops"):
    
    # 1) Load ảnh + file detect
    img = cv2.imread(img_path)
    h, w = img.shape[:2]
    with open(detect_json_path, "r", encoding="utf-8") as f:
        det_res = json.load(f)

    polys = det_res["dt_polys"]           # list 4 điểm
    # det_scores = det_res["dt_scores"]     # cùng chiều dài

    items = []

    for idx, poly in enumerate(polys):
        poly_np = np.array(poly, dtype=np.int32)

        # ===== CROP THEO CÁCH CỦA BẠN =====
        x_min = max(0, np.min(poly_np[:, 0]))
        y_min = max(0, np.min(poly_np[:, 1]))
        x_max = min(w, np.max(poly_np[:, 0]))
        y_max = min(h, np.max(poly_np[:, 1]))

        crop = img[y_min:y_max, x_min:x_max]


        # 3) Nhận dạng text từ crop
        # TODO: thay bằng hàm rec thực tế của bạn
        recognizer = load_recognizer_model()
        img_crop = Image.fromarray(cv2.cvtColor(crop, cv2.COLOR_BGR2RGB))

        text = recognizer.predict(img_crop)
        

        # 4) Tính tâm box
        xs = [p[0] for p in poly]
        ys = [p[1] for p in poly]
        cx = sum(xs) / 4.0
        cy = sum(ys) / 4.0

        items.append({
            "poly": poly,
            "text": text,
            "cx": cx,
            "cy": cy
        })

    # 5) Lưu lại file JSON mới
    full_res = {
        "filename": img_path,
        "items": items
    }

    with open(output_json_path, "w", encoding="utf-8") as f:
        json.dump(full_res, f, ensure_ascii=False, indent=2)
    print(f"Đã lưu JSON đầy đủ vào: {output_json_path}")
    






def run_ocr(img_path: str,
            out_put: str = "out.json"):
    img = cv2.imread(img_path)
    detect(img)
    recog(img_path=img_path,
          detect_json_path="./output/res.json",
          output_json_path=out_put)
    
    with open(out_put, "r", encoding="utf-8") as f:
        data = json.load(f)

    items = data["items"]

    info = {
        "full_name": "",
        "country": "",
        "gender": "",
        "date_of_birth": "",
        "address": "",
    }

    
    info["full_name"] = extract_full_name(items)
    info["date_of_birth"]       = extract_dob(items)
    info["gender"], info["country"] = extract_gender_country(items)
    info["address"]   = extract_address(items)

    print(info)
    folder_path = "./output"

    if os.path.exists(folder_path):
        shutil.rmtree(folder_path)
        print("✔ Folder deleted!")
    else:
        print("❌ Folder does not exist!")

    return info, items



if __name__ == "__main__":
    run_ocr(img_path="./data/cccd.jpg",
            out_put="./test.json")


    