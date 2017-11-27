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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FragmentExplore extends Fragment {


    // ===== VARS
    private List<ItemPost> postList;
    private List<ItemGroup> groupList;
    private List<ItemUser> userList;
    private List postIdList, groupIdList;
    private String userId, postId, postUsername, postDate, postContent, postGroupName,
            postCreator, groupName, groupDescription, groupCreator;
    private long postLikes, postDislikes, postCommentsNumber, groupMembers, groupPosts;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference postsRef, groupsRef, usersRef, postsUserRef, commentsRef;


    private FragmentExplore.OnFragmentInteractionListener mListener;
    public FragmentExplore() {}


    // ===== NEW INSTANCE
    public static FragmentExplore newInstance() {
        FragmentExplore fragment = new FragmentExplore();
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
        return inflater.inflate(R.layout.fragment_explore, container, false);
    }


    // ===== ON ACTIVITY CREATED
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // ===== VARS
        postList = new ArrayList<ItemPost>();
        groupList = new ArrayList<ItemGroup>();
        userList = new ArrayList<ItemUser>();
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
        commentsRef = db.getInstance().getReference().child("comments");


        // ===== TABS
        final TabHost tabHost = (TabHost) getActivity().findViewById(R.id.fragmentExploreTabHost);
        tabHost.setup();

        // POSTS
        TabHost.TabSpec spec1 = tabHost.newTabSpec("posts");
        spec1.setContent(R.id.explorePostsTab);
        spec1.setIndicator("posts");
        tabHost.addTab(spec1);

        // GROUPS
        TabHost.TabSpec spec2 = tabHost.newTabSpec("groups");
        spec2.setContent(R.id.exploreGroupsTab);
        spec2.setIndicator("groups");
        tabHost.addTab(spec2);

        // USERS
        TabHost.TabSpec spec3 = tabHost.newTabSpec("users");
        spec3.setContent(R.id.exploreUsersTab);
        spec3.setIndicator("users");
        tabHost.addTab(spec3);


        // ===== POSTS
        postsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String key = d.getKey();
                    postIdList.add(key);
                }


                // ===== POST INFO
                for (int i = 0; i<postIdList.size(); i++) {
                    postId = String.valueOf(postIdList.get(i));

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

                            RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.explorePostsRecyclerView);
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




        // ===== GROUPS
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
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

                            ItemGroup group = new ItemGroup(groupId, groupDescription, groupCreator, groupPosts, groupMembers);
                            groupList.add(group);

                            RecyclerView groupRecyclerView = (RecyclerView) getActivity().findViewById(R.id.exploreGroupsRecyclerView);
                            AdapterGroups groupAdapter = new AdapterGroups(groupList);
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


        // ===== USERS
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String userId = snapshot.getKey();
                    String username = snapshot.child("username").getValue(String.class);
                    long followers = snapshot.child("followers").getValue(long.class);
                    long following = snapshot.child("following").getValue(long.class);
                    long posts = snapshot.child("posts").getValue(long.class);
                    long groups = snapshot.child("groups").getValue(long.class);

                    ItemUser user = new ItemUser(userId, username, followers, following, posts, groups);
                    userList.add(user);
                }

                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.exploreUsersRecyclerView);
                recyclerView.setNestedScrollingEnabled(false);
                AdapterUsers adapter = new AdapterUsers(userList);
                recyclerView.setAdapter(adapter);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    // ===== ON BUTTON PRESSED
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    // ===== ON DETACH
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    // ===== ON INTERACTION LISTENER
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
