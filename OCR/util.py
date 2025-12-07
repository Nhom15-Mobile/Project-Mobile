def find_boxes(items, keywords):
    """Tìm box chứa 1 trong các keyword."""
    return [
        it for it in items
        if any(kw.lower() in it["text"].lower() for kw in keywords)
    ]

def box_height(box):
    ys = [p[1] for p in box]
    return max(ys) - min(ys)


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

def extract_gender_country(items):
    boxes = find_boxes(items, ["Giới tính", "Sex"])
    if not boxes:
        return None, None

    text = boxes[0]["text"].replace("  ", " ")

    # tách đơn giản: gender là "Nam"/"Nữ", country là "Việt Nam"
    gender = None
    for g in ["Nam", "Nữ", "Male", "Female"]:
        if g in text:
            gender = g
            break

    country = None
    for c in ["Việt Nam", "Vietnam", "Viet Nam"]:
        if c in text:
            country = c
            break

    return gender, country

def extract_address(items):
# 1. Tìm label "Nơi thường trú"
    def contains(text, kws):
        t = text.lower()
        return any(kw.lower() in t for kw in kws)

    label_candidates = [
        it for it in items
        if contains(it["text"], ["Nơi thường trú", "Place of residence"])
    ]
    if not label_candidates:
        return None

    label = label_candidates[0]
    cy_label = label["cy"]
    cx_label = label["cx"]

    # 2. Sort toàn bộ box theo thứ tự đọc (trên xuống, trái sang phải)
    sorted_items = sorted(items, key=lambda it: (it["cy"], it["cx"]))

    # 3. Tìm index của label trong danh sách đã sort
    try:
        label_idx = next(i for i, it in enumerate(sorted_items)
                            if it is label)
    except StopIteration:
        return None

    # 4. Lấy các box xuất hiện SAU label, nằm bên phải label,
    #    trong một vùng dưới label (tránh ăn trúng các dòng trên)
    candidates = []
    for it in sorted_items[label_idx+1:]:
        # chỉ lấy các box ở dưới (hoặc ngang một chút) và nằm bên phải
        if it["cy"] < cy_label - 5:
            continue
        if it["cx"] < cx_label - 20:
            continue

        # loại các label khác
        if contains(it["text"], [
            "Quê quán", "Place of origin",
            "Có giá trị", "Date of expiry",
            "CĂN CƯỚC CÔNG DÂN", "Citizen Identity Card"
        ]):
            continue

        candidates.append(it)

    if not candidates:
        return None

    # 5. Thường địa chỉ CCCD có 1–2 dòng. Lấy tối đa 2 box đầu tiên.
    #    (đã sort theo cy,cx nên sẽ là dòng trên rồi dòng dưới)
    top_candidates = candidates[:2]

    parts = [c["text"].strip(" ,.") for c in top_candidates if c["text"].strip()]
    if not parts:
        return None

    # Ghép lại, tránh dấu phẩy trùng
    address = ", ".join(parts)
    return address
