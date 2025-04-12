package edu.northeastern.finalproject_group_1;

import static java.util.Objects.isNull;

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

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HabitListFragment extends Fragment implements HabitAdapter.OnHabitCheckListener{
    private final String TAG = "HabitListFragment";

    private RecyclerView habitRecyclerView;
    private HabitAdapter habitAdapter;
    private List<Habit> habitList;
    private String currentUser;
    private FirebaseDatabase db;
    private int bank;
    private GardenFragment gardenFragment;

    // Factory method to pass in username to habit list fragment so it can use it to search db
    public static HabitListFragment newInstance(String user, GardenFragment gardenFragment) {
        //Log.d("HabitListFragment", "Username passed in: " + user);
        HabitListFragment fragment = new HabitListFragment();
        Bundle args = new Bundle();
        args.putString("USERNAME", user);
        fragment.setArguments(args);
        fragment.setGardenFragment(gardenFragment);
        return fragment;
    }


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
        if (getArguments() != null) {
            currentUser = getArguments().getString("USERNAME", "");
        }
        return inflater.inflate(R.layout.fragment_habit_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        habitRecyclerView = view.findViewById(R.id.habitRecyclerView);
        habitRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        Log.d(TAG, gardenFragment.toString());
//        gardenFragment = view.findViewById(R.id.gardenView);
//        if (isNull(gardenView)) {
//            Log.d(TAG, "Garden view is null from habit list fragment");
//        }

        //create empty habit list in case user has no habits
        habitList = new ArrayList<>();
        habitAdapter = new HabitAdapter(habitList, this);
        habitRecyclerView.setAdapter(habitAdapter);

        //Log.d("HabitListFragment", "Fragment has username to find: " + this.currentUser);
        db = FirebaseDatabase.getInstance();
        fetchHabits(currentUser);

        // Fake Data
//        habitList = new ArrayList<>();
//        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false,
//                R.drawable.baseline_water_drop_24, "Daily", 10));
//        habitList.add(new Habit("Exercise", "30 minutes workout", false,
//                R.drawable.baseline_fitness_center_24, "Daily", 20));

//        habitAdapter = new HabitAdapter(habitList, this);
//        habitRecyclerView.setAdapter(habitAdapter);

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

    /**
     * Helper method to get list of habits for user from db and add them to the habitList array
     */
    private void fetchHabits(String user) {
        //get habits from db
        DatabaseReference dbHABITS = db.getReference("HABITS");
        //get habits just for current user
        DatabaseReference myHabits = dbHABITS.child(user);
        /*myHabits.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                habitList = new ArrayList<>();
                Habit habit = snapshot.getValue(Habit.class);
                habit.setHabitKey(snapshot.getKey());
                Log.d(TAG, habit.getHabitKey());
                Log.d(TAG, habit.toString());
                habitList.add(habit);
                updateHabitList(habitList);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading habits from db: " + error);
            }
        });*/
        myHabits.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                habitList = new ArrayList<>();
                for (DataSnapshot h: snapshot.getChildren()) {
                    Habit habit = h.getValue(Habit.class);
                    habit.setHabitKey(h.getKey());
                    Log.d(TAG, "Habit key: " + habit.getHabitKey());
                    //Log.d(TAG, habit.toString());
                    habitList.add(habit);
                }
                updateHabitList(habitList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading habits from db: " + error);
            }
        });


    }

    public void updateHabitList(List<Habit> habitList) {
        if (habitList == null) {
            this.habitList = new ArrayList<>();
        } else {
            this.habitList = habitList;
        }
        //Log.d(TAG, String.valueOf(this.habitList.size()));
        habitAdapter = new HabitAdapter(habitList, this);
        habitRecyclerView.setAdapter(habitAdapter);
    }


    @Override
    public void onHabitCheckChanged(int fromPos, boolean isChecked) {
        Habit habit = habitList.get(fromPos);
        habit.setCompleted(isChecked);
        //TODO: calling this updates the db value, BUT it breaks the sorting  - need to adjust something here
        editHabit(habit);
        int reward = habit.getReward();
        //check if the habit has already been completed today, and award reward accordingly
        LocalDate t = LocalDate.now();
        Calendar c = Calendar.getInstance();
        boolean completedToday = false;
        if (habit.getLastCompletedMillis() != -1) { //-1 is case where habit hasn't been completed before
            c.setTimeInMillis(habit.getLastCompletedMillis());
            LocalDate l = LocalDate.of(c.get(c.YEAR), c.get(c.MONTH), c.get(c.DATE));
            completedToday = (t == l);
        }
        if (!completedToday) {
            //give user rewards
            rewardUser(reward);
            //trigger plant growth
            gardenFragment.growPlantByHabit(habit.getHabitKey());
        }
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

    private void rewardUser(int reward) {
        DatabaseReference user = db.getReference().child("GARDENDATA").child(currentUser).child("bank");
        ValueEventListener bankListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bank = snapshot.getValue(int.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        user.addValueEventListener(bankListener);
        user.setValue(bank + reward);
        Toast.makeText(getActivity().getApplicationContext(), "Added " + reward + "to your bank!", Toast.LENGTH_LONG).show();
        user.removeEventListener(bankListener);
    }

    public void addHabit(Habit newHabit) {
        //connect to db to add the habit:
        DatabaseReference dbHabits = db.getReference("HABITS");
        String key = dbHabits.child(currentUser).push().getKey();
        dbHabits.child(currentUser).child(key).setValue(newHabit);
        gardenFragment.addNewPlant(key);

        //wondering if these are needed since this will trigger db listener?
//        int insertPos = 0;
//        while (insertPos < habitList.size() && !habitList.get(insertPos).isCompleted()) {
//            insertPos++;
//        }
//        habitList.add(insertPos, newHabit);
//        habitAdapter.notifyItemInserted(insertPos);
    }

    public void editHabit(Habit updatedHabit) {
        if (!isNull(updatedHabit.getHabitKey())) {
            DatabaseReference dbHabits = db.getReference("HABITS");
            dbHabits.child(currentUser).child(updatedHabit.getHabitKey()).setValue(updatedHabit);
        } else {
            Log.d(TAG, "Trying to update a habit without a key");
        }
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentUser", currentUser);
    }

    private void setGardenFragment(GardenFragment garden) {
        this.gardenFragment = garden;
    }

}
