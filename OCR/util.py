def find_boxes(items, keywords):
    """Tìm box chứa 1 trong các keyword."""
    return [
        it for it in items
        if any(kw.lower() in it["text"].lower() for kw in keywords)
    ]

def box_height(box):
    ys = [p[1] for p in box]
    return max(ys) - min(ys)

def contains(text, kws):
    t = text.lower()
    return any(kw.lower() in t for kw in kws)


def extract_full_name(items):
    labels = find_boxes(items, ["Họ và tên", "Full name"])
    if not labels:
        return None

    label = labels[0]
    h = box_height(label["poly"])
    cy_label = label["cy"]

    # Chọn các box nằm dưới label (dưới tối đa ~1.5 lần chiều cao)
    cands = [
        it for it in items
        if cy_label + 0.5*h <= it["cy"] <= cy_label + 2*h
    ]

    if not cands:
        return None

    # Lấy box gần đường thẳng dọc của label nhất (cùng cột)
    cands.sort(key=lambda it: abs(it["cx"] - label["cx"]))
    return cands[0]["text"]

import re

def extract_dob(items):
    boxes = find_boxes(items, ["Ngày sinh", "Date of birth"])
    if not boxes:
        return None

    text = boxes[0]["text"]
    m = re.search(r"\b(\d{2}/\d{2}/\d{4})\b", text)
    if m:
        return m.group(1)

    # Nếu ngày nằm box bên phải/bên dưới
    label = boxes[0]
    h = box_height(label["poly"])
    cy_label = label["cy"]

    cands = [
        it for it in items
        if cy_label - 0.5*h <= it["cy"] <= cy_label + 1.5*h
        and it["cx"] > label["cx"]
    ]
    if not cands:
        return None

    cands.sort(key=lambda it: it["cx"])
    for it in cands:
        m = re.search(r"\b(\d{2}/\d{2}/\d{4})\b", it["text"])
        if m:
            return m.group(1)

    return None

# def extract_gender_country(items):
#     boxes = find_boxes(items, ["Giới tính", "Sex"])
#     if not boxes:
#         return None, None

#     text = boxes[0]["text"].replace("  ", " ")

#     # tách đơn giản: gender là "Nam"/"Nữ", country là "Việt Nam"
#     gender = None
#     for g in ["Nam", "Nữ", "Male", "Female"]:
#         if g in text:
#             gender = g
#             break

#     country = None
#     for c in ["Việt Nam", "Vietnam", "Viet Nam"]:
#         if c in text:
#             country = c
#             break

#     return gender, country

def extract_gender_country(items):
    gender = None
    country = None

    # 1. Box có "Giới tính / Sex"
    sex_boxes = [it for it in items
                 if contains(it["text"], ["Giới tính", "Sex"])]
    if sex_boxes:
        sex_box = sex_boxes[0]
        t = sex_box["text"]

        for g in ["Nam", "Nữ", "Male", "Female"]:
            if g in t:
                gender = g
                break

        for c in ["Việt Nam", "Vietnam", "Viet Nam"]:
            if c in t:
                country = c
                break
    else:
        sex_box = None

    # 2. Nếu chưa có country, xem phần "Quốc tịch / Nationality"
    nat_boxes = [it for it in items
                 if contains(it["text"], ["Quốc tịch", "Nationality"])]
    if nat_boxes:
        nat_box = nat_boxes[0]
        t = nat_box["text"]

        # thử lấy luôn trong cùng box
        for c in ["Việt Nam", "Vietnam", "Viet Nam"]:
            if c in t:
                country = c
                break

        # nếu vẫn chưa có, lấy box bên phải cùng dòng
        if country is None:
            h = box_height(nat_box["poly"])
            cy0 = nat_box["cy"]
            cx0 = nat_box["cx"]

            same_line = [
                it for it in items
                if abs(it["cy"] - cy0) < 0.6 * h and it["cx"] > cx0
            ]
            same_line.sort(key=lambda it: it["cx"])

            for it in same_line:
                t2 = it["text"]
                for c in ["Việt Nam", "Vietnam", "Viet Nam"]:
                    if c in t2:
                        country = c
                        break
                if country is not None:
                    break

    # 3. Nếu vẫn chưa có gender và sex_box tồn tại,
    #    tìm box bên phải "Giới tính"
    if gender is None and sex_box is not None:
        h = box_height(sex_box["poly"])
        cy0 = sex_box["cy"]
        cx0 = sex_box["cx"]

        same_line = [
            it for it in items
            if abs(it["cy"] - cy0) < 0.6 * h and it["cx"] > cx0
        ]
        same_line.sort(key=lambda it: it["cx"])

        if same_line:
            t2 = same_line[0]["text"]
            for g in ["Nam", "Nữ", "Male", "Female"]:
                if g in t2:
                    gender = g
                    break

    return gender, country


def extract_address(items):
    # 1. Tìm box label chứa "Nơi thường trú"
    label_candidates = [
        it for it in items
        if contains(it["text"], ["Nơi thường trú", "Place of residence"])
    ]
    if not label_candidates:
        return None

    # nếu có nhiều box, ưu tiên box có text dài hơn
    label = max(label_candidates, key=lambda it: len(it["text"]))

    poly = label["poly"]
    xs = [p[0] for p in poly]
    ys = [p[1] for p in poly]
    x_min_lbl, x_max_lbl = min(xs), max(xs)
    h_lbl = box_height(poly)
    cy_lbl = label["cy"]
    cx_lbl = label["cx"]

    # --------- DÒNG 1: trong cùng box hoặc box bên phải cùng dòng ---------
    line1 = None

    # TH1: trong cùng box, lấy phần sau dấu ":"
    m = re.search(r":\s*(.+)", label["text"])
    if m and m.group(1).strip():
        line1 = m.group(1).strip(" ,.")

    # TH2: không có trong cùng box -> lấy box bên phải cùng dòng
    if not line1:
        same_row = [
            it for it in items
            if abs(it["cy"] - cy_lbl) <= 0.6 * h_lbl and it["cx"] > cx_lbl + 5
        ]
        same_row.sort(key=lambda it: it["cx"])
        if same_row:
            line1 = same_row[0]["text"].strip(" ,.")

    # --------- DÒNG 2: box nằm ngay phía dưới ---------
    # chỉ lấy các box dưới label và nằm "cùng cột" (tránh ăn trúng "Có giá trị đến")
    margin = (x_max_lbl - x_min_lbl) * 0.7  # cho phép rộng thêm hai bên
    below = [
        it for it in items
        if it["cy"] > cy_lbl + 0.6 * h_lbl   # phải ở dưới một chút
        and (x_min_lbl - margin) <= it["cx"] <= (x_max_lbl + margin)
    ]

    below.sort(key=lambda it: it["cy"])  # box gần nhất phía dưới
    line2 = below[0]["text"].strip(" ,.") if below else None

    # --------- GHÉP KẾT QUẢ ---------
    if line1 and line2:
        return f"{line1}, {line2}"
    elif line1:
        return line1
    elif line2:
        return line2
    else:
        return None
