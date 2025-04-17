package edu.northeastern.finalproject_group_1;

import static java.util.Objects.isNull;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
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

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    final String TAG = "LoginActivity";
    private FirebaseDatabase db;
    private TextView usernameText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        db = FirebaseDatabase.getInstance();
        usernameText = findViewById(R.id.loginUsernameText);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.openRegistrationButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndLogIn(usernameText.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(regIntent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void validateAndLogIn(String username) {
        if (isNull(username) || username.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter a username to log in", Toast.LENGTH_SHORT).show();
        } else {
            DatabaseReference users = db.getReference().child("USERS");
            users.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG, "Successfully found user " + username + " in database");
                        logIn(username);
                    } else {
                        Toast.makeText(getApplicationContext(), "Could not find a user with that username", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "Failed to find users in database: " + error);
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void logIn(String username) {
        Log.d(TAG, "Logging " + username + " in");

        getSharedPreferences("HabitendPrefs", MODE_PRIVATE)
                .edit()
                .putString("logged_in_username", username)
                .apply();

        updateLoginStreak(username);
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        intent.putExtra("USERNAME", username);
        startActivity(intent);
    }

    private void updateLoginStreak(String username) {
        DatabaseReference statsRef = db.getReference("GARDENDATA").child(username);
        String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String lastLoginDate = snapshot.child("lastLoginDate").getValue(String.class);
                Integer currentStreakVal = snapshot.child("currentStreak").getValue(Integer.class);
                Integer longestStreakVal = snapshot.child("longestStreak").getValue(Integer.class);

                int currentStreak = (currentStreakVal != null) ? currentStreakVal : 0;
                int longestStreak = (longestStreakVal != null) ? longestStreakVal : 0;

                boolean updateStreak = false;

                if (lastLoginDate == null) {
                    currentStreak = 1;
                    longestStreak = 1;
                    updateStreak = true;
                } else {
                    try {
                        Date lastLogin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(lastLoginDate);
                        Calendar cal = Calendar.getInstance();
                        cal.setTime(lastLogin);
                        cal.add(Calendar.DATE, 1);

                        String expectedLogin = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

                        if (expectedLogin.equals(todayStr)) {
                            currentStreak += 1;
                            longestStreak = Math.max(currentStreak, longestStreak);
                            updateStreak = true;
                        } else if (!lastLoginDate.equals(todayStr)) {
                            currentStreak = 1;
                            updateStreak = true;
                        }
                    } catch (ParseException e) {
                        Log.e(TAG, "Date parse error: " + e.getMessage());
                    }
                }

                if (updateStreak) {
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("lastLoginDate", todayStr);
                    updates.put("currentStreak", currentStreak);
                    updates.put("longestStreak", longestStreak);
                    statsRef.updateChildren(updates);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Failed to update login streak: " + error.getMessage());
            }
        });
    }



}