package com.example.records.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.records.R;
import com.example.records.model.AppointmentHistoryItem;

import java.util.ArrayList;
import java.util.List;

public class AppointmentHistoryAdapter
        extends RecyclerView.Adapter<AppointmentHistoryAdapter.HistoryViewHolder> {

    private List<AppointmentHistoryItem> items = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AppointmentHistoryItem item);
    }

    public AppointmentHistoryAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<AppointmentHistoryItem> data) {
        items = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment_history_1, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AppointmentHistoryItem item = items.get(position);
        holder.bind(item, listener);
    }

    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvDoctorName, tvDateTime, tvStatus, tvSpecialty, tvPaymentStatus, tvClinic;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvPaymentStatus = itemView.findViewById(R.id.tvPaymentStatus);
            tvClinic = itemView.findViewById(R.id.tvClinic);
        }

        public void bind(final AppointmentHistoryItem item,
                         final OnItemClickListener listener) {

            tvDoctorName.setText(item.getDoctorName());
            tvSpecialty.setText(item.getSpecialtyName());
            tvDateTime.setText(item.getDate() + " • " + item.getTime());
            tvStatus.setText(convertStatusToLabel(item.getStatus()));
            tvPaymentStatus.setText(item.getPaymentStatus());
            tvClinic.setText(item.getClinicName());

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }

        private String convertStatusToLabel(String status) {
            if ("UPCOMING".equals(status)) return "Chưa khám";
            if ("DONE".equals(status)) return "Đã khám";
            //if ("CANCELED".equals(status)) return "Đã huỷ";
            return status;
        }
    }
}
