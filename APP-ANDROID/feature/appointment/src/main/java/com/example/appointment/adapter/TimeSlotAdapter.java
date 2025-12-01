package com.example.appointment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.model.TimeSlot;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

import com.example.appointment.R;

public class TimeSlotAdapter extends RecyclerView.Adapter<TimeSlotAdapter.SlotVH> {

    public interface OnSlotClick {
        void onSlotSelected(TimeSlot slot, int position);
    }

    private final List<TimeSlot> data = new ArrayList<>();
    private final OnSlotClick listener;
    private int selectedPos = RecyclerView.NO_POSITION;

    public TimeSlotAdapter(List<TimeSlot> slots, OnSlotClick listener) {
        if (slots != null) data.addAll(slots);
        this.listener = listener;
    }

    @NonNull @Override
    public SlotVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_time_slot, parent, false);
        return new SlotVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SlotVH holder, int position) {
        TimeSlot slot = data.get(position);
        holder.btn.setText(slot.time);
        holder.btn.setEnabled(slot.available);
        holder.btn.setChecked(slot.selected);

        holder.btn.setOnClickListener(v -> {
            if (!slot.available) return;

            // đơn chọn: bỏ chọn cũ
            int old = selectedPos;
            if (old != RecyclerView.NO_POSITION && old < data.size()) {
                data.get(old).selected = false;
                notifyItemChanged(old);
            }
            slot.selected = true;
            selectedPos = holder.getAdapterPosition();
            notifyItemChanged(selectedPos);

            if (listener != null) listener.onSlotSelected(slot, selectedPos);
        });
    }

    @Override public int getItemCount() { return data.size(); }

    public static class SlotVH extends RecyclerView.ViewHolder {
        MaterialButton btn;
        public SlotVH(@NonNull View itemView) {
            super(itemView);
            btn = itemView.findViewById(R.id.btnSlot);
        }
    }
}

