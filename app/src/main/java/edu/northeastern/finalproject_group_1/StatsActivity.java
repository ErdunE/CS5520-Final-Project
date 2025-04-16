package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatsActivity extends AppCompatActivity {

    private TextView usernameTV, totalHabitsTV, currentStreakTV, longestStreakTV, completedHabitsTV, levelTV, xpProgressTV;
    private ProgressBar xpProgressBar;
    private String currentUser;
    private ImageView levelBadge;
    private DatabaseReference habitsRef, statsRef;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton gardenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        gardenButton = findViewById(R.id.gardenButton);

        // Setup garden FAB
        gardenButton.setOnClickListener(v -> {
            Intent intent = new Intent(StatsActivity.this, DashboardActivity.class);
            intent.putExtra("USERNAME", currentUser);
            startActivity(intent);
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Shop) {
                    Intent intent = new Intent(StatsActivity.this, MarketplaceActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Challenges) {
                    Intent intent = new Intent(StatsActivity.this, ChallengesActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Stats) {
                    Intent intent = new Intent(StatsActivity.this, StatsActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Settings) {
                    Intent intent = new Intent(StatsActivity.this, SettingsActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        // Retrieve username from Intent
        currentUser = getIntent().getStringExtra("USERNAME");
        if (currentUser == null) currentUser = "testUser1";

        usernameTV = findViewById(R.id.usernameTV);
        levelTV = findViewById(R.id.levelTV);

        totalHabitsTV = findViewById(R.id.totalHabitsTV);
        currentStreakTV = findViewById(R.id.currentStreakTV);
        longestStreakTV = findViewById(R.id.longestStreakTV);
        completedHabitsTV = findViewById(R.id.completedHabitsTV);

        xpProgressBar = findViewById(R.id.xpProgressBar);
        xpProgressTV = findViewById(R.id.xpProgressTV);
        levelBadge = findViewById(R.id.levelBadgeImage);

        statsRef = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(currentUser);
        habitsRef = FirebaseDatabase.getInstance().getReference("HABITS").child(currentUser);

        usernameTV.setText("Hi, " + currentUser);
        updateLoginStreak();
        loadHabitStats();

    }

    private void updateLoginStreak() {
        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String todayStr = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                String lastLoginDate = snapshot.child("lastLoginDate").getValue(String.class);
                Integer currentStreakVal = snapshot.child("currentStreak").getValue(Integer.class);
                Integer longestStreakVal = snapshot.child("longestStreak").getValue(Integer.class);

                int currentStreak = (currentStreakVal != null) ? currentStreakVal : 0;
                int longestStreak = (longestStreakVal != null) ? longestStreakVal : 0;

                boolean updateStreak = false;

                if (lastLoginDate == null) {
                    currentStreak = 1;
                    longestStreak = 1;

                    Map<String, Object> newUserData = new HashMap<>();
                    newUserData.put("lastLoginDate", todayStr);
                    newUserData.put("currentStreak", currentStreak);
                    newUserData.put("longestStreak", longestStreak);

                    statsRef.updateChildren(newUserData);
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
                        e.printStackTrace();
                    }

                    if (updateStreak) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("lastLoginDate", todayStr);
                        updates.put("currentStreak", currentStreak);
                        updates.put("longestStreak", longestStreak);
                        statsRef.updateChildren(updates);
                    }
                }

                currentStreakTV.setText(String.valueOf(currentStreak));
                longestStreakTV.setText(String.valueOf(longestStreak));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatsActivity.this, "Error loading streak info", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHabitStats() {
        habitsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                int completed = 0;
                for (DataSnapshot habitSnap : snapshot.getChildren()) {
                    if (habitSnap.exists() && habitSnap.child("completed").getValue() != null) {
                        total++;
                        boolean isCompleted = Boolean.TRUE.equals(habitSnap.child("completed").getValue(Boolean.class));
                        if (isCompleted) completed++;
                    }
                }

                int xp = completed * 100; // 100 XP per completed habit
                int level = xp / 500;     // 500 XP per level
                int progress = xp % 500;

                totalHabitsTV.setText(String.valueOf(total));
                completedHabitsTV.setText("Habits Completed: " + completed);
                levelTV.setText("Level " + level);
                xpProgressBar.setMax(500);
                xpProgressBar.setProgress(progress);
                xpProgressTV.setText(progress + " / 500 XP");

                // Set badge based on level
                if (level >= 10) {
                    levelBadge.setImageResource(R.drawable.gold_medal);
                } else if (level >= 5) {
                    levelBadge.setImageResource(R.drawable.silver_medal);
                } else if (level >= 0){
                    levelBadge.setImageResource(R.drawable.bronze_medal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatsActivity.this, "Failed to load habits", Toast.LENGTH_SHORT).show();
            }
        });
    }

}