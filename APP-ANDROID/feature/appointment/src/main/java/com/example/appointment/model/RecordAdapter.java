package com.example.appointment.model;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordViewHolder> {
    Context context;
    List<ItemRecord> items;

    public RecordAdapter(Context context, List<ItemRecord> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        holder.name.setText(items.get(position).getName());
        holder.idRecord.setText(items.get(position).getId());
        holder.phone.setText(items.get(position).getPhone());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
