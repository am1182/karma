package com.politics.karma;

import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

public class FragmentNotifications extends Fragment {

    private FragmentNotifications.OnFragmentInteractionListener mListener;
    public FragmentNotifications() {}

    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference notificationsRefFollows, notificationsRefComments;

    private String userId;
    private Drawable icon;

    List<ItemNotification> notificationsListComments;
    List<ItemNotification> notificationsListFollows;


    // ===== NEW INSTANCE
    public static FragmentNotifications newInstance() {
        FragmentNotifications fragment = new FragmentNotifications();
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
        return inflater.inflate(R.layout.fragment_notifications, container, false);
    }


    // ===== ON ACTIVITY CREATED
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // ===== VARS
        notificationsListComments = new ArrayList<ItemNotification>();
        notificationsListFollows = new ArrayList<ItemNotification>();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userId = user.getUid();
        db = FirebaseDatabase.getInstance();

        icon = getResources().getDrawable(R.drawable.ic_button_like_grey);

        notificationsRefFollows = db.getInstance().getReference().child("notifications_follow");
        notificationsRefComments = db.getInstance().getReference().child("notifications_comments");


        // ===== TABS
        final TabHost tabHost = (TabHost) getActivity().findViewById(R.id.notificationsTabHost);
        tabHost.setFocusableInTouchMode(false);
        tabHost.setFocusable(false);
        tabHost.setup();

        // POSTS
        TabHost.TabSpec spec1 = tabHost.newTabSpec("follows");
        spec1.setContent(R.id.notificationsFollowTab);
        spec1.setIndicator("follows");
        tabHost.addTab(spec1);

        // GROUPS
        TabHost.TabSpec spec2 = tabHost.newTabSpec("comments");
        spec2.setContent(R.id.notificationsCommentsTab);
        spec2.setIndicator("comments");
        tabHost.addTab(spec2);


        // ===== FOLLOWS
        notificationsRefFollows.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String message = snapshot.child("message").getValue(String.class);
                    String senderId = snapshot.child("sender_id").getValue(String.class);

                    ItemNotification notification = new ItemNotification(message, senderId, icon);
                    notificationsListFollows.add(notification);
                }

                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.notificationsFollowRecyclerView);
                recyclerView.setNestedScrollingEnabled(false);
                AdapterNotificationsFollow adapter = new AdapterNotificationsFollow(notificationsListFollows);
                recyclerView.setAdapter(adapter);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        // ===== COMMENTS
        notificationsRefComments.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    String message = snapshot.child("message").getValue(String.class);
                    String postContent = snapshot.child("post_content").getValue(String.class);
                    String postId = snapshot.child("post_id").getValue(String.class);

                    ItemNotification notification = new ItemNotification(message, postId, postContent, icon);
                    notificationsListComments.add(notification);
                }

                RecyclerView recyclerView = (RecyclerView) getActivity().findViewById(R.id.notificationsCommentsRecyclerView);
                recyclerView.setNestedScrollingEnabled(false);
                AdapterNotificationsComment adapter = new AdapterNotificationsComment(notificationsListComments);
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
