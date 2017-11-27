package com.politics.karma;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ActivityEditPost extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);


        // ===== POST ID
        Bundle bundle = getIntent().getExtras();
        final String postId = bundle.getString("postId");


        // ===== FIREBASE
        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();
        final DatabaseReference postsRef = db.getInstance().getReference().child("posts");


        // ===== DISPLAY POST INFO
        postsRef.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                EditText contentEditText = (EditText)findViewById(R.id.editPostContentEditText);
                String content = dataSnapshot.child("content").getValue(String.class);
                contentEditText.setText(content);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // ===== CANCEL BUTTON
        Button cancelButton = (Button) findViewById(R.id.editPostCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        // ===== SAVE POST BUTTON
        Button savePostButton = (Button) findViewById(R.id.editPostSavePostButton);
        savePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText contentEditText = (EditText)findViewById(R.id.editPostContentEditText);
                String content = contentEditText.getText().toString();
                postsRef.child(postId).child("content").setValue(content);

                Toast.makeText(ActivityEditPost.this, "post saved", Toast.LENGTH_LONG).show();
                onBackPressed();
            }
        });
    }
}
