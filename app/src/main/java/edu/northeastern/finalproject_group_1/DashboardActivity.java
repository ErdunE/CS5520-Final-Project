package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DashboardPagerAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);
        fab = findViewById(R.id.fab);

        // Set up navigation
        setupNavigation();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statsTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupNavigation() {
        // Initialize viewpager and adapter
        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Set viewpager orientation
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // Set up bottom navigation
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

        // Set up FAB to add new plants when in garden view
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if we're in the garden view (position 0)
                if (viewPager.getCurrentItem() == 0) {
                    // Get current fragment
                    Fragment currentFragment = getSupportFragmentManager()
                            .findFragmentByTag("f" + viewPager.getCurrentItem());

                    if (currentFragment instanceof GardenFragment) {
                        ((GardenFragment) currentFragment).addNewPlant();
                    }
                }
            }
        });
    }
}