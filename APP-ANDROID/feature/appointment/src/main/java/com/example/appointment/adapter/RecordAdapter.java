package com.example.appointment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.ItemRecord;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class RecordAdapter extends RecyclerView.Adapter<RecordAdapter.RecordViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ItemRecord item);
    }
    Context context;
    List<ItemRecord> items;
    private OnItemClickListener listener;

    public RecordAdapter(Context context, List<ItemRecord> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnItemClickListener(OnItemClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecordViewHolder(LayoutInflater.from(context).inflate
                (R.layout.item_view_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, int position) {
        ItemRecord it = items.get(position);
        holder.name.setText(it.getName());
        holder.idRecord.setText(it.getId());
        holder.phone.setText(it.getPhone());

        holder.card.setOnClickListener(v ->{
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onItemClick(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {
        TextView name, idRecord, phone;
        MaterialCardView card;

        public RecordViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            idRecord = itemView.findViewById(R.id.id);
            phone = itemView.findViewById(R.id.phone);
            card = itemView.findViewById(R.id.btnProfileCard);
        }
    }
}
