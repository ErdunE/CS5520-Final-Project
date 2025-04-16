package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChallengesActivity extends AppCompatActivity {

    private GridLayout challengesGrid;
    private String currentUser;
    private FirebaseDatabase database;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton gardenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenges);

        // Get current user from intent
        Intent intent = getIntent();
        currentUser = intent.getStringExtra("USERNAME");
        if (currentUser == null || currentUser.isEmpty()) {
            Toast.makeText(this, "User information missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        challengesGrid = findViewById(R.id.challenges_grid);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        gardenButton = findViewById(R.id.gardenButton);

        // Initialize Firebase
        database = FirebaseDatabase.getInstance();

        // Setup navigation
        setupNavigation();

        // Populate challenge grid
        populateChallenges();
    }

    private void setupNavigation() {
        // Set current selected item
        bottomNavigationView.setSelectedItemId(R.id.Challenges);

        // Set navigation item listener
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.Inventory) {
                Intent intent = new Intent(ChallengesActivity.this, MarketplaceActivity.class);
                intent.putExtra("USERNAME", currentUser);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.Stats) {
                Intent intent = new Intent(ChallengesActivity.this, StatsActivity.class);
                intent.putExtra("USERNAME", currentUser);
                startActivity(intent);
                finish();
                return true;
            } else if (itemId == R.id.Settings) {
                Intent intent = new Intent(ChallengesActivity.this, SettingsActivity.class);
                intent.putExtra("USERNAME", currentUser);
                startActivity(intent);
                finish();
                return true;
            }
            return false;
        });

        // Set garden button click listener
        gardenButton.setOnClickListener(v -> {
            Intent intent = new Intent(ChallengesActivity.this, DashboardActivity.class);
            intent.putExtra("USERNAME", currentUser);
            startActivity(intent);
            finish();
        });
    }

    private void populateChallenges() {
        // Define predefined challenges
        // Each challenge is defined as title, description, icon, duration (days), repeat unit
        Object[][] challengeData = {
                {"Drink More Water", "Drink 8 glasses of water daily", R.drawable.baseline_water_drop_24, 30, "Daily"},
                {"Daily Exercise", "30 minutes of exercise daily", R.drawable.baseline_fitness_center_24, 30, "Daily"},
                {"Meditation", "10 minutes of meditation daily", R.drawable.bonsai, 21, "Daily"},
                {"Early Riser", "Wake up at 6 AM", R.drawable.ic_bedtime, 21, "Daily"},
                {"Reading Habit", "Read for 30 minutes", R.drawable.ic_book, 30, "Daily"},
                {"Digital Detox", "Limit screen time to 2 hours", R.drawable.ic_social, 14, "Daily"}
        };

        // Create and add view for each challenge
        for (Object[] challenge : challengeData) {
            String title = (String) challenge[0];
            String description = (String) challenge[1];
            int iconResId = (int) challenge[2];
            int duration = (int) challenge[3];
            String repeatUnit = (String) challenge[4];

            // Inflate challenge item view
            View challengeView = LayoutInflater.from(this).inflate(
                    R.layout.item_challenge_simple, challengesGrid, false);

            // Set up views
            ImageView iconView = challengeView.findViewById(R.id.challenge_icon);
            TextView titleView = challengeView.findViewById(R.id.challenge_title);
            TextView descriptionView = challengeView.findViewById(R.id.challenge_description);
            Button joinButton = challengeView.findViewById(R.id.btn_join_challenge);

            iconView.setImageResource(iconResId);
            titleView.setText(title);
            descriptionView.setText(description);

            // Set up join button
            joinButton.setOnClickListener(v -> {
                showJoinDialog(title, description, iconResId, duration, repeatUnit);
            });

            // Add to grid
            challengesGrid.addView(challengeView);
        }
    }

    private void showJoinDialog(String title, String description, int iconResId,
                                int duration, String repeatUnit) {
        new AlertDialog.Builder(this)
                .setTitle("Join Challenge")
                .setMessage("Do you want to join the \"" + title + "\" challenge? " +
                        "This will add a new habit to your list.")
                .setPositiveButton("Join", (dialog, which) -> {
                    addChallengeHabit(title, description, iconResId, duration, repeatUnit);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addChallengeHabit(String title, String description, int iconResId,
                                   int duration, String repeatUnit) {
        // Create a new habit based on the challenge
        long startDateMillis = System.currentTimeMillis();
        long endDateMillis = startDateMillis + (duration * 24 * 60 * 60 * 1000L); // duration in milliseconds

        List<Integer> weekdays = new ArrayList<>();
        if (repeatUnit.equals("Weekly")) {
            // If weekly, default to all days
            weekdays.add(Calendar.SUNDAY);
            weekdays.add(Calendar.MONDAY);
            weekdays.add(Calendar.TUESDAY);
            weekdays.add(Calendar.WEDNESDAY);
            weekdays.add(Calendar.THURSDAY);
            weekdays.add(Calendar.FRIDAY);
            weekdays.add(Calendar.SATURDAY);
        }

        Habit newHabit = new Habit(
                title,
                description,
                false, // not completed
                iconResId,
                repeatUnit, // repeat schedule (daily, weekly)
                50, // default reward points
                null, // no custom icon
                0, // no custom color
                repeatUnit, // repeat unit
                1, // every 1 day/week
                weekdays,
                startDateMillis,
                endDateMillis,
                new ArrayList<>(), // no reminder times initially
                null, // habit key will be set when added to database
                -1, // last completed timestamp
                0  // total completed count
        );

        // Add habit to Firebase
        DatabaseReference habitsRef = database.getReference("HABITS").child(currentUser);
        String key = habitsRef.push().getKey();

        if (key != null) {
            habitsRef.child(key).setValue(newHabit)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Challenge joined successfully!", Toast.LENGTH_SHORT).show();
                        // Return to dashboard
                        Intent intent = new Intent(ChallengesActivity.this, DashboardActivity.class);
                        intent.putExtra("USERNAME", currentUser);
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to join challenge", Toast.LENGTH_SHORT).show();
                    });
        }
    }
}