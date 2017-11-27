package com.politics.karma;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterNotificationsFollow extends RecyclerView.Adapter<AdapterNotificationsFollow.NotificationsViewHolder> {


    List<ItemNotification> notificationsList;
    private String senderId;


    // ===== VIEWHOLDER
    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {
        public TextView message;

        public NotificationsViewHolder(View v) {
            super(v);
            message = (TextView) v.findViewById(R.id.cardviewNotificationMessageTextView);
        }
    }


    // ===== ADAPTER
    public AdapterNotificationsFollow(List<ItemNotification> notificationsList){
        this.notificationsList = notificationsList;
    }


    // ===== ONCREATE VIEWHOLDER
    public AdapterNotificationsFollow.NotificationsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview_notification, viewGroup, false);
        return new NotificationsViewHolder(itemView);
    }


    // ===== ON BIND
    @Override
    public void onBindViewHolder(NotificationsViewHolder holder, int i) {
        final ItemNotification notification = notificationsList.get(i);
        holder.message.setText(String.valueOf(notification.getMessage()));


        // ===== ON CLICK
        senderId = notification.getSenderId();
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ActivityProfileUser.class);

                // ===== PASS ID TO CLASS
                Bundle bundle = new Bundle();
                bundle.putString("userId", senderId);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }


    // ===== ITEM COUNT
    @Override
    public int getItemCount() {
        return notificationsList.size();
    }
}
