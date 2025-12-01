package com.example.appointment.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.adapter.AppointmentHistoryAdapter;
import com.example.appointment.api.AppointmentService;
import com.example.appointment.model.AppointmentHistoryItem;
import com.uithealthcare.domain.appointment.AppointmentData;
import com.uithealthcare.domain.appointment.AppointmentHistoryResponse;
import com.uithealthcare.network.ApiServices;
import com.uithealthcare.network.SessionInterceptor;
import com.uithealthcare.util.SessionManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentHistoryListFragment extends Fragment {

    private static final String ARG_STATUS = "ARG_STATUS";

    public static AppointmentHistoryListFragment newInstance(String status) {
        AppointmentHistoryListFragment fragment = new AppointmentHistoryListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_STATUS, status);
        fragment.setArguments(args);
        return fragment;
    }

    // UPCOMING / DONE / CANCELED (3 tab)
    private String statusType;

    private RecyclerView rvHistory;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private AppointmentHistoryAdapter adapter;

    private AppointmentService appointmentService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_appointment_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            statusType = getArguments().getString(ARG_STATUS, "UPCOMING");
        }

        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        // luôn đảm bảo adapter được tạo
        if (adapter == null) {
            adapter = new AppointmentHistoryAdapter(item -> {
                // TODO: mở màn chi tiết lịch hẹn nếu muốn
            });
        }

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);

        SessionManager manager = new SessionManager(requireContext());

        SessionInterceptor.TokenProvider tokenProvider = new SessionInterceptor.TokenProvider() {
                    @Override
                    public String getToken() {
                        return manager.getBearer();
                    }
                };

        appointmentService =
                ApiServices.create(AppointmentService.class, tokenProvider);

        loadData();
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        appointmentService.getAppointments()
                .enqueue(new Callback<AppointmentHistoryResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<AppointmentHistoryResponse> call,
                                           @NonNull Response<AppointmentHistoryResponse> response) {

                        // Fragment đã detach rồi thì thôi, khỏi xử lý tiếp
                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);

                        AppointmentHistoryResponse body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                            showEmpty();
                            return;
                        }

                        List<AppointmentData> dtoList = body.getData();
                        if (dtoList == null || dtoList.isEmpty()) {
                            showEmpty();
                            return;
                        }

                        List<AppointmentHistoryItem> items = new ArrayList<>();

                        for (AppointmentData dto : dtoList) {
                            if (!shouldShowOnThisTab(dto)) continue;
                            items.add(mapToHistoryItem(dto));
                        }

                        if (items.isEmpty()) {
                            showEmpty();
                        } else if (adapter != null) {
                            adapter.setData(items);
                            tvEmpty.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<AppointmentHistoryResponse> call,
                                          @NonNull Throwable t) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);
                        showEmpty();
                    }
                });
    }

    private void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
        if (adapter != null) {
            adapter.setData(new ArrayList<>());
        }
    }

    private Date parseUtcToDate(String isoUtc) {
        if (isoUtc == null) return null;

        try {
            SimpleDateFormat isoFormat =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return isoFormat.parse(isoUtc);  // Date là thời điểm tuyệt đối, so với new Date() được
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * chia appointment ra ở tab nào
     */
    private boolean shouldShowOnThisTab(AppointmentData dto) {
        // 1. Chỉ hiện lịch ĐÃ THANH TOÁN
        String payStatus = dto.getPaymentStatus();
        if (payStatus == null || !payStatus.equalsIgnoreCase("PAID")) {
            return false;
        }

        // 2. Lấy thời gian bắt đầu ca khám
        Date start = parseUtcToDate(dto.getScheduledAt());
        if (start == null) return false;

        // 3. Ca khám kéo dài 1 tiếng -> tính thời gian kết thúc
        Date end = new Date(start.getTime() + 60 * 60 * 1000);

        Date now = new Date();

        boolean isBeforeStart = now.before(start);  // chưa đến giờ khám
        boolean isAfterEnd = now.after(end);        // đã qua giờ kết thúc khám

        switch (statusType) {
            case "UPCOMING":   // Tab "Chưa khám"
                return isBeforeStart;

            case "DONE":       // Tab "Đã khám"
                return isAfterEnd;

            case "CANCELED":   // Tạm thời chưa xử lý
                return false;

            default:
                return false;
        }
    }


    /**
     * Map dữ liệu API -> item cho RecyclerView
     */
    /**
     * Map dữ liệu API -> item cho RecyclerView
     */
    private AppointmentHistoryItem mapToHistoryItem(AppointmentData dto) {
        AppointmentHistoryItem item = new AppointmentHistoryItem();

        item.setAppointmentId(dto.getId());

        if (dto.getDoctor() != null) {
            item.setDoctorName(dto.getDoctor().getFullName());
        } else {
            item.setDoctorName("Bác sĩ");
        }

        item.setSpecialtyName(dto.getService());

        String[] dateTime = convertUtcToVnDateTime(dto.getScheduledAt());
        item.setDate(dateTime[0]); // dd/MM/yyyy
        item.setTime(dateTime[1]); // HH:mm

        item.setClinicName("");

        // --- Set status UI dựa trên thời gian khám ---
        Date start = parseUtcToDate(dto.getScheduledAt());
        if (start != null) {
            Date end = new Date(start.getTime() + 60 * 60 * 1000); // ca khám 1 tiếng
            Date now = new Date();

            boolean isBeforeStart = now.before(start);
            boolean isAfterEnd = now.after(end);

            if (isBeforeStart) {
                item.setStatus("UPCOMING");   // Chưa khám
            } else if (isAfterEnd) {
                item.setStatus("DONE");       // Đã khám
            }
        }

        // Thanh toán
        if ("PAID".equalsIgnoreCase(dto.getPaymentStatus())) {
            item.setPaymentStatus("Đã thanh toán");
        } else {
            item.setPaymentStatus("Chưa thanh toán");
        }

        return item;
    }


    /**
     * Convert ISO UTC -> giờ VN, trả về [date, time]
     */
    private String[] convertUtcToVnDateTime(String isoUtc) {
        if (isoUtc == null) return new String[]{"", ""};

        SimpleDateFormat isoFormat =
                new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        isoFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        SimpleDateFormat dateFormat =
                new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat =
                new SimpleDateFormat("HH:mm", Locale.getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));

        try {
            Date date = isoFormat.parse(isoUtc);
            if (date != null) {
                return new String[]{
                        dateFormat.format(date),
                        timeFormat.format(date)
                };
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new String[]{"", ""};
    }
}
