package com.example.appointment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;
import com.example.appointment.model.ItemSpecialty;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class SpecialtyAdapter extends RecyclerView.Adapter<SpecialtyAdapter.SpecialtyViewHolder> {
    public interface onSpecialtyClickListener {
        void onSpecialtyClick(ItemSpecialty item);
    }
    Context context;
    List<ItemSpecialty> items;
    private onSpecialtyClickListener listener;

    public SpecialtyAdapter(Context context, List<ItemSpecialty> items) {
        this.context = context;
        this.items = items;
    }

    public void setOnSpecialtyClickListener(onSpecialtyClickListener l) {
        this.listener = l;
    }

    @NonNull
    @Override
    public SpecialtyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SpecialtyViewHolder(LayoutInflater.from(context).inflate
                (R.layout.item_view_specialty, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialtyViewHolder holder, int position) {
        ItemSpecialty it = items.get(position);
        holder.name.setText(it.getName());
        holder.price.setText(it.getPrice());


        holder.card.setOnClickListener(v ->{
            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                listener.onSpecialtyClick(it);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class SpecialtyViewHolder  extends RecyclerView.ViewHolder {
        TextView name, price;
        MaterialCardView card;

        public SpecialtyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvSpecialtyName);
            price = itemView.findViewById(R.id.tvPrice);

            card = itemView.findViewById(R.id.cardSpecialty);
        }
    }
}
