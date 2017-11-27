package com.politics.karma;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityLogIn extends AppCompatActivity {


    private String username, email, password, userId;
    private EditText usernameEditText, passwordEditText;
    private TextView signUpTextView;
    private Button logInButton;
    private static final String TAG = "LOG IN";


    // ===== ON CREATE
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);


        // ===== VARS
        usernameEditText = (EditText) findViewById(R.id.loginUsernameEditText);
        passwordEditText = (EditText) findViewById(R.id.loginPasswordEditText);
        signUpTextView = (TextView) findViewById(R.id.loginSignupTextView);
        logInButton = (Button) findViewById(R.id.loginButton);


        // ===== FIREBASE
        final FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference usernamesRef = db.getInstance().getReference().child("usernames");


        // ===== PASSWORD EDITTEXT
        passwordEditText.setTypeface(Typeface.DEFAULT);
        passwordEditText.setTransformationMethod(new PasswordTransformationMethod());


        // ===== LOAD SIGN UP
        signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSignUp();
            }
        });


        // ===== LOG IN
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // ===== USER
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();


                // ===== IF USERNAME/PASSWORD EMPTY
                if (username.isEmpty() || password.isEmpty()) {
                    showMessage("error", "enter username and password");
                }


                // ===== IF USERNAME/PASSWORD NOT EMPTY
                else {

                    usernamesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            // ===== IF USERNAME EXISTS
                            if (dataSnapshot.hasChild(username)) {
                                email = username + "@email.com";

                                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                                        ActivityLogIn.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            loadMain();
                                        }
                                        else {
                                            showMessage("error", "something went wrong");
                                        }
                                    }
                                });
                            }


                            // ===== IF USERNAME DOES NOT EXIST
                            else {
                                showMessage("error", "username does not exist");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }


    // ===== LOAD SIGN UP VIEW
    private void loadSignUp() {
        Intent intent = new Intent(ActivityLogIn.this, ActivitySignUp.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    // ===== LOAD MAIN
    private void loadMain() {
        Intent intent = new Intent(ActivityLogIn.this, ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    // ===== SHOW MESSAGE
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogIn.this);
        builder.setMessage(message).setTitle(title).setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
