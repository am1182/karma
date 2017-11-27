package com.politics.karma;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.UsersViewHolder> {

    List<ItemUser> usersList;

    // ===== VIEWHOLDER
    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        public TextView userUsername, userFollowers, userFollowing, userPosts, userGroups;
        public LinearLayout linearLayout;

        public UsersViewHolder(View v) {
            super(v);

            userUsername = (TextView) v.findViewById(R.id.cardviewUserUsernameTextView);
            userFollowers = (TextView) v.findViewById(R.id.cardviewUserFollowersTextView);
            userFollowing = (TextView) v.findViewById(R.id.cardviewUserFollowingTextView);
            userPosts = (TextView) v.findViewById(R.id.cardviewUserPostsTextView);
            userGroups = (TextView) v.findViewById(R.id.cardviewUserGroupsTextView);

            linearLayout = (LinearLayout) v.findViewById(R.id.cardviewUserLinearLayout);
        }
    }


    // ===== ADAPTER
    public AdapterUsers(List<ItemUser> usersList){
        this.usersList = usersList;
    }


    // ===== ONCREATE VIEWHOLDER
    public AdapterUsers.UsersViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview_user, viewGroup, false);
        return new AdapterUsers.UsersViewHolder(itemView);
    }


    // ===== ON BIND
    @Override
    public void onBindViewHolder(AdapterUsers.UsersViewHolder holder, int i) {
        ItemUser user = usersList.get(i);

        holder.userUsername.setText(String.valueOf(user.getUsername()));
        holder.userFollowers.setText(String.valueOf(user.getFollowers() + " followers"));
        holder.userFollowing.setText(String.valueOf(user.getFollowing() + " following"));
        holder.userPosts.setText(String.valueOf(user.getPosts()) + " posts");
        holder.userGroups.setText(String.valueOf(user.getGroups() + " groups"));


        // ===== ON CLICK
        final String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final String userId = user.getUserId();

        if (currentUserId.equals(userId)) {
            // do nothing
        }

        else {
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ActivityProfileUser.class);

                    // ===== PASS ID TO CLASS
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", userId);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }


    // ===== ITEM COUNT
    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
