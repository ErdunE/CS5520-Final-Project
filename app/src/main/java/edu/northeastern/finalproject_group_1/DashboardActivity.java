package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DashboardPagerAdapter pagerAdapter;

    BottomNavigationView bottomNavigationView;
    private GardenView gardenView;
    private FloatingActionButton fab;

    // Declare spinner and button for testing
    private Spinner plantSelector;
    private Button testGrowButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        gardenView = findViewById(R.id.gardenView);
        fab = findViewById(R.id.fab);

        // Initialize test controls
        plantSelector = findViewById(R.id.plantSelector);
        testGrowButton = findViewById(R.id.testGrowButton);

        // Set up navigation
        setupNavigation();

        // Set up garden
        setupGarden();

        // Set up test controls
        setupTestControls();

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
                // Add a new plant
                addNewPlant();
            }
        });
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
    }

    private void setupGarden() {
        // Add some initial plants for testing
        gardenView.addPlant("Daily Exercise");
        gardenView.addPlant("Reading");
        gardenView.addPlant("Meditation");
    }

    private void addNewPlant() {
        // In a real application, this would be connected to the habits list
        // For now, just add a placeholder plant
        if (gardenView.getPlantCount() < 5) {
            gardenView.addPlant("New Habit " + (gardenView.getPlantCount() + 1));
            Toast.makeText(this, "Added new plant!", Toast.LENGTH_SHORT).show();

            // Update the plant selector
            updatePlantSelector();
        } else {
            Toast.makeText(this, "Garden is full! Complete existing habits first.", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupTestControls() {
        // Initial setup of the plant selector
        updatePlantSelector();

        // Set up test grow button
        testGrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedPosition = plantSelector.getSelectedItemPosition();
                if (selectedPosition >= 0 && selectedPosition < gardenView.getPlantCount()) {
                    gardenView.growPlant(selectedPosition);
                    Toast.makeText(DashboardActivity.this,
                            "Plant " + (selectedPosition + 1) + " grew!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DashboardActivity.this,
                            "Please select a plant to grow",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updatePlantSelector() {
        // Create a list of plant names for the spinner
        String[] plantItems = new String[gardenView.getPlantCount()];
        for (int i = 0; i < gardenView.getPlantCount(); i++) {
            plantItems[i] = "Plant " + (i + 1);
        }

        // Create adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, plantItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply adapter to spinner
        plantSelector.setAdapter(adapter);
    }
}