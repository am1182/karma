package com.politics.karma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
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

public class ActivityProfileUser extends AppCompatActivity {


    // ===== VARS
    private List<ItemPost> postList;
    private List<ItemGroup> groupList;
    private List postIdList, groupIdList;
    private String userId, postId, postUsername, postDate, postContent, postGroupName,
            postCreator, groupName, groupDescription, groupCreator, currentUserId;
    private String username, bio;
    private long followers, following, posts, reputation, groupsNumber;
    private long postLikes, postDislikes, postCommentsNumber, groupMembers, groupPosts;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference postsRef, groupsRef, usersRef, postsUserRef, groupsUserRef, followersRef, followingRef;
    private TextView usernameTextView, bioTextView, followersTextView,
            followingTextView, postsTextView, reputationTextView, groupsNumberTextView, postsNumberTextView;
    ToggleButton followButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_user);


        // ===== GET ARGS
        userId = getIntent().getStringExtra("userId");


        // ===== VARS
        postList = new ArrayList<ItemPost>();
        groupList = new ArrayList<ItemGroup>();
        postIdList = new ArrayList();
        groupIdList = new ArrayList();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        currentUserId = user.getUid();

        usersRef = db.getInstance().getReference().child("users");
        postsRef = db.getInstance().getReference().child("posts");
        postsUserRef = db.getInstance().getReference().child("posts_user");
        groupsRef = db.getInstance().getReference().child("groups");
        groupsUserRef = db.getInstance().getReference().child("groups_user");

        usernameTextView = (TextView) findViewById(R.id.profileUserUsernameTextView);
        bioTextView = (TextView) findViewById(R.id.profileUserBioTextView);
        followersTextView = (TextView) findViewById(R.id.profileUserFollowersTextView);
        followingTextView = (TextView) findViewById(R.id.profileUserFollowingTextView);
        //postsTextView = (TextView) getActivity().findViewById(R.id.homePostsTextView);
        reputationTextView = (TextView) findViewById(R.id.profileUserReputationTextView);
        postsNumberTextView = (TextView) findViewById(R.id.profileUserPostsNumberTextView);
        groupsNumberTextView = (TextView) findViewById(R.id.profileUserGroupsNumberTextView);

        followingRef = db.getInstance().getReference().child("following");
        followersRef = db.getInstance().getReference().child("followers");
        followButton = (ToggleButton) findViewById(R.id.profileUserFollowButton);


        // ===== SET BUTTON STATE
        followingRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    followButton.setChecked(true);
                }
                else {
                    followButton.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ===== TABS
        final TabHost tabHost = (TabHost) findViewById(R.id.fragmentHomeTabHost);
        tabHost.setFocusableInTouchMode(false);
        tabHost.setFocusable(false);
        tabHost.setup();

        // POSTS
        TabHost.TabSpec spec1 = tabHost.newTabSpec("posts");
        spec1.setContent(R.id.profileUserPostsTab);
        spec1.setIndicator("posts");
        tabHost.addTab(spec1);

        // GROUPS
        TabHost.TabSpec spec2 = tabHost.newTabSpec("groups");
        spec2.setContent(R.id.profileUserGroupsTab);
        spec2.setIndicator("groups");
        tabHost.addTab(spec2);


        // ===== PROFILE DETAILS
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                username = dataSnapshot.child("username").getValue(String.class);
                bio = dataSnapshot.child("bio").getValue(String.class);
                followers = dataSnapshot.child("followers").getValue(long.class);
                following = dataSnapshot.child("following").getValue(long.class);
                posts = dataSnapshot.child("posts").getValue(long.class);
                reputation = dataSnapshot.child("reputation").getValue(long.class);
                groupsNumber = dataSnapshot.child("groups").getValue(long.class);

                usernameTextView.setText(username);
                bioTextView.setText(bio);
                followersTextView.setText(followers + "\nfollowers");
                followingTextView.setText(following + "\nfollowing");
                //postsTextView.setText(posts + "\nposts");
                reputationTextView.setText(reputation + "\nkarma");

                postsNumberTextView.setText(posts + " posts");
                groupsNumberTextView.setText(groupsNumber + " groups");


                // ===== FOLLOW BUTTON ONCLICK
                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (followButton.isChecked()) {
                            followButton.setChecked(true);

                            followingRef.child(currentUserId).child(userId).setValue(" ");
                            followersRef.child(userId).child(currentUserId).setValue(" ");

                            // ===== INC FOLLOWER COUNT
                            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long followers = dataSnapshot.child("followers").getValue(long.class);
                                    followers += 1;
                                    usersRef.child(userId).child("followers").setValue(followers);

                                    // ===== INC FOLLOWING COUNT
                                    usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            long following = dataSnapshot.child("following").getValue(long.class);
                                            following += 1;
                                            usersRef.child(currentUserId).child("following").setValue(following);
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });





                            // ===== NOTIFICATION
                            if (userId.equals(currentUserId)) {
                                // do nothing
                            }
                            else {
                                usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        // MESSAGE
                                        String currentUserUsername = dataSnapshot.child("username").getValue(String.class);
                                        String notificationMessage = currentUserUsername + " followed you";
                                        DatabaseReference notificationsRef = db.getInstance().getReference().child("notifications_follow");

                                        // DATABASE
                                        String key = notificationsRef.child(userId).push().getKey();
                                        notificationsRef.child(userId).child(key).child("message").setValue(notificationMessage);
                                        notificationsRef.child(userId).child(key).child("sender_id").setValue(currentUserId);


                                        // ===== NOTIFICATION
                                        Intent intent = new Intent(ActivityProfileUser.this, ActivityMain.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        PendingIntent pendingIntent = PendingIntent.getActivity(ActivityProfileUser.this, 0 /* Request code */, intent,
                                                PendingIntent.FLAG_ONE_SHOT);

                                        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ActivityProfileUser.this)
                                                .setSmallIcon(R.drawable.ic_button_like_red)
                                                //.setContentTitle("Notification")
                                                .setContentText(notificationMessage)
                                                .setAutoCancel(true)
                                                .setSound(defaultSoundUri)
                                                .setContentIntent(pendingIntent);

                                        notificationBuilder.build();
                                        NotificationManager notificationManager = (NotificationManager) getSystemService(ActivityProfileUser.this.NOTIFICATION_SERVICE);
                                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                            }




                        }
                        else {
                            followButton.setChecked(false);
                            followingRef.child(currentUserId).child(userId).removeValue();
                            followersRef.child(userId).child(currentUserId).removeValue();

                            // ===== DEC FOLLOWER COUNT
                            usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    long followers = dataSnapshot.child("followers").getValue(long.class);
                                    followers -= 1;
                                    usersRef.child(userId).child("followers").setValue(followers);

                                    // ===== DEC FOLLOWING COUNT
                                    usersRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            long following = dataSnapshot.child("following").getValue(long.class);
                                            following -= 1;
                                            usersRef.child(currentUserId).child("following").setValue(following);
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ===== FOLLOW BUTTON
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // ===== POST IDs
        postsUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            //Collections.reverse(postList);

                            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.profileUserPostsRecyclerView);
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


        // ===== GROUP IDs
        groupsUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String groupKey = d.getKey();
                    groupIdList.add(groupKey);
                    Log.d("user", groupKey);
                }


                // ===== GROUP INFO
                for (int i = 0; i<groupIdList.size(); i++) {
                    final String groupId = String.valueOf(groupIdList.get(i));

                    // ===== GROUP
                    groupsRef.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                groupCreator = dataSnapshot.child("creator").getValue(String.class);
                                groupDescription = dataSnapshot.child("description").getValue(String.class);
                                groupMembers = dataSnapshot.child("users_number").getValue(long.class);
                                groupPosts = dataSnapshot.child("posts_number").getValue(long.class);

                            }

                            ItemGroup group = new ItemGroup(groupId, groupDescription, groupCreator, groupPosts, groupMembers);
                            groupList.add(group);

                            RecyclerView groupRecyclerView = (RecyclerView) findViewById(R.id.profileUserGroupsRecyclerView);
                            AdapterGroups groupAdapter = new AdapterGroups(groupList);
                            groupRecyclerView.setNestedScrollingEnabled(false);
                            groupRecyclerView.setAdapter(groupAdapter);

                            groupRecyclerView.setLayoutManager(new GridLayoutManager(ActivityProfileUser.this, 2));

                            //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                            //groupRecyclerView.setLayoutManager(layoutManager);
                            groupRecyclerView.setItemAnimator(new DefaultItemAnimator());

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
