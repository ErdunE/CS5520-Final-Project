package edu.northeastern.finalproject_group_1;

import android.app.Dialog;
import android.app.TimePickerDialog;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.yalantis.ucrop.UCrop;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.lang.reflect.Field;

public class AddHabitDialogFragment extends DialogFragment {

    private boolean isEditMode = false;
    private int position = -1;
    private String oldTitle = "";
    private String oldDescription = "";
    private int selectedIconResId = IconData.getDefaultIcon();
    private static final int REQUEST_PICK_IMAGE = 1001;
    private static final int REQUEST_CROP_IMAGE = 1002;
    private ImageView iconPreviewImage;
    private FrameLayout iconPreviewContainer;
    private Uri croppedUri = null;


    private final String[] repeatOptions = {"None", "Daily", "Weekly", "Monthly", "Yearly"};

    public AddHabitDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_habit, null);

        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("isEditMode", false);
            position = getArguments().getInt("position", -1);
            oldTitle = getArguments().getString("title", "");
            oldDescription = getArguments().getString("description", "");
        }

        ImageButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        ImageButton btnSave = dialogView.findViewById(R.id.btnSave);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        EditText titleEditText = dialogView.findViewById(R.id.editHabitTitle);
        EditText descriptionEditText = dialogView.findViewById(R.id.editHabitDescription);

        TextView tvRepeatSelected = dialogView.findViewById(R.id.tvRepeatSelected);
        LinearLayout repeatPickerContainer = dialogView.findViewById(R.id.repeatPickerContainer);

        TextView textIntervalUnit = dialogView.findViewById(R.id.textIntervalUnit);
        FlexboxLayout weekDaySelector = dialogView.findViewById(R.id.weekDaySelector);

        TextView tvEveryValue = dialogView.findViewById(R.id.tvEveryValue);
        LinearLayout everyPickerContainer = dialogView.findViewById(R.id.everyPickerContainer);

        SwitchCompat switchReminder = dialogView.findViewById(R.id.switchReminder);
        LinearLayout reminderContainer = dialogView.findViewById(R.id.reminderContainer);
        LinearLayout reminderTimeList = dialogView.findViewById(R.id.reminderTimeList);

        Button btnAddReminder = dialogView.findViewById(R.id.btnAddReminder);
        ImageView iconCancelButton = dialogView.findViewById(R.id.iconCancelButton);
        Button btnChooseIcon = dialogView.findViewById(R.id.btnChooseIcon);
        Button btnUploadIcon = dialogView.findViewById(R.id.btnUploadIcon);

        iconPreviewContainer = dialogView.findViewById(R.id.iconPreviewContainer);
        iconPreviewImage = dialogView.findViewById(R.id.iconPreviewImage);
        iconCancelButton.setOnClickListener(v -> {
            iconPreviewImage.setImageDrawable(null);
            iconPreviewContainer.setVisibility(View.GONE);
            selectedIconResId = -1;
        });

        repeatPickerContainer.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
            View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_repeat, null);
            bottomSheetDialog.setContentView(sheetView);

            ListView listView = sheetView.findViewById(R.id.repeatListView);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_list_item_1, repeatOptions);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                String selected = repeatOptions[position];
                tvRepeatSelected.setText(selected);
                bottomSheetDialog.dismiss();

                switch (selected) {
                    case "Daily":
                        textIntervalUnit.setText("Day");
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                    case "Weekly":
                        textIntervalUnit.setText("Week");
                        weekDaySelector.setVisibility(View.VISIBLE);
                        weekDaySelector.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        weekDaySelector.requestLayout();
                        setupWeekdayPickers(dialogView);
                        break;
                    case "Monthly":
                        textIntervalUnit.setText("Month");
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                    case "Yearly":
                        textIntervalUnit.setText("Year");
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                    default:
                        textIntervalUnit.setText("");
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                }
            });

            bottomSheetDialog.show();
        });


        if (isEditMode) {
            tvDialogTitle.setText("Edit Habit");
            titleEditText.setText(oldTitle);
            descriptionEditText.setText(oldDescription);
            // TODO: edit icon
        } else {
            tvDialogTitle.setText("Add Habit");
        }

        btnChooseIcon.setOnClickListener(v -> showIconBottomSheet(iconPreviewImage, iconPreviewContainer));
        btnUploadIcon.setOnClickListener(v -> pickImageFromGallery());

        btnCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Discard Changes?")
                    .setMessage("Are you sure you want to discard all changes?")
                    .setPositiveButton("Discard", (dialogInterface, which) -> {
                        dismiss();
                    })
                    .setNegativeButton("Cancel", (dialogInterface, which) -> {
                        dialogInterface.dismiss();
                    })
                    .show();
        });

        btnSave.setOnClickListener(v -> {
            String newTitle = titleEditText.getText().toString().trim();
            String newDescription = descriptionEditText.getText().toString().trim();
            String customUri = (selectedIconResId == -1 && croppedUri != null) ? croppedUri.toString() : null;

            if (newTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isEditMode) {
                ((DashboardActivity) requireActivity()).updateHabitInList(position, newTitle, newDescription);
            } else {
                int iconToUse = (selectedIconResId != -1) ? selectedIconResId : IconData.getDefaultIcon();
                Habit newHabit = new Habit(
                        newTitle,
                        newDescription,
                        false,
                        iconToUse,
                        "Daily",
                        0,
                        customUri
                );
                ((DashboardActivity) requireActivity()).addHabitToList(newHabit);
            }

            dismiss();
        });

        everyPickerContainer.setOnClickListener(v -> {
            BottomSheetDialog numberDialog = new BottomSheetDialog(requireContext());
            View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_every_picker, null);
            numberDialog.setContentView(sheetView);

            NumberPicker picker = sheetView.findViewById(R.id.everyNumberPicker);
            picker.setMinValue(1);
            picker.setMaxValue(30);

            try {
                picker.setValue(Integer.parseInt(tvEveryValue.getText().toString()));
            } catch (NumberFormatException e) {
                picker.setValue(1);
            }

            picker.setOnValueChangedListener((np, oldVal, newVal) -> {
                tvEveryValue.setText(String.valueOf(newVal));
                numberDialog.dismiss();
            });

            numberDialog.show();
        });

        // Toggle visibility
        switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reminderContainer.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

        // Add reminder time
        btnAddReminder.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int hour = now.get(Calendar.HOUR_OF_DAY);
            int minute = now.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, selectedHour, selectedMinute) -> {
                        String time = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);

                        LinearLayout row = new LinearLayout(requireContext());
                        row.setOrientation(LinearLayout.HORIZONTAL);
                        row.setLayoutParams(new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT));
                        row.setPadding(0, 8, 0, 8);
                        row.setGravity(Gravity.CENTER_VERTICAL);

                        // minus icon
                        ImageView deleteIcon = new ImageView(requireContext());
                        deleteIcon.setImageResource(R.drawable.ic_minus);
                        deleteIcon.setLayoutParams(new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        deleteIcon.setPadding(16, 0, 16, 0);
                        deleteIcon.setColorFilter(Color.parseColor("#D3D3D3"));
                        deleteIcon.setOnClickListener(btn -> reminderTimeList.removeView(row));

                        // Label "Time"
                        TextView label = new TextView(requireContext());
                        label.setText("Time");
                        label.setTextSize(16);
                        label.setTextColor(Color.BLACK);
                        label.setLayoutParams(new LinearLayout.LayoutParams(
                                0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                        // Time Value
                        TextView timeView = new TextView(requireContext());
                        timeView.setText(time);
                        timeView.setTextSize(16);
                        timeView.setTextColor(Color.BLACK);

                        // Add views
                        row.addView(deleteIcon);
                        row.addView(label);
                        row.addView(timeView);

                        reminderTimeList.addView(row);
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        builder.setView(dialogView);

        return builder.create();
    }

    private void setupWeekdayPickers(View dialogView) {
        int[] dayIds = {
                R.id.dayMon, R.id.dayTue, R.id.dayWed,
                R.id.dayThu, R.id.dayFri, R.id.daySat, R.id.daySun
        };

        for (int id : dayIds) {
            TextView dayView = dialogView.findViewById(id);
            dayView.setSelected(true);
            dayView.setBackgroundResource(R.drawable.bg_weekday_selected);
            dayView.setTextColor(getResources().getColor(android.R.color.white));

            dayView.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
                v.setBackgroundResource(v.isSelected() ?
                        R.drawable.bg_weekday_selected :
                        R.drawable.bg_weekday_unselected);
                ((TextView) v).setTextColor(getResources().getColor(
                        v.isSelected() ? android.R.color.white : android.R.color.black));
            });
        }
    }

    private void showIconBottomSheet(ImageView iconPreviewImage, FrameLayout iconPreviewContainer) {
        BottomSheetDialog iconDialog = new BottomSheetDialog(requireContext());
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_choose_icon, null);
        iconDialog.setContentView(sheetView);

        RecyclerView recyclerView = sheetView.findViewById(R.id.iconRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));

        IconGridAdapter adapter = new IconGridAdapter(IconData.ICON_RES_IDS, iconResId -> {
            selectedIconResId = iconResId;
            iconPreviewImage.setImageResource(iconResId);
            iconPreviewContainer.setVisibility(View.VISIBLE);
            iconDialog.dismiss();
        });

        recyclerView.setAdapter(adapter);

        iconDialog.show();
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) return;

        if (requestCode == REQUEST_PICK_IMAGE) {
            Uri sourceUri = data.getData();
            Uri destinationUri = Uri.fromFile(new File(requireContext().getCacheDir(), "cropped.png"));

            UCrop.Options options = new UCrop.Options();
            options.setCircleDimmedLayer(false);
            options.setShowCropGrid(false);
            options.setHideBottomControls(true);
            options.setCompressionFormat(Bitmap.CompressFormat.PNG);

            UCrop.of(sourceUri, destinationUri)
                    .withAspectRatio(1, 1)
                    .withMaxResultSize(512, 512)
                    .withOptions(options)
                    .start(requireActivity(), this, REQUEST_CROP_IMAGE);
        } else if (requestCode == REQUEST_CROP_IMAGE) {
            croppedUri = UCrop.getOutput(data);
            if (croppedUri != null) {
                iconPreviewImage.setImageURI(croppedUri);
                iconPreviewContainer.setVisibility(View.VISIBLE);
                selectedIconResId = -1;
            }
        }
    }
}