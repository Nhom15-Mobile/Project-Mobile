package com.example.notification.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notification.R;
import com.example.notification.data.NotificationApi;
import com.example.notification.data.NotificationItem;
import com.example.notification.data.NotificationRepository;
import com.example.notification.data.ReceiptRepository;
import com.example.notification.data.ReceiptApi;

import java.util.ArrayList;
import java.util.List;

public class NotificationFragment extends Fragment {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private final List<NotificationItem> uiList = new ArrayList<>();
    private NotificationRepository repo;
    private ReceiptRepository receiptrepo;

    public NotificationFragment() {
        // bắt buộc constructor rỗng
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.notification, container, false);

        rvNotifications = v.findViewById(R.id.rvNotifications);

        repo = new NotificationRepository(requireContext());
        receiptrepo = new ReceiptRepository(requireContext());

        // adapter dùng list uiList + listener click
        adapter = new NotificationAdapter(uiList, this::handleItemClick);
        rvNotifications.setAdapter(adapter);

        loadNotifications();

        return v;
    }

    private void loadNotifications() {
        // 20 bản
        repo.getNotifications(null, 20, null, new NotificationRepository.CallbackList() {
            @Override
            public void onSuccess(NotificationApi.Data data) {
                uiList.clear();

                if (data != null && data.items != null) {
                    for (NotificationApi.NotificationDto dto : data.items) {
                        uiList.add(mapDtoToItem(dto));
                    }
                }

                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> adapter.notifyDataSetChanged());
                }
            }

            @Override
            public void onError(String message) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // map 1 notification từ server -> item hiển thị trên UI
    private NotificationItem mapDtoToItem(NotificationApi.NotificationDto dto) {
        String timeText = formatTime(dto.createdAt);
        boolean isNew = (dto.readAt == null);

        NotificationItem item = new NotificationItem(
                dto.title,
                dto.body,
                timeText,
                isNew,
                0
        );
        item.type = dto.type;   // ⭐️ quan trọng

        if (dto.data != null) {
            item.appointmentId = dto.data.appointmentId;
            item.service       = dto.data.service;
            item.scheduledAt   = dto.data.scheduledAt;
        }
        return item;
    }
    private void handleItemClick(NotificationItem item) {
        if ("PAYMENT_SUCCESS".equals(item.type)) {
            // click vào thông báo thanh toán → gọi API phiếu khám
            if (item.appointmentId == null) {
                Toast.makeText(getContext(), "Không tìm thấy mã lịch khám", Toast.LENGTH_SHORT).show();
                return;
            }

            receiptrepo.getReceipt(item.appointmentId, new ReceiptRepository.ReceiptCallback() {
                @Override
                public void onSuccess(ReceiptApi.Receipt r) {
                    if (getContext() == null) return;

                    Intent i = new Intent(getContext(), ReceiptDetailActivity.class);
                    i.putExtra("receiptNo",   r.receiptNo);
                    i.putExtra("patientName", r.patientName);
                    i.putExtra("specialty",   r.specialty);
                    i.putExtra("examDate",    r.examDate);

                    if (r.examTime != null) {
                        i.putExtra("examStart", r.examTime.start);
                        i.putExtra("examEnd",   r.examTime.end);
                    }

                    i.putExtra("clinicRoom",  r.clinicRoom);
                    i.putExtra("amount",      r.amount);
                    i.putExtra("bookedAt",    r.bookedAt);

                    startActivity(i);
                }

                @Override
                public void onError(String msg) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            // các loại thông báo khác: sau này xử lý tiếp
        }
    }


    // format createdAt: "2025-11-26T08:47:37.809Z" -> "2025-11-26 08:47"
    private String formatTime(String iso) {
        if (iso == null) return "";
        String s = iso.replace('T', ' ');
        // cắt bớt phần giây + mili cho gọn
        if (s.length() >= 16) {
            return s.substring(0, 16);
        }
        return s;
    }
}
