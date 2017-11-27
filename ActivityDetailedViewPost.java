package com.politics.karma;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ActivityDetailedViewPost extends AppCompatActivity {

    private String creator, postContent, groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view_post);


        // ===== GET POST ID
        Bundle bundle = getIntent().getExtras();
        final String postId = bundle.getString("postId");


        // ===== FIREBASE
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final String userId = user.getUid();

        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference postsRef = db.getInstance().getReference().child("posts");
        final DatabaseReference usersRef = db.getInstance().getReference().child("users");
        final DatabaseReference commentsRef = db.getInstance().getReference().child("comments");
        final DatabaseReference postsUserRef = db.getInstance().getReference().child("posts_user");
        final DatabaseReference groupsRef = db.getInstance().getReference().child("groups");
        final DatabaseReference reportRef = db.getInstance().getReference().child("report");

        final TextView usernameTextView = (TextView) findViewById(R.id.detailedViewPostUsernameTextView);

        final ToggleButton likeButton = (ToggleButton) findViewById(R.id.detailedViewPostLikeButton);
        final ToggleButton dislikeButton = (ToggleButton) findViewById(R.id.detailedViewPostDislikeButton);


        // ===== DISPLAY POST DETAILS
        postsRef.child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {

                    String username = dataSnapshot.child("username").getValue(String.class);
                    postContent = dataSnapshot.child("content").getValue(String.class);
                    String date = dataSnapshot.child("date").getValue(String.class);
                    groupName = dataSnapshot.child("group_name").getValue(String.class);
                    long likes = dataSnapshot.child("likes").getValue(long.class);
                    long dislikes = dataSnapshot.child("dislikes").getValue(long.class);
                    long comments = dataSnapshot.child("comments").getValue(long.class);
                    creator = dataSnapshot.child("creator").getValue(String.class);

                    // ===== VIEWS
                    final TextView usernameTextView = (TextView) findViewById(R.id.detailedViewPostUsernameTextView);
                    final TextView dateTextView = (TextView) findViewById(R.id.detailedViewPostDateTextView);
                    final TextView groupNameTextView = (TextView) findViewById(R.id.detailedViewPostGroupNameTextView);
                    final TextView contentTextView = (TextView) findViewById(R.id.detailedViewPostContentTextView);
                    final TextView likesTextView = (TextView) findViewById(R.id.detailedViewPostLikesTextView);
                    final TextView dislikesTextView = (TextView) findViewById(R.id.detailedViewPostDislikesTextView);
                    final TextView commentsTextView = (TextView) findViewById(R.id.detailedViewPostCommentsNumberTextView);

                    usernameTextView.setText(username);
                    dateTextView.setText(date);
                    groupNameTextView.setText(groupName);
                    contentTextView.setText(postContent);
                    likesTextView.setText(String.valueOf(likes));
                    dislikesTextView.setText(String.valueOf(dislikes));
                    commentsTextView.setText(String.valueOf(comments));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ----- USERNAME ONCLICK
        usernameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                // ----- FIREBASE
                String uid = user.getUid();
                DatabaseReference usersRef = db.getInstance().getReference().child("users");
                final String un = usernameTextView.getText().toString();

                // ----- DISPLAY USER DETAILS
                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot s : dataSnapshot.getChildren()) {
                            String username = dataSnapshot.child("username").getValue(String.class);
                            Log.d("USER", "Username is.... " + username);

                            // ----- IF USERNAME MATCHES
                            if (username.equals(un)) {
                                Log.d("USER", "MESSAGE: Username " + un + " matches current user - " + username);
                            }

                            // ----- IF USERNAME DOES NOT MATCH
                            else {
                                Log.d("USER", "MESSAGE: Username " + un + " does not match current user - " + username);
/*
                                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                                FragmentProfileUser fragment = FragmentProfileUser.newInstance(un);
                                activity.getFragmentManager()
                                        .beginTransaction()
                                        .replace(R.id.content, fragment)
                                        .addToBackStack(null)
                                        .commit();*/
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {}
                });

                Log.d("USER", "PROFILE IS LOADING");
            }
        });



        // ===== IF USER IS CREATOR
        Button menuButton = (Button) findViewById(R.id.detailedViewPostExpandButton);
        if (userId.equals(creator)) {
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.getMenuInflater().inflate(R.menu.menu_post_creator, menu.getMenu());

                    // ===== MENU ON CLICK
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            // ==== EDIT POST
                            if (id == R.id.action_editPost) {
                                Intent intent = new Intent(v.getContext(), ActivityEditPost.class);

                                // ===== PASS ID TO CLASS
                                Bundle bundle = new Bundle();
                                bundle.putString("postId", postId);
                                intent.putExtras(bundle);
                                v.getContext().startActivity(intent);
                            }

                            // ===== DELETE POST
                            if (id == R.id.action_deletePost) {

                                // ===== REMOVE
                                postsUserRef.child(userId).child(postId).removeValue();
                                postsRef.child(postId).removeValue();
                                commentsRef.child(postId).removeValue();
                                groupsRef.child(groupName).child("posts").child(postId).removeValue();

                                // ===== DEC POSTS NUMBER USER
                                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        long postsNumberUser = dataSnapshot.child("posts").getValue(long.class);
                                        postsNumberUser -= 1;
                                        usersRef.child(userId).child("posts").setValue(postsNumberUser);

                                        // ===== DEC POSTS NUMBER GROUP
                                        groupsRef.child(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                long postsNumberGroup = dataSnapshot.child("posts_number").getValue(long.class);
                                                postsNumberGroup -= 1;
                                                groupsRef.child(groupName).child("posts_number").setValue(postsNumberGroup);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {}
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {}
                                });
                            }
                            return true;
                        }
                    });

                    menu.show();
                }
            });
        }


        // ===== IF USER IS NOT CREATOR
        else {
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    PopupMenu menu = new PopupMenu(v.getContext(), v);
                    menu.getMenuInflater().inflate(R.menu.menu_post_user, menu.getMenu());

                    // ===== MENU ONCLICK
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            int id = item.getItemId();

                            // EDIT POST
                            if (id == R.id.action_reportPost) {
                                reportRef.child(postId).setValue(" ");
                            }

                            return true;
                        }
                    });

                    menu.show();
                }
            });
        }



        // ===== ADD COMMENT
        final String uid = user.getUid();
        Button saveCommentButton = (Button) findViewById(R.id.detailedViewPostAddCommentButton);
        saveCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText commentContentEditText = (EditText) findViewById(R.id.detailedViewPostAddCommentEditText);
                String commentContent = commentContentEditText.getText().toString();
                if (!commentContent.isEmpty()) {
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            // KEY
                            String key = commentsRef.child(postId).push().getKey();

                            // USERNAME
                            String username = String.valueOf(dataSnapshot.child("username").getValue());

                            // DATE
                            SimpleDateFormat s = new SimpleDateFormat("dd/MM/yy");
                            String commentDate = s.format(new Date());

                            // CONTENT
                            EditText commentContentEditText = (EditText) findViewById(R.id.detailedViewPostAddCommentEditText);
                            String commentContent = commentContentEditText.getText().toString();

                            commentsRef.child(postId).child(key).child("username").setValue(username);
                            commentsRef.child(postId).child(key).child("date").setValue(commentDate);
                            commentsRef.child(postId).child(key).child("content").setValue(commentContent);

                            // ----- COMMENT RECYCLERVIEW
                            final List<ItemComment> commentsList = new ArrayList<>();

                            // GET COMMENT KEYS
                            final List keyList = new ArrayList();

                            commentsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    int i = 0;
                                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                                        String key = d.getKey();
                                        keyList.add(key);
                                        i++;

                                        Log.d("USER", "COMMENT KEY: " + key);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                            // GET POST COMMENTS
                            commentsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (int i = 0; i < keyList.size(); i++) {
                                        final String aKey = String.valueOf(keyList.get(i));

                                        // GET EACH COMMENT
                                        commentsRef.child(postId).child(aKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                String username = null;
                                                String date = null;
                                                String content = null;

                                                for (DataSnapshot s : dataSnapshot.getChildren()) {
                                                    username = String.valueOf(dataSnapshot.child("username").getValue());
                                                    date = String.valueOf(dataSnapshot.child("date").getValue());
                                                    content = String.valueOf(dataSnapshot.child("content").getValue());
                                                }

                                                ItemComment comment = new ItemComment(aKey, username, date, content);
                                                commentsList.add(comment);

                                                // RECYCLER VIEW
                                                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.detailedViewPostCommentsRecyclerView);
                                                AdapterComments adapter = new AdapterComments(commentsList);
                                                recyclerView.setNestedScrollingEnabled(false);
                                                recyclerView.setAdapter(adapter);

                                                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                                                recyclerView.setLayoutManager(layoutManager);
                                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                    }

                                    postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            long commentsNumber = dataSnapshot.child("comments").getValue(long.class);
                                            commentsNumber += 1;
                                            postsRef.child(postId).child("comments").setValue(commentsNumber);
                                            final TextView commentsTextView = (TextView) findViewById(R.id.detailedViewPostCommentsNumberTextView);
                                            commentsTextView.setText(String.valueOf(commentsNumber));


                                            // ===== NOTIFICATION
                                            if (creator.equals(userId)) {
                                                // do nothing
                                            } else {
                                                usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                                        // MESSAGE
                                                        String currentUserUsername = dataSnapshot.child("username").getValue(String.class);
                                                        String notificationMessage = currentUserUsername + " commented on your post";
                                                        DatabaseReference notificationsRef = db.getInstance().getReference().child("notifications_comments");

                                                        // DATABASE
                                                        String key = notificationsRef.child(creator).push().getKey();
                                                        notificationsRef.child(creator).child(key).child("message").setValue(notificationMessage);
                                                        notificationsRef.child(creator).child(key).child("post_content").setValue(postContent);
                                                        notificationsRef.child(creator).child(key).child("post_id").setValue(postId);


                                                        // ===== NOTIFICATION
                                                        Intent intent = new Intent(ActivityDetailedViewPost.this, ActivityMain.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        PendingIntent pendingIntent = PendingIntent.getActivity(ActivityDetailedViewPost.this, 0 /* Request code */, intent,
                                                                PendingIntent.FLAG_ONE_SHOT);

                                                        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(ActivityDetailedViewPost.this)
                                                                .setSmallIcon(R.drawable.ic_button_like_red)
                                                                //.setContentTitle("Notification")
                                                                .setContentText(notificationMessage)
                                                                .setAutoCancel(true)
                                                                .setSound(defaultSoundUri)
                                                                .setContentIntent(pendingIntent);

                                                        notificationBuilder.build();
                                                        NotificationManager notificationManager = (NotificationManager) getSystemService(ActivityDetailedViewPost.this.NOTIFICATION_SERVICE);
                                                        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                    }
                                                });
                                            }
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
                else {
                    showMessage("error", "comment is empty");
                }
            }
        });


        // ----- COMMENT RECYCLERVIEW
        final List<ItemComment> commentsList = new ArrayList<>();

        // GET COMMENT KEYS
        final List keyList = new ArrayList();

        commentsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    String key = d.getKey();
                    keyList.add(key);
                    i++;

                    Log.d("USER", "COMMENT KEY: " + key);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // GET POST COMMENTS
        commentsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (int i=0; i<keyList.size(); i++) {
                    final String aKey = String.valueOf(keyList.get(i));

                    // GET EACH COMMENT
                    commentsRef.child(postId).child(aKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String username = null;
                            String date = null;
                            String content = null;

                            for (DataSnapshot s:dataSnapshot.getChildren()) {
                                username = String.valueOf(dataSnapshot.child("username").getValue());
                                date = String.valueOf(dataSnapshot.child("date").getValue());
                                content = String.valueOf(dataSnapshot.child("content").getValue());
                            }

                            ItemComment comment = new ItemComment(aKey, username, date, content);
                            commentsList.add(comment);

                            // RECYCLER VIEW
                            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.detailedViewPostCommentsRecyclerView);
                            AdapterComments adapter = new AdapterComments(commentsList);
                            recyclerView.setNestedScrollingEnabled(false);
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


        // ===== LIKE/DISLIKE BUTTON STATE

        // LIKES
        final DatabaseReference likesPostRef = db.getInstance().getReference().child("likes_post");
        final DatabaseReference likesUserRef = db.getInstance().getReference().child("likes_user");

        // DISLIKES
        final DatabaseReference dislikesPostRef = db.getInstance().getReference().child("dislikes_post");
        final DatabaseReference dislikesUserRef = db.getInstance().getReference().child("dislikes_user");

        final TextView likesTextView = (TextView) findViewById(R.id.detailedViewPostLikesTextView);
        final TextView dislikesTextView = (TextView) findViewById(R.id.detailedViewPostDislikesTextView);

        // CHECK LIKES
        likesUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postId)) {
                    likeButton.setChecked(true);
                }
                else {
                    likeButton.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // CHECK DISLIKES
        dislikesUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postId)) {
                    dislikeButton.setChecked(true);
                }
                else {
                    dislikeButton.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ===== LIKE BUTTON ONCLICK
        final DatabaseReference postRef = db.getInstance().getReference().child("posts").child(postId);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ----- IF CHECKED && DISLIKE BUTTON CHECKED
                if (likeButton.isChecked() && dislikeButton.isChecked()) {
                    dislikeButton.setChecked(false);

                    // INC LIKES, DEC DISLIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long likes = (long) dataSnapshot.child("likes").getValue();
                            long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                            likes += 1;
                            dislikes -= 1;

                            postRef.child("likes").setValue(likes);
                            postRef.child("dislikes").setValue(dislikes);

                            // SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    likesTextView.setText(String.valueOf(likes));
                                    dislikesTextView.setText(String.valueOf(dislikes));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ADD TO DB
                    likesPostRef.child(postId).child(userId).setValue(" ");
                    likesUserRef.child(userId).child(postId).setValue(" ");

                    dislikesPostRef.child(postId).child(userId).removeValue();
                    dislikesUserRef.child(userId).child(postId).removeValue();
                }

                // ----- IF CHECKED && DISLIKEBUTTON UNCHECKED
                if (likeButton.isChecked() && !dislikeButton.isChecked()) {

                    // INC LIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long likes = (long) dataSnapshot.child("likes").getValue();
                            likes += 1;
                            postRef.child("likes").setValue(likes);

                            // SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    likesTextView.setText(String.valueOf(likes));
                                    dislikesTextView.setText(String.valueOf(dislikes));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ADD TO DB
                    likesPostRef.child(postId).child(userId).setValue(" ");
                    likesUserRef.child(userId).child(postId).setValue(" ");
                }

                // ----- IF UNCHECKED
                if (!likeButton.isChecked()) {

                    // DEC LIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long likes = (long) dataSnapshot.child("likes").getValue();
                            likes -= 1;
                            postRef.child("likes").setValue(likes);

                            // SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    likesTextView.setText(String.valueOf(likes));
                                    dislikesTextView.setText(String.valueOf(dislikes));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ADD TO DB
                    likesPostRef.child(postId).child(userId).removeValue();
                    likesUserRef.child(userId).child(postId).removeValue();
                }
            }
        });


        // ----- DISLIKE BUTTON ONCLICK
        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // ----- IF CHECKED && DISLIKE BUTTON CHECKED
                if (dislikeButton.isChecked() && likeButton.isChecked()) {
                    likeButton.setChecked(false);

                    // DEC LIKES, INC DISLIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long likes = (long) dataSnapshot.child("likes").getValue();
                            long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                            likes -= 1;
                            dislikes += 1;

                            postRef.child("likes").setValue(likes);
                            postRef.child("dislikes").setValue(dislikes);

                            // SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    likesTextView.setText(String.valueOf(likes));
                                    dislikesTextView.setText(String.valueOf(dislikes));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ADD TO DB
                    dislikesPostRef.child(postId).child(userId).setValue(" ");
                    dislikesUserRef.child(userId).child(postId).setValue(" ");

                    likesPostRef.child(postId).child(userId).removeValue();
                    likesUserRef.child(userId).child(postId).removeValue();
                }

                // ----- IF CHECKED && DISLIKEBUTTON UNCHECKED
                if (dislikeButton.isChecked() && !likeButton.isChecked()) {

                    // INC DISLIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long dislikes = (long) dataSnapshot.child("dislikes").getValue();
                            dislikes += 1;
                            postRef.child("dislikes").setValue(dislikes);

                            // SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    likesTextView.setText(String.valueOf(likes));
                                    dislikesTextView.setText(String.valueOf(dislikes));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ADD TO DB
                    dislikesPostRef.child(postId).child(userId).setValue(" ");
                    dislikesUserRef.child(userId).child(postId).setValue(" ");
                }

                // ----- IF UNCHECKED
                if (!dislikeButton.isChecked()) {

                    // DEC DISLIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long dislikes = (long) dataSnapshot.child("dislikes").getValue();
                            dislikes -= 1;
                            postRef.child("dislikes").setValue(dislikes);

                            // SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    likesTextView.setText(String.valueOf(likes));
                                    dislikesTextView.setText(String.valueOf(dislikes));
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ADD TO DB
                    dislikesPostRef.child(postId).child(userId).removeValue();
                    dislikesUserRef.child(userId).child(postId).removeValue();
                }
            }
        });
    }


    // ===== SHOW MESSAGE
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityDetailedViewPost.this);
        builder.setMessage(message).setTitle(title).setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
