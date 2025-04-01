package edu.northeastern.finalproject_group_1;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private DashboardPagerAdapter pagerAdapter;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton fab;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Storing current user from login
        Intent loginIntent = getIntent();
        if (loginIntent.hasExtra("USERNAME")) {
            currentUser = loginIntent.getStringExtra("USERNAME");
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                finish();
                return;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Initialize views
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        viewPager = findViewById(R.id.viewPager);
        fab = findViewById(R.id.fab);

        // Add button Click Listener
        fab.setOnClickListener(v -> {
            showAddHabitDialog();
        });

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

        // There was added by Alp for add new plants, but after discussed we decide use the add button for add new habits, just in case, left the old code right there, but need to be cleaned before final submission
//        // Set up FAB to add new plants when in garden view
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Check if we're in the garden view (position 0)
//                if (viewPager.getCurrentItem() == 0) {
//                    // Get current fragment
//                    Fragment currentFragment = getSupportFragmentManager()
//                            .findFragmentByTag("f" + viewPager.getCurrentItem());
//
//                    if (currentFragment instanceof GardenFragment) {
//                        ((GardenFragment) currentFragment).addNewPlant();
//                    }
//                }
//            }
//        });

        // Test code for reminder, current + 10s
        Calendar testTime = Calendar.getInstance();
        testTime.add(Calendar.SECOND, 10);
        List<Calendar> testTimes = new ArrayList<>();
        testTimes.add(testTime);
        ReminderScheduler.scheduleReminders(
                DashboardActivity.this,
                999,
                "Test Water Reminder",
                testTimes
        );
    }

    // Pop up dialog after click add button
    private void showAddHabitDialog() {
        AddHabitDialogFragment dialog = new AddHabitDialogFragment();
        dialog.show(getSupportFragmentManager(), "AddHabitDialog");
    }

    // Add new habit
    public void addHabitToList(Habit newHabit) {
        // Test log
        Log.d("NewHabit", "Title: " + newHabit.getTitle());
        Log.d("NewHabit", "Description: " + newHabit.getDescription());
        Log.d("NewHabit", "Repeat Unit: " + newHabit.getRepeatUnit());
        Log.d("NewHabit", "Every: " + newHabit.getEvery());
        Log.d("NewHabit", "Weekdays: " + newHabit.getWeekdays());
        Log.d("NewHabit", "Start Date: " + (newHabit.getStartDate() != null ? newHabit.getStartDate().getTime().toString() : "null"));
        Log.d("NewHabit", "End Date: " + (newHabit.getEndDate() != null ? newHabit.getEndDate().getTime().toString() : "null"));
        Log.d("NewHabit", "Reminder Times: " + newHabit.getReminderTimes());
        Log.d("NewHabit", "Custom Icon URI: " + newHabit.getCustomIconUri());
        Log.d("NewHabit", "Color: " + newHabit.getCustomColor());

        List<Calendar> timeCalendars = new ArrayList<>();
        for (String time : newHabit.getReminderTimes()) {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                timeCalendars.add(cal);
            }
        }

        List<Calendar> reminderTimes = ReminderTimeGenerator.generateReminderTimes(
                newHabit.getStartDate(),
                newHabit.getEndDate(),
                ReminderTimeGenerator.RepeatUnit.valueOf(newHabit.getRepeatUnit().toUpperCase()),
                newHabit.getEvery(),
                newHabit.getWeekdays(),
                timeCalendars
        );

        for (Calendar reminderTime : reminderTimes) {
            Log.d("ReminderScheduler", "Scheduled reminder at " + reminderTime.getTime().toString());
        }

        ReminderScheduler.scheduleReminders(
                this,
                newHabit.hashCode(),
                newHabit.getTitle(),
                reminderTimes
        );

        Log.d("NewHabit", "ReminderScheduler called for: " + newHabit.getTitle());

        HabitListFragment fragment = (HabitListFragment) getSupportFragmentManager()
                .findFragmentByTag("f1");

        if (fragment != null) {
            fragment.addHabit(newHabit);
        }
    }

    public void deleteHabitReminder(Habit habit) {
        ReminderTimeGenerator.RepeatUnit unit =
                ReminderTimeGenerator.RepeatUnit.valueOf(habit.getRepeatUnit().toUpperCase());

        List<Calendar> timeCalendars = new ArrayList<>();
        for (String time : habit.getReminderTimes()) {
            String[] parts = time.split(":");
            if (parts.length == 2) {
                int hour = Integer.parseInt(parts[0]);
                int minute = Integer.parseInt(parts[1]);
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minute);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                timeCalendars.add(cal);
            }
        }

        List<Calendar> reminderTimes = ReminderTimeGenerator.generateReminderTimes(
                habit.getStartDate(),
                habit.getEndDate(),
                unit,
                habit.getEvery(),
                habit.getWeekdays(),
                timeCalendars
        );

        for (Calendar cal : reminderTimes) {
            Log.d("ReminderScheduler", "Cancelling reminder at " + cal.getTime().toString());
        }

        ReminderScheduler.cancelReminders(this, habit.hashCode(), reminderTimes);
    }

    public void showEditHabitDialog(Habit habit, int position) {
        AddHabitDialogFragment dialog = new AddHabitDialogFragment();
        Bundle args = new Bundle();
        args.putBoolean("isEditMode", true);
        args.putInt("position", position);
        args.putString("title", habit.getTitle());
        args.putString("description", habit.getDescription());
        args.putString("repeatUnit", habit.getRepeatUnit());
        args.putInt("every", habit.getEvery());
        args.putIntegerArrayList("weekdays", new ArrayList<>(habit.getWeekdays()));
        args.putLong("startDate", habit.getStartDate().getTimeInMillis());
        args.putLong("endDate", habit.getEndDate().getTimeInMillis());
        args.putStringArrayList("reminderTimes", new ArrayList<>(habit.getReminderTimes()));
        dialog.setArguments(args);

        dialog.show(getSupportFragmentManager(), "EditHabitDialog");
    }

    public void updateHabitInList(int position, Habit updatedHabit) {
        HabitListFragment fragment = (HabitListFragment) getSupportFragmentManager()
                .findFragmentByTag("f1");

        if (fragment == null) {
            Log.e("DashboardActivity", "HabitListFragment not found for tag 'f1'. Update aborted.");
            return;
        }

        List<Habit> habitList = fragment.getHabitList();
        if (position >= 0 && position < habitList.size()) {
            Habit oldHabit = habitList.get(position);

            ReminderScheduler.updateReminders(this, oldHabit, updatedHabit);

            habitList.set(position, updatedHabit);
            fragment.getHabitAdapter().notifyItemChanged(position);
        } else {
            Log.w("DashboardActivity", "Invalid habit position: " + position);
        }
    }
}