package com.example.appointment.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appointment.R;

import java.util.List;

public class SpecialtyAdapter extends RecyclerView.Adapter<SpecialtyViewHolder> {

    Context context;
    List<ItemSpecialty> items;


    public SpecialtyAdapter(Context context, List<ItemSpecialty> items) {
        this.context = context;
        this.items = items;
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


//        holder.card.setOnClickListener(v ->{
//            if (listener != null && holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
//                listener.onItemClick(it);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
