package edu.northeastern.finalproject_group_1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton gardenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        TextView usernameTextView = findViewById(R.id.usernameText);
        TextView userIdTextView = findViewById(R.id.userIdText);

        String username = getSharedPreferences("HabitendPrefs", MODE_PRIVATE)
                .getString("logged_in_username", "Guest");

        usernameTextView.setText(username);
        userIdTextView.setText("ID: " + username);

        RecyclerView recyclerView = findViewById(R.id.settingsRecyclerView);
        List<SettingOption> options = Arrays.asList(
                new SettingOption(R.drawable.ic_settings_friends, "Friends List"),
                new SettingOption(R.drawable.ic_settings_about, "About Habitend"),
                new SettingOption(R.drawable.ic_settings_privacy, "Privacy & Terms"),
                new SettingOption(R.drawable.ic_settings_log_out, "Log Out")
        );

        gardenButton = findViewById(R.id.gardenButton);

        // Setup garden FAB
        gardenButton.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, DashboardActivity.class);
            intent.putExtra("USERNAME", username);
            startActivity(intent);
        });


        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Shop) {
                    Intent intent = new Intent(SettingsActivity.this, MarketplaceActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Challenges) {
                    Intent intent = new Intent(SettingsActivity.this, ChallengesActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Stats) {
                    Intent intent = new Intent(SettingsActivity.this, StatsActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Settings) {
                    Intent intent = new Intent(SettingsActivity.this, SettingsActivity.class);
                    intent.putExtra("USERNAME", username);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingOptionAdapter adapter = new SettingOptionAdapter(options, option -> {
            switch (option.getTitle()) {
                case "Friends List":
                    startActivity(new Intent(SettingsActivity.this, FriendsListActivity.class));
                    break;
                case "About Habitend":
                    showAboutDialog();
                    break;
                case "Privacy & Terms":
                    showPrivacyDialog();
                    break;
                case "Log Out":
                    showLogoutDialog();
                    break;
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void showAboutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("About Habitend")
                .setMessage(
                        "Habitend v1.0.0\n\n" +
                        "A habit tracking app with a growing garden!\n\n" +
                        "Developed by Group 1:\n" +
                        "Jenny Nguyen\n" +
                        "Erdun E\n" +
                        "Alp Yalcinkaya\n" +
                        "Megan Heinhold\n" +
                        "for CS5520 Mobile App Development\n" +
                        "Khoury College, Northeastern University\n\n" +
                        "This app is a course project and intended for educational use only.\n\n" +
                        "GitHub: github.com/CS5520Spring2025Feinberg/project-group-1"
                )
                .setPositiveButton("OK", null)
                .show();
    }

    private void showPrivacyDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Privacy & Terms")
                .setMessage(
                        "Habitend stores your habit data locally and in Firebase to support features like reminders and syncing.\n" +
                        "Your information is not shared externally.\n\n" +
                        "This app is a CS5520 course project and is intended for learning purposes only."
                )
                .setPositiveButton("Understood", null)
                .show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Log out?")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log out", (dialog, which) -> {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}