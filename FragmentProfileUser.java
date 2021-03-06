package com.politics.karma;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


public class FragmentProfileUser extends Fragment {


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


    private OnFragmentInteractionListener mListener;
    public FragmentProfileUser() {}


    public static FragmentProfileUser newInstance(String userId) {
        FragmentProfileUser fragment = new FragmentProfileUser();
        Bundle args = new Bundle();
        args.putString("userId", userId);
        fragment.setArguments(args);
        return fragment;
    }


    // ===== ON CREATE
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    // ===== ON CREATE VIEW
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_user, container, false);
    }


    // ===== ON ACTIVITY CREATED
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // ===== GET ARGS
        Bundle args = getArguments();
        userId = args.getString("userId");


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

        usernameTextView = (TextView) getActivity().findViewById(R.id.profileUserUsernameTextView);
        bioTextView = (TextView) getActivity().findViewById(R.id.profileUserBioTextView);
        followersTextView = (TextView) getActivity().findViewById(R.id.profileUserFollowersTextView);
        followingTextView = (TextView) getActivity().findViewById(R.id.profileUserFollowingTextView);
        //postsTextView = (TextView) getActivity().findViewById(R.id.homePostsTextView);
        //reputationTextView = (TextView) getActivity().findViewById(R.id.homeReputationTextView);
        postsNumberTextView = (TextView) getActivity().findViewById(R.id.profileUserPostsNumberTextView);
        groupsNumberTextView = (TextView) getActivity().findViewById(R.id.profileUserGroupsNumberTextView);

        followingRef = db.getInstance().getReference().child("following");
        followersRef = db.getInstance().getReference().child("followers");
        followButton = (ToggleButton) getActivity().findViewById(R.id.profileUserFollowButton);


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


        // ===== FOLLOW BUTTON ONCLICK
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (followButton.isChecked()) {
                    followButton.setChecked(true);
                    Log.d("USER", "follow button is checking");

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
                                    usersRef.child(userId).child("following").setValue(following);
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
                                    usersRef.child(userId).child("following").setValue(following);
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


        // ===== TABS
        final TabHost tabHost = (TabHost) getActivity().findViewById(R.id.fragmentHomeTabHost);
        tabHost.setFocusableInTouchMode(false);
        tabHost.setFocusable(false);
        tabHost.setup();

        // POSTS
        TabHost.TabSpec spec1 = tabHost.newTabSpec("posts");
        spec1.setContent(R.id.homePostsTab);
        spec1.setIndicator("posts");
        tabHost.addTab(spec1);

        // GROUPS
        TabHost.TabSpec spec2 = tabHost.newTabSpec("groups");
        spec2.setContent(R.id.homeGroupsTab);
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
                //reputationTextView.setText(reputation + "\nreputation");

                postsNumberTextView.setText(posts + " posts");
                groupsNumberTextView.setText(groupsNumber + " groups");
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

                            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.profileUserPostsRecyclerView);
                            recyclerView.setNestedScrollingEnabled(false);
                            AdapterPosts adapter = new AdapterPosts(postList);
                            recyclerView.setAdapter(adapter);

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
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

                            RecyclerView groupRecyclerView = (RecyclerView) getActivity().findViewById(R.id.profileUserGroupsRecyclerView);
                            AdapterGroups groupAdapter = new AdapterGroups(groupList);
                            groupRecyclerView.setNestedScrollingEnabled(false);
                            groupRecyclerView.setAdapter(groupAdapter);

                            groupRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

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


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
