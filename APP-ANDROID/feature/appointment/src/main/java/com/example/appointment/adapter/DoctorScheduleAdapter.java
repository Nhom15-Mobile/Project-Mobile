package com.example.appointment.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.DoctorSchedule;
import com.example.appointment.model.TimeSlot;
import java.util.*;

public class DoctorScheduleAdapter extends RecyclerView.Adapter<DoctorScheduleAdapter.VH> {

    public interface OnSelectSlot {
        void onSelected(DoctorSchedule doctor, TimeSlot slot);
    }

    private final List<DoctorSchedule> data;
    private final RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private final OnSelectSlot listener;

    public DoctorScheduleAdapter(List<DoctorSchedule> data, OnSelectSlot listener) {
        this.data = data; this.listener = listener;
    }

    @NonNull
    @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int v) {
        View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_doctor_card, p, false);
        return new VH(view);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        DoctorSchedule d = data.get(pos);
        h.tvDoctorName.setText(d.doctorName);
        h.tvDate.setText(d.dateText);
        h.tvLocation.setText(d.location);

        GridLayoutManager lm = new GridLayoutManager(h.rv.getContext(), 2);
        h.rv.setLayoutManager(lm);
        h.rv.setHasFixedSize(true);
        h.rv.setRecycledViewPool(viewPool);


        TimeSlotAdapter child = new TimeSlotAdapter(d.slots, (slot, slotPos) -> {
            if (listener != null) listener.onSelected(d, slot);
        });
        h.rv.setAdapter(child);
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDate, tvLocation;
        RecyclerView rv;
        VH(@NonNull View v) {
            super(v);
            tvDoctorName = v.findViewById(R.id.tvDoctorName);
            tvDate       = v.findViewById(R.id.tvDate);
            tvLocation   = v.findViewById(R.id.tvLocation);
            rv           = v.findViewById(R.id.rvTimeSlots);
        }
    }
}

