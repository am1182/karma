package com.politics.karma;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class ActivityMain extends AppCompatActivity {


    // ===== VARS
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private Fragment fragment;


    // ===== ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // ===== VARS
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        // ===== TOOLBAR
        Toolbar toolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);


        // ===== LOAD LOG IN VIEW
        if (firebaseUser == null) {
            Intent intent = new Intent(ActivityMain.this, ActivityLogIn.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Log.d("user", "user is null");
        }

        else {

            // ===== BOTTOM NAV
            BottomNavigationViewEx navigation = (BottomNavigationViewEx) findViewById(R.id.bottom_navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.enableItemShiftingMode(false);
            navigation.enableShiftingMode(false);
            navigation.enableAnimation(false);
            navigation.setSelectedItemId(R.id.navigation_home);
            navigation.setTextVisibility(true);
            navigation.setIconSize(25, 25);
        }
    }


    // ===== BOTTOM NAV
    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {

                // ===== HOME
                case R.id.navigation_home:
                    fragment = new FragmentHome();
                    FragmentTransaction transactionProfile = getFragmentManager().beginTransaction();
                    transactionProfile.replace(R.id.activityMainContent, fragment);
                    transactionProfile.commit();
                    break;

                // ===== NOTIFICATIONS
                case R.id.navigation_notifications:
                    fragment = new FragmentNotifications();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.activityMainContent, fragment);
                    transaction.commit();
                    break;

                // ===== ADD CONTENT
                case R.id.navigation_add:
                    addContent();
                    break;

                // ===== EXPLORE
                case R.id.navigation_explore:
                    fragment = new FragmentExplore();
                    FragmentTransaction transactionTests = getFragmentManager().beginTransaction();
                    transactionTests.replace(R.id.activityMainContent, fragment);
                    transactionTests.commit();
                    break;

                // ===== FOLLOWING
                case R.id.navigation_following:
                    fragment = new FragmentFollowing();
                    FragmentTransaction transactionFollowing = getFragmentManager().beginTransaction();
                    transactionFollowing.replace(R.id.activityMainContent, fragment);
                    transactionFollowing.commit();
                    break;
            }

            return true;
        }

    };


    // ===== LOAD LOG IN VIEW
    private void loadLogInView() {
        Intent intent = new Intent(ActivityMain.this, ActivityLogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    // ===== ADD CONTENT
    private void addContent() {
        Intent intent = new Intent(ActivityMain.this, ActivityAddGroup.class);
        startActivity(intent);
    }


    // ===== BROADCAST
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((mMessageReceiver),
                new IntentFilter("data")
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String from = intent.getExtras().getString("from");
            String body = intent.getExtras().getString("body");

            Log.d("user", from + body);
        }
    };


    // ===== MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar, menu);
        return true;
    }


    // ===== MENU SELECTED
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_toolbar_logout) {
            FirebaseAuth.getInstance().signOut();
            loadLogInView();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
