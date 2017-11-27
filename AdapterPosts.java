package com.politics.karma;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.PostsViewHolder> {

    private List<ItemPost> postList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseDatabase db;
    private DatabaseReference postsRef, groupsRef, usersRef, postsUserRef, commentsRef, reportRef;
    private String userId, creator, postUsername, groupName, postId;
    private long postsNumberUser, postsNumberGroup, reputation;
    private ItemPost post;


    // ===== VIEWHOLDER
    public static class PostsViewHolder extends RecyclerView.ViewHolder {

        private TextView username, date, content, likes, dislikes, commentsNumber, group;
        private ToggleButton likeButton, dislikeButton;
        private Button commentsButton, menuButton;

        public PostsViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.cardviewPostUsernameTextView);
            date = (TextView) v.findViewById(R.id.cardviewPostDateTextView);
            content = (TextView) v.findViewById(R.id.cardviewPostContentTextView);
            commentsNumber = (TextView) v.findViewById(R.id.cardviewPostCommentsNumberTextView);
            likes = (TextView) v.findViewById(R.id.cardviewPostLikesTextView);
            dislikes = (TextView) v.findViewById(R.id.cardviewPostDislikesTextView);
            group = (TextView) v.findViewById(R.id.cardviewPostGroupNameTextView);

            likeButton = (ToggleButton) v.findViewById(R.id.cardviewPostLikeButton);
            dislikeButton = (ToggleButton) v.findViewById(R.id.cardViewPostDislikeButton);
            commentsButton = (Button) v.findViewById(R.id.cardviewPostCommentsButton);
            menuButton = (Button) v.findViewById(R.id.cardviewPostExpandButton);

        }
    }


    // ===== ADAPTER
    public AdapterPosts(List<ItemPost> postList){
        this.postList = postList;
    }


    // ===== ON CREATE VIEWHOLDER
    public AdapterPosts.PostsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.cardview_post, viewGroup, false);
        return new PostsViewHolder(itemView);
    }


    // ===== ITEM COUNT
    @Override
    public int getItemCount() {
        return postList.size();
    }


    // ===== ON BIND VIEWHOLDER
    @Override
    public void onBindViewHolder(final PostsViewHolder viewHolder, int i) {

        post = postList.get(i);

        viewHolder.username.setText(String.valueOf(post.getUsername()));
        viewHolder.date.setText(String.valueOf(post.getDate()));
        viewHolder.content.setText(String.valueOf(post.getContent()));
        viewHolder.likes.setText(String.valueOf(post.getLikes()));
        viewHolder.dislikes.setText(String.valueOf(post.getDislikes()));
        viewHolder.commentsNumber.setText(String.valueOf(post.getComments()));
        viewHolder.group.setText(String.valueOf(post.getGroup()));


        // ===== VARS
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseDatabase.getInstance();

        userId = user.getUid();
        postId = post.getId();
        postUsername = post.getUsername();
        final String postId = String.valueOf(post.getId());
        final String creator = String.valueOf(post.getCreator());
        final String groupName = String.valueOf(post.getGroup());

        usersRef = db.getInstance().getReference().child("users");
        postsRef = db.getInstance().getReference().child("posts");
        postsUserRef = db.getInstance().getReference().child("posts_user");
        groupsRef = db.getInstance().getReference().child("groups");
        commentsRef = db.getInstance().getReference().child("comments");
        reportRef = db.getInstance().getReference().child("report");


        // ===== IF USER IS CREATOR
        if (userId.equals(creator)) {
            viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
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
                                        postsNumberUser = dataSnapshot.child("posts").getValue(long.class);
                                        postsNumberUser -= 1;
                                        usersRef.child(userId).child("posts").setValue(postsNumberUser);

                                        // ===== DEC POSTS NUMBER GROUP
                                        groupsRef.child(groupName).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                postsNumberGroup = dataSnapshot.child("posts_number").getValue(long.class);
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
            viewHolder.menuButton.setOnClickListener(new View.OnClickListener() {
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


        // ===== LIKE/DISLIKE BUTTON STATE

        // LIKES
        final DatabaseReference likesPostRef = db.getInstance().getReference().child("likes_post");
        final DatabaseReference likesUserRef = db.getInstance().getReference().child("likes_user");

        // DISLIKES
        final DatabaseReference dislikesPostRef = db.getInstance().getReference().child("dislikes_post");
        final DatabaseReference dislikesUserRef = db.getInstance().getReference().child("dislikes_user");


        // ===== CHECK LIKES
        likesUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postId)) {
                    viewHolder.likeButton.setChecked(true);
                }
                else {
                    viewHolder.likeButton.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ===== CHECK DISLIKES
        dislikesUserRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(postId)) {
                    viewHolder.dislikeButton.setChecked(true);
                }
                else {
                    viewHolder.dislikeButton.setChecked(false);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });


        // ===== LIKE BUTTON ONCLICK
        final DatabaseReference postRef = db.getInstance().getReference().child("posts").child(postId);

        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                // ===== IF CHECKED && DISLIKE BUTTON CHECKED
                if (viewHolder.likeButton.isChecked() && viewHolder.dislikeButton.isChecked()) {
                    viewHolder.dislikeButton.setChecked(false);

                    // ===== REPUTATION - inc by 2
                    usersRef.child(creator).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reputation = dataSnapshot.child("reputation").getValue(long.class);
                            reputation += 2;
                            usersRef.child(creator).child("reputation").setValue(reputation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                    // ===== INC LIKES, DEC DISLIKES
                    postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            long likes = (long) dataSnapshot.child("likes").getValue();
                            long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                            likes += 1;
                            dislikes -= 1;

                            postRef.child("likes").setValue(likes);
                            postRef.child("dislikes").setValue(dislikes);

                            // ===== SET LIKES/DISLIKES
                            postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    long likes = (long) dataSnapshot.child("likes").getValue();
                                    long dislikes = (long) dataSnapshot.child("dislikes").getValue();

                                    viewHolder.likes.setText(String.valueOf(likes));
                                    viewHolder.dislikes.setText(String.valueOf(dislikes));

                                    // ===== ADD TO DB
                                    likesPostRef.child(postId).child(userId).setValue(" ");
                                    likesUserRef.child(userId).child(postId).setValue(" ");

                                    dislikesPostRef.child(postId).child(userId).removeValue();
                                    dislikesUserRef.child(userId).child(postId).removeValue();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                }

                // ===== IF CHECKED && DISLIKEBUTTON UNCHECKED
                else if (viewHolder.likeButton.isChecked() && !viewHolder.dislikeButton.isChecked()) {

                    // ===== REPUTATION - inc by 1
                    usersRef.child(creator).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reputation = dataSnapshot.child("reputation").getValue(long.class);
                            reputation += 1;
                            usersRef.child(creator).child("reputation").setValue(reputation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

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

                                    viewHolder.likes.setText(String.valueOf(likes));
                                    viewHolder.dislikes.setText(String.valueOf(dislikes));

                                    // ADD TO DB
                                    likesPostRef.child(postId).child(userId).setValue(" ");
                                    likesUserRef.child(userId).child(postId).setValue(" ");
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

                }

                // ===== IF UNCHECKED
                else if (!viewHolder.likeButton.isChecked()) {

                    // ===== REPUTATION - dec by 1
                    usersRef.child(creator).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reputation = dataSnapshot.child("reputation").getValue(long.class);
                            reputation -= 1;
                            usersRef.child(creator).child("reputation").setValue(reputation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

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

                                    viewHolder.likes.setText(String.valueOf(likes));
                                    viewHolder.dislikes.setText(String.valueOf(dislikes));

                                    // ADD TO DB
                                    likesPostRef.child(postId).child(userId).removeValue();
                                    likesUserRef.child(userId).child(postId).removeValue();
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


        // ===== DISLIKE BUTTON ONCLICK
        viewHolder.dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // ===== IF CHECKED && DISLIKE BUTTON CHECKED
                if (viewHolder.dislikeButton.isChecked() && viewHolder.likeButton.isChecked()) {
                    viewHolder.likeButton.setChecked(false);

                    // ===== REPUTATION
                    usersRef.child(creator).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reputation = dataSnapshot.child("reputation").getValue(long.class);
                            reputation -= 2;
                            usersRef.child(creator).child("reputation").setValue(reputation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

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

                                    viewHolder.likes.setText(String.valueOf(likes));
                                    viewHolder.dislikes.setText(String.valueOf(dislikes));

                                    // ADD TO DB
                                    dislikesPostRef.child(postId).child(userId).setValue(" ");
                                    dislikesUserRef.child(userId).child(postId).setValue(" ");

                                    likesPostRef.child(postId).child(userId).removeValue();
                                    likesUserRef.child(userId).child(postId).removeValue();
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                // ===== IF CHECKED && DISLIKEBUTTON UNCHECKED
                else if (viewHolder.dislikeButton.isChecked() && !viewHolder.likeButton.isChecked()) {

                    // ===== REPUTATION
                    usersRef.child(creator).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reputation = dataSnapshot.child("reputation").getValue(long.class);
                            reputation -= 1;
                            usersRef.child(creator).child("reputation").setValue(reputation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

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

                                    viewHolder.likes.setText(String.valueOf(likes));
                                    viewHolder.dislikes.setText(String.valueOf(dislikes));

                                    // ADD TO DB
                                    dislikesPostRef.child(postId).child(userId).setValue(" ");
                                    dislikesUserRef.child(userId).child(postId).setValue(" ");
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {}
                            });
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }

                // ===== IF UNCHECKED
                else if (!viewHolder.dislikeButton.isChecked()) {

                    // ===== REPUTATION
                    usersRef.child(creator).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            reputation = dataSnapshot.child("reputation").getValue(long.class);
                            reputation += 1;
                            usersRef.child(creator).child("reputation").setValue(reputation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });

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

                                    viewHolder.likes.setText(String.valueOf(likes));
                                    viewHolder.dislikes.setText(String.valueOf(dislikes));

                                    // ADD TO DB
                                    dislikesPostRef.child(postId).child(userId).removeValue();
                                    dislikesUserRef.child(userId).child(postId).removeValue();
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


        // ===== COMMENTS BUTTON ONCLICK
        viewHolder.commentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityDetailedViewPost.class);
                Bundle bundle = new Bundle();
                bundle.putString("postId", postId);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);

                Log.d("user", "comments button clicked");
            }
        });


        // ===== GROUP NAME ONCLICK
        viewHolder.group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ActivityDetailedViewGroup.class);

                // ===== PASS ID TO CLASS
                //String groupName = String.valueOf(post.getGroup());
                Bundle bundle = new Bundle();
                bundle.putString("groupName", groupName);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
                Log.d("user", groupName);
            }
        });


        // ===== USERNAME ONCLICK
        viewHolder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                // ===== IF USER IS CREATOR
                if (userId.equals(creator)) {
                    /*AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    FragmentHome fragment = FragmentHome.newInstance();
                    activity.getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activityMainContent, fragment)
                            .addToBackStack(null)
                            .commit();*/
                }


                // ===== IF USER IS NOT CREATOR
                else {
                    /*
                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    FragmentProfileUser fragment = FragmentProfileUser.newInstance(creator);
                    activity.getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activityMainContent, fragment)
                            .addToBackStack(null)
                            .commit();*/

                    Intent intent = new Intent(v.getContext(), ActivityProfileUser.class);

                    // ===== PASS ID TO CLASS
                    Bundle bundle = new Bundle();
                    bundle.putString("userId", creator);
                    intent.putExtras(bundle);
                    v.getContext().startActivity(intent);
                }
            }
        });
    }
}






























