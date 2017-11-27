package com.politics.karma;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class AdapterNotificationsComment extends RecyclerView.Adapter<AdapterNotificationsComment.NotificationsCommentViewHolder> {

    List<ItemNotification> notificationsList;
    private String postId;


    // ===== VIEWHOLDER
    public static class NotificationsCommentViewHolder extends RecyclerView.ViewHolder {
        public TextView message;

        public NotificationsCommentViewHolder(View v) {
            super(v);
            message = (TextView) v.findViewById(R.id.cardviewNotificationMessageTextView);
        }
    }


    // ===== ADAPTER
    public AdapterNotificationsComment(List<ItemNotification> notificationsList){
        this.notificationsList = notificationsList;
    }


    // ===== ONCREATE VIEWHOLDER
    public AdapterNotificationsComment.NotificationsCommentViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview_notification, viewGroup, false);
        return new NotificationsCommentViewHolder(itemView);
    }


    // ===== ON BIND
    @Override
    public void onBindViewHolder(NotificationsCommentViewHolder holder, int i) {
        final ItemNotification notification = notificationsList.get(i);
        holder.message.setText(String.valueOf(notification.getMessage()));


        // ===== ON CLICK
        postId = notification.getPostId();
        holder.message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityDetailedViewPost.class);

                // ===== PASS ID TO CLASS
                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
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
