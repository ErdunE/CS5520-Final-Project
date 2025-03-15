package edu.northeastern.finalproject_group_1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Testing
        habitList = new ArrayList<>();
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));

        habitAdapter = new HabitAdapter(habitList);
        recyclerView.setAdapter(habitAdapter);
    }
}