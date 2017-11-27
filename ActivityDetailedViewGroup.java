package com.politics.karma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActivityDetailedViewGroup extends AppCompatActivity {


    // ===== VARS
    private List<ItemPost> postList;
    private List<ItemGroup> groupList;
    private List postIdList, groupIdList;
    private String userId, postId, postUsername, postDate, postContent, postGroupName,
            postCreator, groupName, groupDescription, groupCreator, groupId;
    private long followers, following, posts, reputation, groupsNumber;
    private long postLikes, postDislikes, postCommentsNumber, groupMembers, groupPosts;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference postsRef, groupsRef, usersRef, postsUserRef, groupsUserRef;
    private TextView groupNameTextView, groupDescriptionTextView, groupMembersTextView, postsNumberTextView;
    private ToggleButton joinButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view_group);


        // ===== VARS
        postList = new ArrayList<ItemPost>();
        groupList = new ArrayList<ItemGroup>();
        postIdList = new ArrayList();
        groupIdList = new ArrayList();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        userId = user.getUid();

        usersRef = db.getInstance().getReference().child("users");
        postsRef = db.getInstance().getReference().child("posts");
        postsUserRef = db.getInstance().getReference().child("posts_user");
        groupsRef = db.getInstance().getReference().child("groups");
        groupsUserRef = db.getInstance().getReference().child("groups_user");


        groupNameTextView = (TextView) findViewById(R.id.detailedViewGroupGroupNameTextView);
        groupDescriptionTextView = (TextView) findViewById(R.id.detailedViewGroupGroupDescriptionTextView);
        groupMembersTextView = (TextView) findViewById(R.id.detailedViewGroupGroupMembersTextView);
        postsNumberTextView = (TextView) findViewById(R.id.detailedViewGroupGroupPostsTextView);
        joinButton = (ToggleButton) findViewById(R.id.detailedViewGroupJoinButton);


        // ===== POST ID
        Bundle bundle = getIntent().getExtras();
        groupId = bundle.getString("groupName");


        // ===== JOIN BUTTON STATE
        groupsRef.child(groupId).child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    joinButton.setChecked(true);
                }
                else {
                    joinButton.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ===== JOIN BUTTON
        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (joinButton.isChecked()) {
                    joinButton.setChecked(true);

                    groupsRef.child(groupId).child("users").child(userId).setValue(" ");
                    groupsUserRef.child(userId).child(groupId).setValue(" ");

                    // ===== INC MEMBER COUNT
                    groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long users = dataSnapshot.child("users_number").getValue(long.class);
                            users += 1;
                            groupsRef.child(groupId).child("users_number").setValue(users);

                            // ===== INC GROUPS COUNT
                            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long groups = dataSnapshot.child("groups").getValue(long.class);
                                    groups += 1;
                                    usersRef.child(userId).child("groups").setValue(groups);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                else {
                    Log.d("user", "follow button unchecking");
                    joinButton.setChecked(false);

                    groupsRef.child(groupId).child("users").child(userId).removeValue();
                    groupsUserRef.child(userId).child(groupId).removeValue();

                    // ===== DEC MEMBER COUNT
                    groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            long users = dataSnapshot.child("users_number").getValue(long.class);
                            users -= 1;
                            groupsRef.child(groupId).child("users_number").setValue(users);

                            // ===== INC GROUPS COUNT
                            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long groups = dataSnapshot.child("groups").getValue(long.class);
                                    groups -= 1;
                                    usersRef.child(userId).child("groups").setValue(groups);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });


        // ===== POST
        groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    groupCreator = dataSnapshot.child("creator").getValue(String.class);
                    groupDescription = dataSnapshot.child("description").getValue(String.class);
                    groupMembers = dataSnapshot.child("users_number").getValue(long.class);
                    groupPosts = dataSnapshot.child("posts_number").getValue(long.class);

                }

                groupNameTextView.setText(String.valueOf(groupId));
                groupDescriptionTextView.setText(String.valueOf(groupDescription));
                groupMembersTextView.setText(String.valueOf(groupMembers + "\nmembers"));
                postsNumberTextView.setText(String.valueOf(groupPosts + "\nposts"));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



        // ===== POST IDs
        groupsRef.child(groupId).child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String key = d.getKey();
                    postIdList.add(key);
                }


                // ===== POST INFO
                for (int i = 0; i<postIdList.size(); i++) {
                    postId = String.valueOf(postIdList.get(i));
                    Log.d("user", postId);

                    // ===== POST
                    postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                postId = dataSnapshot.getKey();
                                postUsername = dataSnapshot.child("username").getValue(String.class);
                                postCreator = dataSnapshot.child("creator").getValue(String.class);
                                postDate = dataSnapshot.child("date").getValue(String.class);
                                postContent = dataSnapshot.child("content").getValue(String.class);
                                postLikes = dataSnapshot.child("likes").getValue(long.class);
                                postDislikes = dataSnapshot.child("dislikes").getValue(long.class);
                                postCommentsNumber = dataSnapshot.child("comments").getValue(long.class);
                                postGroupName = dataSnapshot.child("group_name").getValue(String.class);

                            }

                            ItemPost post = new ItemPost(postId, postCreator, postUsername, postDate, postContent,
                                    postGroupName, postLikes, postDislikes, postCommentsNumber);
                            postList.add(0, post);

                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.detailedViewGroupRecyclerView);
                            recyclerView.setNestedScrollingEnabled(false);
                            AdapterPosts adapter = new AdapterPosts(postList);
                            recyclerView.setAdapter(adapter);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            recyclerView.setLayoutManager(layoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }
}
