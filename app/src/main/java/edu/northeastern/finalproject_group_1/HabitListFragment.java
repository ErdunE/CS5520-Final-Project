package edu.northeastern.finalproject_group_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class HabitListFragment extends Fragment {

    private RecyclerView habitRecyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_habit_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        habitRecyclerView = view.findViewById(R.id.habitRecyclerView);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

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
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false, R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false, R.drawable.baseline_fitness_center_24, "Daily", 20));

        habitAdapter = new HabitAdapter(habitList);
        habitRecyclerView.setAdapter(habitAdapter);
    }
}
