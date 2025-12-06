package com.example.results.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.results.R;
import com.uithealthcare.domain.result.ResultData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    public interface OnItemClickListener {
        void onClick(ResultData item);
    }

    private List<ResultData> list;
    private OnItemClickListener listener;

    public ResultAdapter(List<ResultData> list, OnItemClickListener listener) {
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultData item = list.get(position);

        // Tiêu đề: dùng service (Khám tổng quát)
        holder.tvTitle.setText(item.getService());

        // Bác sĩ
        if (item.getDoctor() != null) {
            holder.tvDoctor.setText(item.getDoctor().getFullName());
        } else {
            holder.tvDoctor.setText("Bác sĩ");
        }

        // Ngày
        holder.tvDate.setText(formatDate(item.getScheduledAt()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    public void updateData(List<ResultData> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDoctor, tvDate;

        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvResultTitle);
            tvDoctor = itemView.findViewById(R.id.tvDoctorName);
            tvDate = itemView.findViewById(R.id.tvResultDate);
        }
    }

    private String formatDate(String iso) {
        if (iso == null) return "";
        try {
            // 2025-12-16T06:30:00.000Z
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
            Date date = input.parse(iso);
            return output.format(date);
        } catch (ParseException e) {
            return iso;
        }
    }
}
