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


public class StatsActivity extends AppCompatActivity {

    private TextView usernameTV, totalHabitsTV, currentStreakTV, longestStreakTV, completedHabitsTV, levelTV, xpProgressTV;
    private ProgressBar xpProgressBar;
    private String currentUser;
    private ImageView levelBadge;
    private DatabaseReference habitsRef;
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

        habitsRef = FirebaseDatabase.getInstance().getReference("HABITS").child(currentUser);

        usernameTV.setText("Hi, " + currentUser);
        loadStreakStats();
        loadHabitStats();

    }

    private void loadHabitStats() {
        habitsRef.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int totalHabits = 0;
                int completed= 0;
                int totalCompletions = 0;

                for (DataSnapshot habitSnap : snapshot.getChildren()) {
                    totalHabits++;

                    // Count "completed" habits
                    Boolean isCompleted = habitSnap.child("completed").getValue(Boolean.class);
                    if (Boolean.TRUE.equals(isCompleted)) {
                        completed++;
                    }

                    // Sum totalCompleted values
                    Long completions = habitSnap.child("totalCompleted").getValue(Long.class);
                    if (completions != null) {
                        totalCompletions += completions.intValue();
                    }
                }

                int xp = totalCompletions * 100; // 100 XP per completed habit
                int level = xp / 500;     // 500 XP per level
                int progress = xp % 500;

                totalHabitsTV.setText(String.valueOf(totalHabits));
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
                } else {
                    levelBadge.setImageResource(R.drawable.bronze_medal);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatsActivity.this, "Failed to load habits", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadStreakStats() {
        DatabaseReference statsRef = FirebaseDatabase.getInstance()
                .getReference("GARDENDATA")
                .child(currentUser);

        statsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentStreak = snapshot.child("currentStreak").getValue(Integer.class);
                Integer longestStreak = snapshot.child("longestStreak").getValue(Integer.class);

                if (currentStreak != null) {
                    currentStreakTV.setText(String.valueOf(currentStreak));
                } else {
                    currentStreakTV.setText("0");
                }

                if (longestStreak != null) {
                    longestStreakTV.setText(String.valueOf(longestStreak));
                } else {
                    longestStreakTV.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(StatsActivity.this, "Failed to load streak data", Toast.LENGTH_SHORT).show();
            }
        });
    }


}