package com.example.appointment.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.google.android.material.card.MaterialCardView;

public class RecordViewHolder extends RecyclerView.ViewHolder {
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
