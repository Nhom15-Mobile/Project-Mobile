package com.example.notification.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notification.R;
import com.example.notification.data.NotificationItem;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.VH> {

    private List<NotificationItem> list;
    public interface OnItemClick {
        void onClick(NotificationItem item);
    }
    private final OnItemClick listener;

    public NotificationAdapter(List<NotificationItem> list, OnItemClick listener) {
        this.list = list;
        this.listener = listener;
    }

    public static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvTime, badgeNew;

        public VH(@NonNull View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvContent = v.findViewById(R.id.tvContent);
            tvTime = v.findViewById(R.id.tvTime);
            badgeNew = v.findViewById(R.id.badgeNew);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        NotificationItem item = list.get(position);

        h.tvTitle.setText(item.title);
        h.tvContent.setText(item.content);
        h.tvTime.setText(item.time);
        h.badgeNew.setVisibility(item.isNew ? View.VISIBLE : View.GONE);

        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
