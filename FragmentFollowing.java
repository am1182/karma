package com.politics.karma;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import java.util.Collections;
import java.util.List;

public class FragmentFollowing extends Fragment {

    private FragmentFollowing.OnFragmentInteractionListener mListener;
    public FragmentFollowing() {}

    private List postIdList;
    private List postFollowingIdList;
    private List postGroupIdList;
    private List groupIdList;
    private List<ItemPost> postList;
    private List<ItemPost> groupList;
    private List<ItemPost> allList;


    // ===== NEW INSTANCE
    public static FragmentFollowing newInstance() {
        FragmentFollowing fragment = new FragmentFollowing();
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
        return inflater.inflate(R.layout.fragment_following, container, false);
    }


    // ===== ON ACTIVITY CREATED
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // ===== TABS
        final TabHost tabHost = (TabHost) getActivity().findViewById(R.id.fragmentFollowingTabHost);
        tabHost.setup();

        // ALL
        TabHost.TabSpec spec1 = tabHost.newTabSpec("all");
        spec1.setContent(R.id.followingAllTab);
        spec1.setIndicator("all");
        tabHost.addTab(spec1);

        // GROUPS
        TabHost.TabSpec spec2 = tabHost.newTabSpec("groups");
        spec2.setContent(R.id.followingGroupsTab);
        spec2.setIndicator("groups");
        tabHost.addTab(spec2);

        // USERS
        TabHost.TabSpec spec3 = tabHost.newTabSpec("users");
        spec3.setContent(R.id.followingUsersTab);
        spec3.setIndicator("users");
        tabHost.addTab(spec3);


        // ===== FIREBASE
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        FirebaseDatabase db = FirebaseDatabase.getInstance();

        final DatabaseReference followingRef = db.getInstance().getReference().child("following");
        final DatabaseReference userPostsRef = db.getInstance().getReference().child("posts_user");
        final DatabaseReference postsRef = db.getInstance().getReference().child("posts");
        final DatabaseReference groupsRef = db.getInstance().getReference().child("groups");
        final DatabaseReference groupsUserRef = db.getInstance().getReference().child("groups_user");

        postList = new ArrayList<>();
        groupList = new ArrayList<>();
        allList = new ArrayList<>();
        postIdList = new ArrayList();
        groupIdList = new ArrayList();
        postGroupIdList = new ArrayList();
        postFollowingIdList = new ArrayList();
        final String userId = user.getUid();


        // ===== FOLLOWING
        followingRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                // ===== USERS
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String followingId = d.getKey();
                    postFollowingIdList.add(followingId);
                }

                // ===== GET POST IDs
                for (int k=0; k<postFollowingIdList.size(); k++) {
                    String followingId = (String) postFollowingIdList.get(k);
                    userPostsRef.child(followingId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            int i = 0;
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                String postId = d.getKey();
                                postIdList.add(postId);
                                i++;
                            }

                            // ===== GET POST DETAILS
                            for (int j=0; j<postIdList.size(); j++) {

                                String postId = (String) postIdList.get(j);
                                postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        String id = null;
                                        String content = null;
                                        String date = null;
                                        String username = null;
                                        String creator = null;
                                        String group = null;

                                        long dislikes = 0;
                                        long likes = 0;
                                        long comments = 0;

                                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                                            id = dataSnapshot.getKey();
                                            username = dataSnapshot.child("username").getValue(String.class);
                                            date = dataSnapshot.child("date").getValue(String.class);
                                            content = dataSnapshot.child("content").getValue(String.class);
                                            group = dataSnapshot.child("group_name").getValue(String.class);
                                            likes = dataSnapshot.child("likes").getValue(long.class);
                                            dislikes = dataSnapshot.child("dislikes").getValue(long.class);
                                            comments = dataSnapshot.child("comments").getValue(long.class);
                                        }

                                        // String id, String creator, String username, String date,
                                        // String content, String group, long likes, long dislikes, long comments
                                        ItemPost post = new ItemPost(id, creator, username, date, content, group, likes, dislikes, comments);
                                        postList.add(post);
                                        allList.add(post);

                                        RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.followingUsersRecyclerView);
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
                }


                // ===== GROUPS
                groupsUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                            String followingId = d.getKey();
                            groupIdList.add(followingId);
                            Log.d("user", followingId);
                        }

                        // ===== GET POST IDs
                        for (int i=0; i<groupIdList.size(); i++) {
                            String groupKey = (String) groupIdList.get(i);

                            final List pgIdList = new ArrayList();
                            groupsRef.child(groupKey).child("posts").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        String postId = d.getKey();
                                        pgIdList.add(postId);
                                        Log.d("user", postId);
                                    }

                                    // ===== GET POST DETAILS
                                    for (int j=0; j<pgIdList.size(); j++) {

                                        String postId = (String) pgIdList.get(j);
                                        Log.d("user", postId);
                                        postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                String id = null;
                                                String content = null;
                                                String date = null;
                                                String username = null;
                                                String creator = null;
                                                String group = null;

                                                long dislikes = 0;
                                                long likes = 0;
                                                long comments = 0;

                                                for (DataSnapshot s : dataSnapshot.getChildren()) {
                                                    id = dataSnapshot.getKey();
                                                    username = dataSnapshot.child("username").getValue(String.class);
                                                    date = dataSnapshot.child("date").getValue(String.class);
                                                    content = dataSnapshot.child("content").getValue(String.class);
                                                    group = dataSnapshot.child("group_name").getValue(String.class);
                                                    likes = dataSnapshot.child("likes").getValue(long.class);
                                                    dislikes = dataSnapshot.child("dislikes").getValue(long.class);
                                                    comments = dataSnapshot.child("comments").getValue(long.class);
                                                }

                                                // String id, String creator, String username, String date,
                                                // String content, String group, long likes, long dislikes, long comments
                                                ItemPost post = new ItemPost(id, creator, username, date, content, group, likes, dislikes, comments);
                                                groupList.add(post);
                                                Collections.reverse(groupList);
                                                allList.add(post);
                                                Collections.reverse(allList);


                                                // ===== GROUPS
                                                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.followingGroupsRecyclerView);
                                                AdapterPosts adapter = new AdapterPosts(groupList);
                                                recyclerView.setAdapter(adapter);

                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                                                recyclerView.setLayoutManager(layoutManager);
                                                recyclerView.setItemAnimator(new DefaultItemAnimator());


                                                // ===== ALL
                                                RecyclerView rv = (RecyclerView) getActivity().findViewById(R.id.followingAllRecyclerView);
                                                AdapterPosts a = new AdapterPosts(allList);
                                                rv.setAdapter(a);

                                                RecyclerView.LayoutManager lm = new LinearLayoutManager(getActivity().getApplicationContext());
                                                rv.setLayoutManager(lm);
                                                rv.setItemAnimator(new DefaultItemAnimator());
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

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                // ===== ALL
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
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
