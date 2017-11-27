package com.politics.karma;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ActivityAddGroup extends AppCompatActivity {


    // ===== VARS
    private SimpleDateFormat simpleDateFormat;
    private String groupId, groupName, groupDescription, postContent, postGroupName, userId, username, postKey, date;
    private long postsNumberUser, groupsNumberUser, postsNumberGroup;
    private Button savePostButton, saveGroupButton;
    private EditText groupNameEditText, groupDescriptionEditText, postContentEditText, postGroupEditText;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference postsRef, groupsRef, usersRef, postsUserRef, groupsUserRef;


    // ===== ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);


        // ===== VARS
        savePostButton = (Button) findViewById(R.id.addGroupSavePostButton);
        saveGroupButton = (Button) findViewById(R.id.addGroupSaveGroupButton);
        groupNameEditText = (EditText) findViewById(R.id.addGroupGroupNameEditText);
        groupDescriptionEditText = (EditText) findViewById(R.id.addGroupDescriptionEditText);
        postGroupEditText = (EditText) findViewById(R.id.addGroupPostGroupEditText);
        postContentEditText = (EditText) findViewById(R.id.addGroupPostContentEditText);

        simpleDateFormat = new SimpleDateFormat("dd/MM/yy");
        date = simpleDateFormat.format(new Date());


        // ===== FIREBASE
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();
        usersRef = db.getInstance().getReference().child("users");
        postsRef = db.getInstance().getReference().child("posts");
        postsUserRef = db.getInstance().getReference().child("posts_user");
        groupsRef = db.getInstance().getReference().child("groups");
        groupsUserRef = db.getInstance().getReference().child("groups_user");
        userId = user.getUid();


        // ===== TABS
        final TabHost tabHost = (TabHost) findViewById(R.id.tabHostAddPost);
        tabHost.setup();

        // POSTS
        TabHost.TabSpec spec1 = tabHost.newTabSpec("post");
        spec1.setContent(R.id.addPostTab);
        spec1.setIndicator("post");
        tabHost.addTab(spec1);

        // COMPASS
        TabHost.TabSpec spec2 = tabHost.newTabSpec("group");
        spec2.setContent(R.id.addGroupTab);
        spec2.setIndicator("group");
        tabHost.addTab(spec2);

        // ===== CANCEL BUTTON
        Button cancelButton = (Button) findViewById(R.id.addGroupCancelGroupButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // ===== CANCEL BUTTON
        Button groupCancelButton = (Button) findViewById(R.id.addGroupCancelPostButton);
        groupCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // ===== SAVE POST
        savePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // ===== VARS
                postContent = postContentEditText.getText().toString();
                postGroupName = postGroupEditText.getText().toString();


                // ===== DATABASE
                groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        if (!postContent.isEmpty() && !postGroupName.isEmpty()) {


                            // ===== IF GROUP EXISTS
                            if (dataSnapshot.hasChild(postGroupName)) {

                                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        username = dataSnapshot.child(userId).child("username").getValue(String.class);
                                        postKey = postsRef.push().getKey();

                                        // ===== POSTS
                                        postsRef.child(postKey).child("post_id").setValue(postKey);
                                        postsRef.child(postKey).child("username").setValue(username);
                                        postsRef.child(postKey).child("date").setValue(date);
                                        postsRef.child(postKey).child("content").setValue(postContent);
                                        postsRef.child(postKey).child("likes").setValue(0);
                                        postsRef.child(postKey).child("dislikes").setValue(0);
                                        postsRef.child(postKey).child("comments").setValue(0);
                                        postsRef.child(postKey).child("group_name").setValue(postGroupName);
                                        postsRef.child(postKey).child("creator").setValue(userId);

                                        // ===== POSTS_USER
                                        postsUserRef.child(userId).child(postKey).setValue(" ");

                                        // ===== GROUPS
                                        groupsRef.child(postGroupName).child("posts").child(postKey).setValue(" ");

                                        // ===== INC POSTS USER
                                        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                postsNumberUser = dataSnapshot.child("posts").getValue(long.class);
                                                postsNumberUser += 1;
                                                usersRef.child(userId).child("posts").setValue(postsNumberUser);

                                                // ===== INC POSTS GROUP
                                                groupsRef.child(postGroupName).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        postsNumberGroup = dataSnapshot.child("posts_number").getValue(long.class);
                                                        postsNumberGroup += 1;
                                                        groupsRef.child(postGroupName).child("posts_number").setValue(postsNumberGroup);

                                                        Toast.makeText(ActivityAddGroup.this, "post saved", Toast.LENGTH_LONG).show();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });

                            }


                            // ===== IF GROUP DOES NOT EXIST
                            else {
                                showMessage("error", "group does not exist");
                            }
                        }

                        else {
                            showMessage("error", "enter group name and post content");
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}


                });
            }


        });


        // ===== SAVE GROUP
        saveGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // ===== VARS
                groupName = groupNameEditText.getText().toString();
                groupDescription = groupDescriptionEditText.getText().toString();


                // ===== DATABASE
                groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (!groupName.isEmpty()) {

                            // ===== IF GROUP NAME EXISTS
                            if (dataSnapshot.hasChild(groupName)) {
                                showMessage("error", "group already exists");
                            }


                            // ===== IF GROUP DOES NOT EXIST
                            else {

                                // ===== GROUPS
                                groupsRef.child(groupName).child("users_number").setValue(1);
                                groupsRef.child(groupName).child("posts_number").setValue(0);
                                groupsRef.child(groupName).child("users").child(userId).setValue(" ");
                                groupsRef.child(groupName).child("description").setValue(groupDescription);
                                groupsRef.child(groupName).child("creator").setValue(userId);

                                // ===== GROUPS USER
                                groupsUserRef.child(userId).child(groupName).setValue(" ");

                                // ===== INC GROUPS USER
                                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        groupsNumberUser = dataSnapshot.child("groups").getValue(long.class);
                                        groupsNumberUser += 1;
                                        usersRef.child(userId).child("groups").setValue(groupsNumberUser);

                                        Toast.makeText(ActivityAddGroup.this, "group saved", Toast.LENGTH_LONG).show();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }


                        else {
                            showMessage("error", "must enter group name");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });
            }
        });
    }


    // ===== SHOW MESSAGE
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityAddGroup.this);
        builder.setMessage(message).setTitle(title).setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

























