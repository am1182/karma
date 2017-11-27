package com.politics.karma;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AdapterGroups extends RecyclerView.Adapter<AdapterGroups.GroupsViewHolder> {


    private List<ItemGroup> groupList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference postsRef, groupsRef, usersRef, postsUserRef, commentsRef;
    private String userId, creator, postUsername, groupName;
    private long postsNumberUser, postsNumberGroup, reputation;


    // ===== VIEWHOLDER
    public static class GroupsViewHolder extends RecyclerView.ViewHolder {

        private TextView name, description, members, posts;
        private Button menuButton;

        public GroupsViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.cardviewGroupNameTextView);
            description = (TextView) v.findViewById(R.id.cardviewGroupDescriptionTextView);
            members = (TextView) v.findViewById(R.id.cardviewGroupMembersTextView);
            posts = (TextView) v.findViewById(R.id.cardviewGroupPostsTextView);
            //menuButton = (Button) v.findViewById(R.id.cardviewGroupExpandButton);

        }
    }


    // ===== ADAPTER
    public AdapterGroups(List<ItemGroup> groupList){
        this.groupList = groupList;
    }


    // ===== ON CREATE VIEWHOLDER
    public AdapterGroups.GroupsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview_group, viewGroup, false);
        return new AdapterGroups.GroupsViewHolder(itemView);
    }


    // ===== ITEM COUNT
    @Override
    public int getItemCount() {
        return groupList.size();
    }


    // ===== ON BIND VIEWHOLDER
    @Override
    public void onBindViewHolder(final AdapterGroups.GroupsViewHolder viewHolder, int i) {
        final ItemGroup group = groupList.get(i);
        viewHolder.name.setText(String.valueOf(group.getName()));
        viewHolder.description.setText(String.valueOf(group.getDescription()));
        viewHolder.members.setText(String.valueOf(group.getMembers()) + "\nmembers");
        viewHolder.posts.setText(String.valueOf(group.getPosts()) + "\nposts");


        // ===== GROUP NAME ONCLICK
        viewHolder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityDetailedViewGroup.class);

                // ===== PASS ID TO CLASS
                groupName = String.valueOf(group.getName());
                Bundle bundle = new Bundle();
                bundle.putString("groupName", groupName);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                Log.d("user", groupName);
            }
        });


        // ===== VARS
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        usersRef = db.getInstance().getReference().child("users");
        postsRef = db.getInstance().getReference().child("posts");
        postsUserRef = db.getInstance().getReference().child("posts_user");
        groupsRef = db.getInstance().getReference().child("groups");
        commentsRef = db.getInstance().getReference().child("comments");


    }
}
