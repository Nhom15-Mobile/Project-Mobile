package com.example.records.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.records.R;
import com.example.records.model.Record;
import com.google.android.material.card.MaterialCardView;


import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.RecordViewHolder> {
    public interface RecordListener{
        void onRecordClicked(Record record);
    }
    List<Record> recordList;
    RecordListener listener;

    public RecordsAdapter(List<Record> recordList, RecordListener listener) {
        this.recordList = recordList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_record, parent, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        Record record = recordList.get(position);

        holder.tvName.setText(record.getName());
        holder.tvId.setText(record.getId());
        holder.tvPhone.setText(record.getPhone());

        holder.card.setOnClickListener(v -> {
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onRecordClicked(record);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (recordList != null){
            return recordList.size();
        }
        return 0;
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvId, tvPhone;

        MaterialCardView card;
        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.name);
            tvId = itemView.findViewById(R.id.id);
            tvPhone = itemView.findViewById(R.id.phone);

            card = itemView.findViewById(R.id.btnProfileCard);
        }
    }
}
