package com.example.appointment.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;

public class RecordViewHolder extends RecyclerView.ViewHolder {
    TextView name, idRecord, phone;

    public RecordViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        idRecord = itemView.findViewById(R.id.id);
        phone = itemView.findViewById(R.id.phone);
    }
}
