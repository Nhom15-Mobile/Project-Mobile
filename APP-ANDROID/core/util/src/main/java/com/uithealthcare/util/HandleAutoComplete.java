package com.uithealthcare.util;

import android.widget.ArrayAdapter;

import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.List;

public class HandleAutoComplete {
    public static <T> void setupDropDown(MaterialAutoCompleteTextView view, List<T> items) {
        ArrayAdapter<T> adapter =
                new ArrayAdapter<>(view.getContext(), android.R.layout.simple_list_item_1, items);

        view.setAdapter(adapter);

        // số ký tự tối thiểu trước khi filter (1 = gõ 1 chữ là filter, 0 = luôn filter)
        view.setThreshold(1);

        // Ấn vào là xổ xuống ngay
        view.setOnClickListener(v -> {
            if (!view.isPopupShowing()) {
                view.showDropDown();
            }
        });

        // Khi focus vào ô cũng tự dropdown (nhìn pro hơn)
        view.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && !view.isPopupShowing()) {
                view.showDropDown();
            }
        });
    }
}
