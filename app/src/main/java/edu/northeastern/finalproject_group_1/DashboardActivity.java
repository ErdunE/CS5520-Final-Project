package edu.northeastern.finalproject_group_1;

import static java.util.Objects.isNull;

import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
        if (savedInstanceState != null) {
            this.currentUser = savedInstanceState.getString("currentUser");
        } else {
            //Storing current user from parent activity
            Intent parentIntent = getIntent();
            if (parentIntent.hasExtra("USERNAME")) {
                currentUser = parentIntent.getStringExtra("USERNAME");
            }
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
        setupNavigation(currentUser);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.statsTV), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupNavigation(String currentUser) {
        // Initialize viewpager and adapter
        pagerAdapter = new DashboardPagerAdapter(this, currentUser);
        viewPager.setAdapter(pagerAdapter);

        // Set viewpager orientation
        viewPager.setOrientation(ViewPager2.ORIENTATION_VERTICAL);

        // Set up bottom navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                //pass along username to child activities
                if (itemId == R.id.Shop) {
                    Intent intent = new Intent(DashboardActivity.this, MarketplaceActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Challenges) {
                    Intent intent = new Intent(DashboardActivity.this, ChallengesActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Stats) {
                    Intent intent = new Intent(DashboardActivity.this, StatsActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Settings) {
                    Intent intent = new Intent(DashboardActivity.this, SettingsActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
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

        // Test code for reminder, current + per 10s
        String[] testHabitTitles = {
                "Drink Water", "Workout", "Read Books", "Meditate"
        };

        for (int i = 0; i < testHabitTitles.length; i++) {
            Calendar testTime = Calendar.getInstance();
            testTime.add(Calendar.SECOND, 10 * (i + 1));

            String habitTitle = testHabitTitles[i];
            String message = ReminderMessagePool.getRandomMessage(habitTitle);

            List<Calendar> testTimes = new ArrayList<>();
            testTimes.add(testTime);

            ReminderScheduler.scheduleReminders(
                    DashboardActivity.this,
                    900 + i,
                    habitTitle,
                    testTimes
            );
        }
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
        //Log.d("NewHabit", "Start Date: " + (newHabit.getStartDateMillis() != null ? newHabit.getStartDateMillis() : "null"));
        //Log.d("NewHabit", "End Date: " + (newHabit.getEndDate() != null ? newHabit.getEndDate().getTime().toString() : "null"));
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
        Calendar cS = Calendar.getInstance();
        cS.setTimeInMillis(newHabit.getStartDateMillis());
        Calendar cE = Calendar.getInstance();
        cE.setTimeInMillis(newHabit.getEndDateMillis());
        List<Calendar> reminderTimes = ReminderTimeGenerator.generateReminderTimes(
                cS,
                cE,
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

        HabitListFragment fragment = pagerAdapter.getHabitListFragment();
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

        Calendar start = Calendar.getInstance();
        start.setTimeInMillis(habit.getStartDateMillis());
        Calendar end = Calendar.getInstance();
        end.setTimeInMillis(habit.getEndDateMillis());
        List<Calendar> reminderTimes = ReminderTimeGenerator.generateReminderTimes(
                start,
                end,
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
        args.putInt("iconResId", habit.getIconResId());
        args.putString("customIconUri", habit.getCustomIconUri());
        args.putInt("customColor", habit.getCustomColor());
        args.putString("repeatUnit", habit.getRepeatUnit());
        args.putInt("every", habit.getEvery());
        args.putIntegerArrayList("weekdays", new ArrayList<>(habit.getWeekdays()));
        args.putLong("startDate", habit.getStartDateMillis());
        args.putLong("endDate", habit.getEndDateMillis());
        args.putStringArrayList("reminderTimes", new ArrayList<>(habit.getReminderTimes()));
        args.putString("habitKey", habit.getHabitKey());
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

        //update existing habit in db if the key is set
        if (!isNull(updatedHabit.getHabitKey())) {
            fragment.editHabit(updatedHabit);
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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", this.currentUser);
    }

    public String getCurrentUser() {
        return this.currentUser;
    }
}