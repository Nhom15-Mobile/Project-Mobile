package com.example.records.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;   // <-- nhớ import
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

import com.example.records.R;
import com.example.records.adapter.AppointmentHistoryAdapter;
import com.example.records.api.AppointmentService;
import com.example.records.model.AppointmentHistoryItem;
import com.uithealthcare.domain.appointment.AppointmentData;
import com.uithealthcare.domain.appointment.AppointmentHistoryResponse;
import com.uithealthcare.domain.payment.Payment;
import com.uithealthcare.domain.payment.PaymentMeta;
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
    private static final String ARG_CARE_ID = "ARG_CARE_ID";
    private static final String TAG = "HIS_DEBUG";

    private String careProfileId;

    public static AppointmentHistoryListFragment newInstance(String status,String careProfileId) {
        AppointmentHistoryListFragment fragment = new AppointmentHistoryListFragment();
        Bundle args = new Bundle();

        args.putString(ARG_STATUS, status);
        args.putString(ARG_CARE_ID, careProfileId);

        fragment.setArguments(args);
        return fragment;
    }

    // UPCOMING / DONE
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
        return inflater.inflate(R.layout.list_appointment_fragment_1, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            statusType = getArguments().getString(ARG_STATUS, "UPCOMING");
            careProfileId = getArguments().getString(ARG_CARE_ID);

        }

        Log.d(TAG, "Fragment " + statusType + " nhận careProfileId = " + careProfileId);

        rvHistory = view.findViewById(R.id.rvHistory);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        if (adapter == null) {
            adapter = new AppointmentHistoryAdapter(item -> {
                if (getContext() == null) return;

                Intent intent = new Intent(getContext(), DetailHistoryActivity.class);
                intent.putExtra("EXTRA_APPOINTMENT_ITEM", item);
                startActivity(intent);
            });
        }

        rvHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        rvHistory.setAdapter(adapter);

        SessionManager manager = new SessionManager(requireContext());

        SessionInterceptor.TokenProvider tokenProvider = () -> manager.getBearer();

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

                        if (!isAdded()) return;

                        progressBar.setVisibility(View.GONE);

                        AppointmentHistoryResponse body = response.body();
                        if (!response.isSuccessful() || body == null || !body.isSuccess()) {
                            showEmpty();
                            return;
                        }

                        List<AppointmentData> dtoList = body.getData();
                        Log.d(TAG, "Tổng số lịch backend trả về = " + (dtoList == null ? 0 : dtoList.size()));
                        if (dtoList == null || dtoList.isEmpty()) {
                            showEmpty();
                            return;
                        }

                        List<AppointmentHistoryItem> items = new ArrayList<>();

                        for (AppointmentData dto : dtoList) {
                            boolean matchCare = matchesCareProfile(dto);
                            boolean matchTab = shouldShowOnThisTab(dto);

                            Log.d(TAG,
                                    "Check item id=" + dto.getId()
                                            + " careIdApi=" + dto.getCareProfileId()
                                            + " status=" + dto.getStatus()
                                            + " payStatus=" + dto.getPaymentStatus()
                                            + " -> matchCare=" + matchCare
                                            + ", matchTab(" + statusType + ")=" + matchTab);

                            if (!matchCare) continue;
                            if (!matchTab) continue;

                            items.add(mapToHistoryItem(dto));
                        }

                        Log.d(TAG, "Số item sau filter cho tab " + statusType + " = " + items.size());

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

    private boolean matchesCareProfile(AppointmentData dto) {
        if (careProfileId == null || careProfileId.isEmpty()) {
            // không filter theo profile khi vào từ màn chính
            return true;
        }

        String dtoCareId = dto.getCareProfileId();

        if (dtoCareId == null && dto.getCareProfile() != null) {
            dtoCareId = dto.getCareProfile().getId();
        }

        if (dtoCareId == null) return false;

        return careProfileId.equals(dtoCareId);
    }

    private void showEmpty() {
        tvEmpty.setVisibility(View.VISIBLE);
        if (adapter != null) {
            adapter.setData(new ArrayList<>());
        }
    }

    /**
     * Chia appointment ra tab nào:
     * - Chỉ lấy thanh toán PAID
     * - CONFIRMED  -> tab UPCOMING (Chưa khám)
     * - COMPLETED  -> tab DONE (Đã khám)
     */
    private boolean shouldShowOnThisTab(AppointmentData dto) {
        String payStatus = dto.getPaymentStatus();
        if (payStatus == null || !payStatus.equalsIgnoreCase("PAID")) {
            return false;
        }

        String rawStatus = dto.getStatus();
        if (rawStatus == null) return false;

        rawStatus = rawStatus.toUpperCase(Locale.ROOT);

        switch (statusType) {
            case "UPCOMING":   // Tab "Chưa khám"
                return "CONFIRMED".equals(rawStatus);

            case "DONE":       // Tab "Đã khám"
                return "COMPLETED".equals(rawStatus);

            default:
                return false;
        }
    }

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
        item.setDate(dateTime[0]);
        item.setTime(dateTime[1]);

        item.setClinicName("");

        String rawStatus = dto.getStatus();
        if (rawStatus != null) {
            rawStatus = rawStatus.toUpperCase(Locale.ROOT);
            if ("CONFIRMED".equals(rawStatus)) {
                item.setStatus("UPCOMING");
            } else if ("COMPLETED".equals(rawStatus)) {
                item.setStatus("DONE");
            } else {
                item.setStatus(rawStatus);
            }
        }

        if (dto.getCareProfile() != null) {
            item.setPatientName(dto.getCareProfile().getFullName());
        } else {
            item.setPatientName("");
        }

        if ("PAID".equalsIgnoreCase(dto.getPaymentStatus())) {
            item.setPaymentStatus("Đã thanh toán");
        } else {
            item.setPaymentStatus("Chưa thanh toán");
        }

        String[] created = convertUtcToVnDateTime(dto.getCreatedAt());
        item.setCreateAt(created[0] + " " + created[1]);

        Payment payment = dto.getPayment();
        if (payment != null) {
            item.setAmount(payment.getAmount());
            PaymentMeta meta = payment.getMeta();
            if (meta != null) {
                item.setTransId(meta.getTransId());
            }
        }

        return item;
    }

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
