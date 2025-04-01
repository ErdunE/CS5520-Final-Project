package edu.northeastern.finalproject_group_1;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HabitListFragment extends Fragment implements HabitAdapter.OnHabitCheckListener{

    private RecyclerView habitRecyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;

    public List<Habit> getHabitList() {
        return habitList;
    }

    public HabitAdapter getHabitAdapter() {
        return habitAdapter;
    }

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

        // Fake Data
        habitList = new ArrayList<>();
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false,
                R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false,
                R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false,
                R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false,
                R.drawable.baseline_fitness_center_24, "Daily", 20));
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false,
                R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false,
                R.drawable.baseline_fitness_center_24, "Daily", 20));

        habitAdapter = new HabitAdapter(habitList, this);
        habitRecyclerView.setAdapter(habitAdapter);

        // Long press Helper
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.LEFT
        ) {
            private final ColorDrawable background = new ColorDrawable(0xFFFF5555);
            private Drawable deleteIcon;
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                Habit fromHabit = habitList.get(fromPos);
                habitList.remove(fromPos);
                habitList.add(toPos, fromHabit);

                habitAdapter.notifyItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT) {
                    int position = viewHolder.getAdapterPosition();
                    showDeleteConfirmationDialog(position);
                }
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c,
                                    @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY,
                                    int actionState,
                                    boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float translationX = dX;

                    if (translationX < 0) {
                        background.setBounds(
                                (int) (itemView.getRight() + translationX),
                                itemView.getTop(),
                                itemView.getRight(),
                                itemView.getBottom()
                        );
                        background.draw(c);
                    }

                    float absDx = Math.abs(translationX);
                    int threshold = 150;

                    if (deleteIcon == null) {
                        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete);
                    }

                    if (deleteIcon != null && translationX < 0 && absDx > threshold) {
                        int iconSizePx = (int) TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP,
                                32,
                                getResources().getDisplayMetrics()
                        );

                        int itemHeight = itemView.getHeight();
                        int iconMargin = (itemHeight - iconSizePx) / 2;

                        int iconLeft = itemView.getRight() - iconMargin - iconSizePx - 20;
                        int iconRight = itemView.getRight() - iconMargin - 20;
                        int iconTop = itemView.getTop() + (itemHeight - iconSizePx) / 2;
                        int iconBottom = iconTop + iconSizePx;

                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                        deleteIcon.draw(c);
                    }
                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(habitRecyclerView);
    }

    @Override
    public void onHabitCheckChanged(int fromPos, boolean isChecked) {
        Habit habit = habitList.get(fromPos);
        habit.setCompleted(isChecked);

        habitList.remove(fromPos);

        int toPos;

        if (isChecked) {
            habitList.add(habit);
            toPos = habitList.size() - 1;
        } else {
            int insertPos = 0;
            while (insertPos < habitList.size() && !habitList.get(insertPos).isCompleted()) {
                insertPos++;
            }
            habitList.add(insertPos, habit);
            toPos = insertPos;
        }

        habitAdapter.notifyItemMoved(fromPos, toPos);
        habitAdapter.notifyItemChanged(toPos);
    }

    public void addHabit(Habit newHabit) {
        habitList.add(newHabit);
        habitAdapter.notifyItemInserted(habitList.size() - 1);
    }

    public void updateHabit(int position, String newTitle, String newDescription) {
        if (position >= 0 && position < habitList.size()) {
            Habit habit = habitList.get(position);
            habit.setTitle(newTitle);
            habit.setDescription(newDescription);
            habitAdapter.notifyItemChanged(position);
        }
    }

    private void showDeleteConfirmationDialog(int position) {
        Habit habit = habitList.get(position);

        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Habit")
                .setMessage("Are you sure you want to delete '" + habit.getTitle() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    habitList.remove(position);
                    habitAdapter.notifyItemRemoved(position);
                    ((DashboardActivity) requireActivity()).deleteHabitReminder(habit);
                    showUndoSnackbar(habit, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    habitAdapter.notifyItemChanged(position);
                })
                .setCancelable(false)
                .show();
    }

    private void showUndoSnackbar(Habit deletedHabit, int deletedPosition) {
        View rootView = getView();
        if (rootView == null) return;

        Snackbar snackbar = Snackbar.make(rootView,
                "Habit deleted: " + deletedHabit.getTitle(),
                Snackbar.LENGTH_LONG);

        snackbar.setAction("Undo", v -> {
            habitList.add(deletedPosition, deletedHabit);
            habitAdapter.notifyItemInserted(deletedPosition);
        });
        snackbar.show();
    }
}
