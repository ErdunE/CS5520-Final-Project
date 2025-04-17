package edu.northeastern.finalproject_group_1;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.Calendar;

public class GardenFragment extends Fragment {
    private final String TAG = "GardenFragment";

    private GardenView gardenView;
    private Spinner plantSelector;
    private Button testGrowButton;
    private Button addPlantButton;
    private String currentUser;
    private FirebaseDatabase db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_garden, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            this.currentUser = savedInstanceState.getString("currentUser");
        }

        // Initialize views
        gardenView = view.findViewById(R.id.gardenView);
        plantSelector = view.findViewById(R.id.plantSelector);
        testGrowButton = view.findViewById(R.id.testGrowButton);
        addPlantButton = view.findViewById(R.id.addPlantButton);

        db = FirebaseDatabase.getInstance();

        // Set up garden
        setupGarden();

        // Set up test controls
        //setupTestControls();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", this.currentUser);
    }

    private void setupGarden() {
        // Initialize plants for the user's habits
        Log.d(TAG, "Creating garden for " + currentUser);
        getHabits(currentUser);

    }

    private void getHabits(String currentUser) {
        DatabaseReference dbHabits = db.getReference("HABITS").child(currentUser);
        dbHabits.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                gardenView.resetPlants();
                for (DataSnapshot h: snapshot.getChildren()) {
                    Habit habit = h.getValue(Habit.class);
                    String key = h.getKey();
                    habit.setHabitKey(key);
                    Log.d(TAG, "Adding a plant for habit " + key);
                    addNewPlant(key);
                    int completed = habit.getTotalCompleted();
                    for (int i = 0; i < completed; i++) {
                        growPlantByHabit(key);
                    }
                    //reset the completed flag if needed
                    if (habit.isCompleted()) {
                        if (!completedToday(habit)) {
                            Log.d(TAG, "Resetting completed flag for habit");
                            habit.setCompleted(false);
                            dbHabits.child(h.getKey()).child("completed").setValue(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading habits for garden: " + error);
            }
        });
    }

    public boolean completedToday(Habit habit) {
        LocalDate t = LocalDate.now();
        Calendar c = Calendar.getInstance();
        if (habit.getLastCompletedMillis() != -1) {
            c.setTimeInMillis(habit.getLastCompletedMillis());
            LocalDate l = LocalDate.of(c.get(c.YEAR), c.get(c.MONTH) + 1, c.get(c.DATE));
            return t.isEqual(l);
        }
        return false;
    }

    public void addNewPlant(String habitName) {
        // Removing the limit check - allow unlimited plants
        gardenView.addPlant(habitName);
        //Toast.makeText(getContext(), "Added new plant!", Toast.LENGTH_SHORT).show();

        // Update the plant selector
        updatePlantSelector();
    }

    private void setupTestControls() {
        // Initial setup of the plant selector
        updatePlantSelector();

        // Set up test grow button
        testGrowButton.setOnClickListener(v -> {
            int selectedPosition = plantSelector.getSelectedItemPosition();
            if (selectedPosition >= 0 && selectedPosition < gardenView.getPlantCount()) {
                gardenView.growPlant(selectedPosition);
                Toast.makeText(getContext(),
                        "Plant " + (selectedPosition + 1) + " grew!",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(),
                        "Please select a plant to grow",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Set up add plant button
//        addPlantButton.setOnClickListener(v -> {
//            addNewPlant();
//        });
    }

    private void updatePlantSelector() {
        if (getContext() == null || gardenView == null) return;

        // Create a list of plant names for the spinner
        String[] plantItems = new String[gardenView.getPlantCount()];
        for (int i = 0; i < gardenView.getPlantCount(); i++) {
            plantItems[i] = "Plant " + (i + 1);
        }

        // Create adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                plantItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply adapter to spinner
        plantSelector.setAdapter(adapter);
    }

    public void growPlantByHabit(String habitName) {
        if (gardenView != null) {
            gardenView.growPlantByHabitName(habitName);
        }
    }

    public void setUser(String user) {
        this.currentUser = user;
    }

}