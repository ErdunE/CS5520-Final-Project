package edu.northeastern.finalproject_group_1;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;

public class AddHabitDialogFragment extends DialogFragment {

    private EditText titleEditText;
    private EditText descriptionEditText;
    private boolean isEditMode = false;
    private int position = -1;
    private String oldTitle = "";
    private String oldDescription = "";

    public AddHabitDialogFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_habit, null);

        titleEditText = dialogView.findViewById(R.id.editHabitTitle);
        descriptionEditText = dialogView.findViewById(R.id.editHabitDescription);

        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("isEditMode", false);
            position = getArguments().getInt("position", -1);
            oldTitle = getArguments().getString("title", "");
            oldDescription = getArguments().getString("description", "");
        }

        if (isEditMode) {
            titleEditText.setText(oldTitle);
            descriptionEditText.setText(oldDescription);
        }

        builder.setView(dialogView)
                .setTitle(isEditMode ? "Edit Habit" : "Add Habit")
                .setPositiveButton(isEditMode ? "Save" : "Add", (dialog, which) -> {
                    String title = titleEditText.getText().toString().trim();
                    String description = descriptionEditText.getText().toString().trim();

                    if (isEditMode) {
                        ((DashboardActivity) requireActivity()).updateHabitInList(position, title, description);
                    } else {
                        Habit newHabit = new Habit(title, description, false,
                                R.drawable.baseline_checkbox_24, "Daily", 0);
                        ((DashboardActivity) requireActivity()).addHabitToList(newHabit);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }
}