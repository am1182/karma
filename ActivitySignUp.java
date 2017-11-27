package com.politics.karma;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivitySignUp extends AppCompatActivity {


    private EditText usernameEditText, passwordEditText, confirmPasswordEditText;
    private TextView loginTextView;
    private Button signupButton;
    private String username, password, email, userId, bio;
    private long followers, following, posts, groups, reputation;
    private static final String TAG = "SIGN UP";


    // ===== ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        // ===== VARS
        usernameEditText = (EditText) findViewById(R.id.signupUsernameEditText);
        passwordEditText = (EditText) findViewById(R.id.signupPasswordEditText);
        confirmPasswordEditText = (EditText) findViewById(R.id.signupConfirmPasswordEditText);
        loginTextView = (TextView) findViewById(R.id.signupLoginTextView);
        signupButton = (Button) findViewById(R.id.signupButton);

        bio = " ";
        followers = 0;
        following = 0;
        posts = 0;
        groups = 0;
        reputation = 0;


        // ===== FIREBASE
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference usernamesRef = db.getInstance().getReference().child("usernames");
        final DatabaseReference usersRef = db.getInstance().getReference().child("users");


        // ===== PASSWORD EDITTEXT
        passwordEditText.setTypeface(Typeface.DEFAULT);
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());
        confirmPasswordEditText.setTypeface(Typeface.DEFAULT);
        confirmPasswordEditText.setTransformationMethod(new PasswordTransformationMethod());


        // ===== LOAD LOG IN
        loginTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLogIn();
            }
        });


        // ===== SIGN UP
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // ===== USER
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                email = username + "@email.com";

                Log.d(TAG, email);


                // ===== IF USERNAME/PASSWORD EMPTY
                if (username.isEmpty() || password.isEmpty()) {
                    showMessage("error", "enter username and password");
                }


                // ===== IF USERNAME/PASSWORD NOT EMPTY
                else {

                    usernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            // ===== IF USERNAME TAKEN
                            if (dataSnapshot.hasChild(username)) {
                                showMessage("error", "username is already taken");
                            }


                            // ===== IF USERNAME NOT TAKEN
                            else {


                                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(

                                        ActivitySignUp.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {


                                                // ===== IF TASK SUCCESSFUL
                                                if (task.isSuccessful()) {


                                                    // ===== ADD USER TO DB
                                                    FirebaseUser user = auth.getCurrentUser();
                                                    userId = user.getUid();
                                                    Log.d(TAG, "user id: " + userId);


                                                    // ===== USER DB
                                                    usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            usersRef.child(userId).child("username").setValue(username);
                                                            usersRef.child(userId).child("bio").setValue(bio);
                                                            usersRef.child(userId).child("followers").setValue(followers);
                                                            usersRef.child(userId).child("following").setValue(following);
                                                            usersRef.child(userId).child("posts").setValue(posts);
                                                            usersRef.child(userId).child("reputation").setValue(reputation);
                                                            usersRef.child(userId).child("groups").setValue(groups);


                                                            // ===== USERNAME DB
                                                            usernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(DataSnapshot dataSnapshot) {

                                                                    usernamesRef.child(username).child("user_id").setValue(userId);

                                                                    // ===== LOAD MAIN
                                                                    loadMain();
                                                                }

                                                                @Override
                                                                public void onCancelled(DatabaseError databaseError) {}
                                                            });
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {}
                                                    });
                                                }


                                                // ===== IF TASK NOT SUCCESSFUL
                                                else {
                                                    showMessage("error", "something went wrong");
                                                }
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }


    // ===== LOAD LOG IN
    private void loadLogIn() {
        Intent intent = new Intent(ActivitySignUp.this, ActivityLogIn.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    // ===== LOAD MAIN
    private void loadMain() {
        Intent intent = new Intent(ActivitySignUp.this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    // ===== SHOW MESSAGE
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySignUp.this);
        builder.setMessage(message).setTitle(title).setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}









