package edu.northeastern.finalproject_group_1;

import static java.util.Objects.isNull;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistrationActivity extends AppCompatActivity {
    final String TAG = "RegistrationActivity";
    private FirebaseDatabase db;
    private TextView newUsernameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        db = FirebaseDatabase.getInstance();
        newUsernameText = findViewById(R.id.regUsernameText);
        Button regButton = findViewById(R.id.registerButton);
        
        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(newUsernameText.getText().toString());
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void registerUser(String username) {
        if (isNull(username) || username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a username to register", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference existingUsers = db.getReference().child("USERS");
            existingUsers.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Toast.makeText(getApplicationContext(), "Username already taken", Toast.LENGTH_SHORT).show();
                    } else {
                        createUser(username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.d(TAG, "Failed to find users in database: " + error);
                }
            });
        }
    }

    private void createUser(String username) {
        DatabaseReference dbRef = db.getReference();
        //create child in USERS node with username as key
        dbRef.child("USERS").child(username).setValue("");
        //create child in HABITS node
        dbRef.child("HABITS").child(username).setValue("");
        //create child in GARDENDATA node
        dbRef.child("GARDENDATA").child(username).child("bank").setValue(0);
        dbRef.child("GARDENDATA").child(username).child("displayed").setValue("");
        dbRef.child("GARDENDATA").child(username).child("inventory").setValue("");
        //create child in FRIENDSLIST node
        dbRef.child("FRIENDSLIST").child(username).setValue("");
        //automatically log user in, continue to dashboard activity
        Log.d(TAG, "Created user " + username + " in db, logging in");
        Intent intent = new Intent(RegistrationActivity.this, DashboardActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }
}