package com.example.appointment.model;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.google.android.material.card.MaterialCardView;

public class SpecialtyViewHolder  extends RecyclerView.ViewHolder {
    TextView name, price;
    MaterialCardView card;

    public SpecialtyViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.tvSpecialtyName);
        price = itemView.findViewById(R.id.tvPrice);

        card = itemView.findViewById(R.id.cardSpecialty);
    }
}
