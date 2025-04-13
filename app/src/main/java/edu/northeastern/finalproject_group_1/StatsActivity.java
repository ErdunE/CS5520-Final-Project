package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.graphics.Color;
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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {

    private TextView usernameTV, totalHabitsTV, currentStreakTV, longestStreakTV, completedHabitsTV, levelTV, xpProgressTV;
    private ProgressBar xpProgressBar;
    private String currentUser;
    private ImageView levelBadge;
    private DatabaseReference habitsRef, gardenRef;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private LinearLayout achievementIconsLayout;
    FloatingActionButton gardenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stats);

        gardenButton = findViewById(R.id.gardenButton);

        // Setup garden FAB
        gardenButton.setOnClickListener(v -> {
            Intent intent = new Intent(StatsActivity.this, DashboardActivity.class);
            startActivity(intent);
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Shop) {
                    startActivity(new Intent(StatsActivity.this, MarketplaceActivity.class));
                    return true;
                } else if (itemId == R.id.Challenges) {
                    startActivity(new Intent(StatsActivity.this, ChallengesActivity.class));
                    return true;
                } else if (itemId == R.id.Stats) {
                    startActivity(new Intent(StatsActivity.this, StatsActivity.class));
                    return true;
                } else if (itemId == R.id.Settings) {
                    startActivity(new Intent(StatsActivity.this, SettingsActivity.class));
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
        achievementIconsLayout = findViewById(R.id.achievementIconsLayout);

        gardenRef = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(currentUser);
        habitsRef = FirebaseDatabase.getInstance().getReference("HABITS").child(currentUser);

        usernameTV.setText("Hi, " + currentUser);
        updateLoginStreak();
        loadHabitStats();

    }

    private void updateLoginStreak() {
        gardenRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String today = LocalDate.now().toString();
                String lastLogin = snapshot.child("lastLoginDate").getValue(String.class);
                int streak = snapshot.child("streak").getValue(Integer.class) != null ? snapshot.child("streak").getValue(Integer.class) : 0;
                int longestStreak = snapshot.child("longestStreak").getValue(Integer.class) != null ? snapshot.child("longestStreak").getValue(Integer.class) : 0;

                if (lastLogin == null) {
                    gardenRef.child("lastLoginDate").setValue(today);
                    gardenRef.child("streak").setValue(1);
                    gardenRef.child("longestStreak").setValue(1);
                    currentStreakTV.setText("1");
                    longestStreakTV.setText("1");
                } else {
                    LocalDate todayDate = LocalDate.parse(today);
                    LocalDate lastLoginDate = LocalDate.parse(lastLogin);
                    long daysBetween = ChronoUnit.DAYS.between(lastLoginDate, todayDate);

                    if (daysBetween == 1) {
                        streak++;
                        longestStreak = Math.max(streak, longestStreak);
                    } else if (daysBetween > 1) {
                        streak = 1;
                    }

                    gardenRef.child("streak").setValue(streak);
                    gardenRef.child("lastLoginDate").setValue(today);
                    gardenRef.child("longestStreak").setValue(longestStreak);

                    currentStreakTV.setText(String.valueOf(streak));
                    longestStreakTV.setText(String.valueOf(longestStreak));
                }
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