package com.example.auth.ui.dialog; // đổi cho đúng

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.auth.R; // đổi cho đúng
import com.google.android.material.button.MaterialButton;

public class OtpDialog extends DialogFragment {

    public interface OtpCallback {
        void onOtpSubmit(String otp);
        void onResendClicked();
    }

    private final OtpCallback callback;
    private CountDownTimer countDownTimer;
    private TextView tvResend;
    private EditText[] otpFields;

    public OtpDialog(OtpCallback callback) {
        this.callback = callback;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ❗ Không cho cancel khi bấm Back hoặc chạm bên ngoài
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_otp, null);

        // Ánh xạ view
        EditText et1 = view.findViewById(R.id.etOtp1);
        EditText et2 = view.findViewById(R.id.etOtp2);
        EditText et3 = view.findViewById(R.id.etOtp3);
        EditText et4 = view.findViewById(R.id.etOtp4);
        EditText et5 = view.findViewById(R.id.etOtp5);
        EditText et6 = view.findViewById(R.id.etOtp6);
        //tvResend = view.findViewById(R.id.tvResend);
        MaterialButton btnConfirm = view.findViewById(R.id.btnConfirm);

        otpFields = new EditText[]{et1, et2, et3, et4, et5, et6};

        setupOtpAutoMove();
        //startResendCountDown();

        btnConfirm.setOnClickListener(v -> {
            String otp = getOtpFromFields();
            if (otp.length() == 6) {
                if (callback != null) callback.onOtpSubmit(otp);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đủ 6 số", Toast.LENGTH_SHORT).show();
            }
        });

//        tvResend.setOnClickListener(v -> {
//            if (tvResend.isEnabled()) {
//                if (callback != null) callback.onResendClicked();
//                startResendCountDown(); // reset countdown
//            }
//        });

        builder.setView(view);
        Dialog dialog = builder.create();

        // Tăng độ mờ nền phía sau dialog
        dialog.setOnShowListener(d -> {
            Window window = dialog.getWindow();
            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                WindowManager.LayoutParams lp = window.getAttributes();
                lp.dimAmount = 0.7f;
                window.setAttributes(lp);
                window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            }
        });

        dialog.setCanceledOnTouchOutside(false);

        return dialog;
    }

    private void setupOtpAutoMove() {
        for (int i = 0; i < otpFields.length; i++) {
            final int index = i;
            otpFields[i].addTextChangedListener(new TextWatcher() {
                private String beforeText = "";

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    beforeText = s.toString();
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    if (text.length() == 1) {
                        // move to next
                        if (index < otpFields.length - 1) {
                            otpFields[index + 1].requestFocus();
                        }
                    } else if (text.length() == 0 && beforeText.length() == 1) {
                        // user pressed delete, move back
                        if (index > 0) {
                            otpFields[index - 1].requestFocus();
                        }
                    }
                }
            });
        }
    }

    private String getOtpFromFields() {
        StringBuilder sb = new StringBuilder();
        for (EditText et : otpFields) {
            sb.append(et.getText().toString().trim());
        }
        return sb.toString();
    }

//    private void startResendCountDown() {
//        // tắt timer cũ nếu có
//        if (countDownTimer != null) {
//            countDownTimer.cancel();
//        }
//
//        tvResend.setEnabled(false);
//
//        countDownTimer = new CountDownTimer(60000, 1000) { // 60s
//            @Override
//            public void onTick(long millisUntilFinished) {
//                long second = millisUntilFinished / 1000;
//                tvResend.setText("Gửi lại mã (" + second + "s)");
//            }
//
//            @Override
//            public void onFinish() {
//                tvResend.setText("Gửi lại mã");
//                tvResend.setEnabled(true);
//            }
//        };
//        countDownTimer.start();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
