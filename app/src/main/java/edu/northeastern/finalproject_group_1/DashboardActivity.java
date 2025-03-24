package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private GardenView gardenView;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Find views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        gardenView = findViewById(R.id.gardenView);

        fab = findViewById(R.id.fab);

        // Set up navigation
        setupNavigation();

        // Add initial plants (for testing purposes)
        setupGarden();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statsTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Shop) {
                    startActivity(new Intent(DashboardActivity.this, MarketplaceActivity.class));
                    return true;
                } else if (itemId == R.id.Challenges) {
                    startActivity(new Intent(DashboardActivity.this, ChallengesActivity.class));
                    return true;
                } else if (itemId == R.id.Stats) {
                    startActivity(new Intent(DashboardActivity.this, StatsActivity.class));
                    return true;
                } else if (itemId == R.id.Settings) {
                    startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            }
        });

        // Set up FAB to add new plants
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // In the final version of the app, this would open the habit creation dialog
                // For now, just add a new plant
                addNewPlant();
            }
        });
    }

    private void setupGarden() {
        // Add some initial plants for testing
        gardenView.addPlant("Daily Exercise");
        gardenView.addPlant("Reading");
        gardenView.addPlant("Meditation");

        // Plants will grow when habits are completed
        // This will be integrated with your habit tracking system
    }

    private void addNewPlant() {
        // In a real application, this would be connected to the habits list
        // For now, just add a placeholder plant
        if (gardenView.getPlantCount() < 5) {
            gardenView.addPlant("New Habit " + (gardenView.getPlantCount() + 1));
            Toast.makeText(this, "Added new plant!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Garden is full! Complete existing habits first.", Toast.LENGTH_SHORT).show();
        }
    }
}