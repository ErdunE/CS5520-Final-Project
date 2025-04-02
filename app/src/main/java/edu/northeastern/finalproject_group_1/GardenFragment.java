package edu.northeastern.finalproject_group_1;

import android.os.Bundle;
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

public class GardenFragment extends Fragment {

    private GardenView gardenView;
    private Spinner plantSelector;
    private Button testGrowButton;
    private Button addPlantButton;

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

        // Initialize views
        gardenView = view.findViewById(R.id.gardenView);
        plantSelector = view.findViewById(R.id.plantSelector);
        testGrowButton = view.findViewById(R.id.testGrowButton);
        addPlantButton = view.findViewById(R.id.addPlantButton);

        // Set up garden - we're not adding initial plants anymore
        setupGarden();

        // Set up test controls
        setupTestControls();
    }

    private void setupGarden() {
        // We're not adding initial plants anymore
        // The garden will start empty
    }

    public void addNewPlant() {
        // In a real application, this would be connected to the habits list
        // For now, just add a placeholder plant
        if (gardenView.getPlantCount() < 5) {
            gardenView.addPlant("New Habit " + (gardenView.getPlantCount() + 1));
            Toast.makeText(getContext(), "Added new plant!", Toast.LENGTH_SHORT).show();

            // Update the plant selector
            updatePlantSelector();
        } else {
            Toast.makeText(getContext(), "Garden is full! Complete existing habits first.", Toast.LENGTH_SHORT).show();
        }
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
        addPlantButton.setOnClickListener(v -> {
            addNewPlant();
        });
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
}