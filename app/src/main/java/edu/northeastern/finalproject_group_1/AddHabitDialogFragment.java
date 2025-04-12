package edu.northeastern.finalproject_group_1;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
    private Button btnStartDate;
    private Button btnEndDate;
    private Calendar startDate = Calendar.getInstance();
    private Calendar endDate = Calendar.getInstance();
    private String habitKey;
    private long lastCompleted = -1;
    private int reward = 50;
    private int selectedColor = Color.BLACK;
    private LinearLayout reminderTimeList;


    private final String[] repeatOptions = {"Never", "Daily", "Weekly", "Monthly", "Yearly"};

    public AddHabitDialogFragment() {}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_habit, null);

        // Retrieve edit mode info
        if (getArguments() != null) {
            isEditMode = getArguments().getBoolean("isEditMode", false);
            position = getArguments().getInt("position", -1);
            oldTitle = getArguments().getString("title", "");
            oldDescription = getArguments().getString("description", "");
            habitKey = getArguments().getString("habitKey",null);
            lastCompleted = getArguments().getLong("lastCompletedDate");

        }

        // Top controls
        ImageButton btnCancel = dialogView.findViewById(R.id.btnCancel);
        ImageButton btnSave = dialogView.findViewById(R.id.btnSave);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        EditText titleEditText = dialogView.findViewById(R.id.editHabitTitle);
        EditText descriptionEditText = dialogView.findViewById(R.id.editHabitDescription);

        // Icon + color controls
        ImageView iconCancelButton = dialogView.findViewById(R.id.iconCancelButton);
        Button btnChooseIcon = dialogView.findViewById(R.id.btnChooseIcon);
        Button btnUploadIcon = dialogView.findViewById(R.id.btnUploadIcon);
        Button btnMoreColors = dialogView.findViewById(R.id.btnMoreColors);

        // Time picker
        btnStartDate = dialogView.findViewById(R.id.btnStartDate);
        btnEndDate = dialogView.findViewById(R.id.btnEndDate);

        // Repeat + every picker
        TextView tvRepeatSelected = dialogView.findViewById(R.id.tvRepeatSelected);
        LinearLayout repeatPickerContainer = dialogView.findViewById(R.id.repeatPickerContainer);
        TextView textIntervalUnit = dialogView.findViewById(R.id.textIntervalUnit);
        FlexboxLayout weekDaySelector = dialogView.findViewById(R.id.weekDaySelector);

        TextView tvEveryValue = dialogView.findViewById(R.id.tvEveryValue);
        LinearLayout everyPickerContainer = dialogView.findViewById(R.id.everyPickerContainer);

        // Reminder
        SwitchCompat switchReminder = dialogView.findViewById(R.id.switchReminder);
        LinearLayout reminderContainer = dialogView.findViewById(R.id.reminderContainer);
        reminderTimeList = dialogView.findViewById(R.id.reminderTimeList);
        Button btnAddReminder = dialogView.findViewById(R.id.btnAddReminder);

        // Bottom buttons
        Button btnBottomCancel = dialogView.findViewById(R.id.btnBottomCancel);
        Button btnBottomSave = dialogView.findViewById(R.id.btnBottomSave);

        btnChooseIcon.setOnClickListener(v -> showIconBottomSheet(iconPreviewImage, iconPreviewContainer));
        btnUploadIcon.setOnClickListener(v -> pickImageFromGallery());

        iconPreviewContainer = dialogView.findViewById(R.id.iconPreviewContainer);
        iconPreviewImage = dialogView.findViewById(R.id.iconPreviewImage);
        iconCancelButton.setOnClickListener(v -> {
            iconPreviewImage.setImageDrawable(null);
            iconPreviewContainer.setVisibility(View.GONE);
            selectedIconResId = -1;
        });

        btnMoreColors.setOnClickListener(v -> {
            new ColorPickerDialog.Builder(requireContext())
                    .setTitle("Choose Color")
                    .setPreferenceName("MyColorPickerDialog")
                    .setPositiveButton("Confirm", (ColorEnvelopeListener) (envelope, fromUser) -> {
                        selectedColor = envelope.getColor();
                        if (iconPreviewImage.getDrawable() != null) {
                            iconPreviewImage.setColorFilter(selectedColor);
                        }
                    })
                    .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss())
                    .attachAlphaSlideBar(true)
                    .attachBrightnessSlideBar(true)
                    .show();
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
                        everyPickerContainer.setVisibility(View.VISIBLE);
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                    case "Weekly":
                        textIntervalUnit.setText("Week");
                        everyPickerContainer.setVisibility(View.VISIBLE);
                        weekDaySelector.setVisibility(View.VISIBLE);
                        weekDaySelector.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
                        weekDaySelector.requestLayout();
                        setupWeekdayPickers(dialogView);
                        break;
                    case "Monthly":
                        textIntervalUnit.setText("Month");
                        everyPickerContainer.setVisibility(View.VISIBLE);
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                    case "Yearly":
                        textIntervalUnit.setText("Year");
                        everyPickerContainer.setVisibility(View.VISIBLE);
                        weekDaySelector.setVisibility(View.GONE);
                        break;
                    default:
                        textIntervalUnit.setText("");
                        everyPickerContainer.setVisibility(View.GONE);
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

            selectedIconResId = getArguments().getInt("iconResId", -1);
            String customUriStr = getArguments().getString("customIconUri", null);
            selectedColor = getArguments().getInt("customColor", Color.BLACK);

            if (selectedIconResId != -1) {
                iconPreviewImage.setImageResource(selectedIconResId);
                iconPreviewImage.setColorFilter(selectedColor);
                iconPreviewContainer.setVisibility(View.VISIBLE);
            } else if (customUriStr != null) {
                croppedUri = Uri.parse(customUriStr);
                iconPreviewImage.setImageURI(croppedUri);
                iconPreviewContainer.setVisibility(View.VISIBLE);
            }

            // Repeat resume
            tvRepeatSelected.setText(getArguments().getString("repeatUnit", "Daily"));
            String selectedRepeat = tvRepeatSelected.getText().toString();
            switch (selectedRepeat) {
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
                    List<Integer> selectedWeekdays = getArguments().getIntegerArrayList("weekdays");
                    if (selectedWeekdays != null) {
                        int[] dayIds = {
                                R.id.dayMon, R.id.dayTue, R.id.dayWed,
                                R.id.dayThu, R.id.dayFri, R.id.daySat, R.id.daySun
                        };
                        for (int i = 0; i < dayIds.length; i++) {
                            TextView dayView = dialogView.findViewById(dayIds[i]);
                            dayView.setSelected(selectedWeekdays.contains(i + 1));
                            dayView.setBackgroundResource(dayView.isSelected() ?
                                    R.drawable.bg_weekday_selected :
                                    R.drawable.bg_weekday_unselected);
                            dayView.setTextColor(getResources().getColor(
                                    dayView.isSelected() ? android.R.color.white : android.R.color.black));
                        }
                    }
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

            // Every resume
            tvEveryValue.setText(String.valueOf(getArguments().getInt("every", 1)));

            // Date resume
            long startMillis = getArguments().getLong("startDate", -1);
            if (startMillis != -1) {
                startDate.setTimeInMillis(startMillis);
                btnStartDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(startDate.getTime()));
            }
            long endMillis = getArguments().getLong("endDate", -1);
            if (endMillis != -1) {
                endDate.setTimeInMillis(endMillis);
                btnEndDate.setText(new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(endDate.getTime()));
            }

            // reminder time resume
            ArrayList<String> reminders = getArguments().getStringArrayList("reminderTimes");
            if (reminders != null && !reminders.isEmpty()) {
                switchReminder.setChecked(true);
                reminderContainer.setVisibility(View.VISIBLE);
                for (String time : reminders) {
                    addReminderRow(time);
                }
            }
        } else {
            tvDialogTitle.setText("Add Habit");
        }

        btnBottomSave.setText(isEditMode ? "Save" : "Create");

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

        btnBottomCancel.setOnClickListener(v -> btnCancel.performClick());
        btnBottomSave.setOnClickListener(v -> btnSave.performClick());

        btnSave.setOnClickListener(v -> {
            String newTitle = titleEditText.getText().toString().trim();
            String newDescription = descriptionEditText.getText().toString().trim();
            String customUri = (selectedIconResId == -1 && croppedUri != null) ? croppedUri.toString() : null;

            if (newTitle.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
                return;
            }

            int iconToUse = (selectedIconResId != -1) ? selectedIconResId : IconData.getDefaultIcon();
            String repeatUnit = tvRepeatSelected.getText().toString();
            int every = Integer.parseInt(tvEveryValue.getText().toString());

            List<Integer> selectedWeekdays = new ArrayList<>();
            if (repeatUnit.equals("Weekly")) {
                int[] dayIds = {
                        R.id.dayMon, R.id.dayTue, R.id.dayWed,
                        R.id.dayThu, R.id.dayFri, R.id.daySat, R.id.daySun
                };
                for (int i = 0; i < dayIds.length; i++) {
                    TextView dayView = dialogView.findViewById(dayIds[i]);
                    if (dayView.isSelected()) selectedWeekdays.add(i + 1);
                }
            }

            List<String> reminderTimes = new ArrayList<>();
            if (switchReminder.isChecked()) {
                for (int i = 0; i < reminderTimeList.getChildCount(); i++) {
                    LinearLayout row = (LinearLayout) reminderTimeList.getChildAt(i);
                    TextView timeView = (TextView) row.getChildAt(2);
                    reminderTimes.add(timeView.getText().toString());
                }
            }

            Habit habit = new Habit(
                    newTitle,
                    newDescription,
                    false,
                    iconToUse,
                    repeatUnit,
                    reward,
                    customUri,
                    selectedColor,
                    repeatUnit,
                    every,
                    selectedWeekdays,
                    startDate.getTimeInMillis(),
                    endDate.getTimeInMillis(),
                    reminderTimes,
                    habitKey,
                    lastCompleted
            );

            if (isEditMode) {
                ((DashboardActivity) requireActivity()).updateHabitInList(position, habit);
            } else {
                ((DashboardActivity) requireActivity()).addHabitToList(habit);
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
                        addReminderRow(time);
                    }, hour, minute, true);

            timePickerDialog.show();
        });

        btnStartDate.setOnClickListener(v -> showDatePickerDialog(startDate, btnStartDate));
        btnEndDate.setOnClickListener(v -> showDatePickerDialog(endDate, btnEndDate));
        setupPresetColorPickers(dialogView);
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

    private void setupPresetColorPickers(View dialogView) {
        int[] colorViewIds = {
                R.id.colorRed, R.id.colorOrange, R.id.colorYellow,
                R.id.colorGreen, R.id.colorTeal, R.id.colorBlue,
                R.id.colorPurple, R.id.colorBlack
        };

        int[] colors = {
                Color.parseColor("#F44336"), Color.parseColor("#FF9800"),
                Color.parseColor("#FFEB3B"), Color.parseColor("#4CAF50"),
                Color.parseColor("#009688"), Color.parseColor("#2196F3"),
                Color.parseColor("#9C27B0"), Color.parseColor("#212121")
        };

        for (int i = 0; i < colorViewIds.length; i++) {
            View colorView = dialogView.findViewById(colorViewIds[i]);
            int color = colors[i];
            colorView.setOnClickListener(v -> {
                selectedColor = color;
                if (iconPreviewImage.getDrawable() != null) {
                    iconPreviewImage.setColorFilter(selectedColor);
                }
            });
        }
    }

    private void showDatePickerDialog(Calendar calendar, Button button) {
        new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    String formattedDate = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(calendar.getTime());
                    button.setText(formattedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void addReminderRow(String time) {
        View row = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_reminder_row, reminderTimeList, false);

        TextView timeView = row.findViewById(R.id.timeValue);
        ImageView deleteIcon = row.findViewById(R.id.iconDelete);
        ImageView editIcon = row.findViewById(R.id.iconEdit);

        timeView.setText(time);

        deleteIcon.setOnClickListener(v -> reminderTimeList.removeView(row));

        View.OnClickListener editClickListener = v -> {
            String[] parts = timeView.getText().toString().split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);

            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(),
                    (view, selectedHour, selectedMinute) -> {
                        String newTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                        timeView.setText(newTime);
                    }, hour, minute, true);

            timePickerDialog.show();
        };

        timeView.setOnClickListener(editClickListener);
        editIcon.setOnClickListener(editClickListener);

        reminderTimeList.addView(row);
    }
}