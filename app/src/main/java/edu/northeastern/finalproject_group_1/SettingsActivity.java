package edu.northeastern.finalproject_group_1;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        RecyclerView recyclerView = findViewById(R.id.settingsRecyclerView);
        List<SettingOption> options = Arrays.asList(
                new SettingOption(R.drawable.ic_settings_about, "About Habitend"),
                new SettingOption(R.drawable.ic_settings_privacy, "Privacy & Terms"),
                new SettingOption(R.drawable.ic_settings_log_out, "Log Out")
        );

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SettingOptionAdapter adapter = new SettingOptionAdapter(options, option -> {
            switch (option.getTitle()) {
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