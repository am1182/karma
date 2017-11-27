package com.politics.karma;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ActivityEditProfile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);


        Button saveProfileButton = (Button)findViewById(R.id.editProfileSaveProfileButton);


        // ===== FIREBASE
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        final FirebaseDatabase db = FirebaseDatabase.getInstance();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        final DatabaseReference usersRef = db.getInstance().getReference().child("users");
        final String uid = user.getUid();


        // ===== DISPLAY DETAILS
        usersRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                EditText bioEditText = (EditText)findViewById(R.id.editProfileBioEditText);
                String bio = null;

                // ===== USER DETAILS
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    bio = dataSnapshot.child("bio").getValue(String.class);
                }
                bioEditText.setText(bio);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

        // ===== CANCEL BUTTON
        Button cancelButton = (Button) findViewById(R.id.editProfileCancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // ===== SAVE BUTTON
        saveProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText bioEditText = (EditText)findViewById(R.id.editProfileBioEditText);
                String editedBio = bioEditText.getText().toString();
                if (!editedBio.isEmpty()) {
                    usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            EditText bioEditText = (EditText) findViewById(R.id.editProfileBioEditText);
                            String editedBio = bioEditText.getText().toString();
                            usersRef.child(uid).child("bio").setValue(editedBio);

                            Toast.makeText(ActivityEditProfile.this, "bio saved", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
                else {
                    showMessage("error", "bio is empty");
                }
            }
        });
    }

    // ===== SHOW MESSAGE
    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityEditProfile.this);
        builder.setMessage(message).setTitle(title).setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
