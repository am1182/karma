package com.politics.karma;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AdapterComments extends RecyclerView.Adapter<AdapterComments.CommentsViewHolder> {


    List<ItemComment> commentsList;


    // ===== VIEWHOLDER
    public static class CommentsViewHolder extends RecyclerView.ViewHolder {
        public TextView commentUsername, commentDate, commentContent;

        public CommentsViewHolder(View v) {
            super(v);
            commentUsername = (TextView) v.findViewById(R.id.cardviewCommentUsernameTextView);
            commentDate = (TextView) v.findViewById(R.id.cardviewCommentDateTextView);
            commentContent = (TextView) v.findViewById(R.id.cardviewCommentContentTextView);
        }
    }


    // ===== ADAPTER
    public AdapterComments(List<ItemComment> commentsList){
        this.commentsList = commentsList;
    }


    // ===== ONCREATE VIEWHOLDER
    public AdapterComments.CommentsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview_comment, viewGroup, false);
        return new CommentsViewHolder(itemView);
    }


    // ===== ON BIND
    @Override
    public void onBindViewHolder(CommentsViewHolder holder, int i) {
        final ItemComment comment = commentsList.get(i);
        holder.commentUsername.setText(String.valueOf(comment.getCommentUsername()));
        holder.commentDate.setText(String.valueOf(comment.getCommentDate()));
        holder.commentContent.setText(String.valueOf(comment.getCommentContent()));
    }


    // ===== ITEM COUNT
    @Override
    public int getItemCount() {
        return commentsList.size();
    }
}
