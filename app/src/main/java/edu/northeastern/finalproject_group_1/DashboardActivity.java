package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Add button Click Listener
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            showAddHabitDialog();
        });

        // Bottom Navbar
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statsTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initial ViewPager2
        viewPager = findViewById(R.id.viewPager);
        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
    }

    // Pop up dialog after click add button
    private void showAddHabitDialog() {
        AddHabitDialogFragment dialog = new AddHabitDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddHabitDialog");
    }

    // Add new habit
    public void addHabitToList(Habit newHabit) {
        HabitListFragment fragment = (HabitListFragment) getSupportFragmentManager()
                .findFragmentByTag("f1");

        if (fragment != null) {
            fragment.addHabit(newHabit);
        }
    }

    public void showEditHabitDialog(Habit habit, int position) {
        AddHabitDialogFragment dialog = new AddHabitDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("isEditMode", true);
        args.putInt("position", position);
        args.putString("title", habit.getTitle());
        args.putString("description", habit.getDescription());
        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "EditHabitDialog");
    }

    public void updateHabitInList(int position, String newTitle, String newDescription) {
        HabitListFragment fragment = (HabitListFragment) getSupportFragmentManager()
                .findFragmentByTag("f1");

        if (fragment != null) {
            fragment.updateHabit(position, newTitle, newDescription);
        }
    }
}