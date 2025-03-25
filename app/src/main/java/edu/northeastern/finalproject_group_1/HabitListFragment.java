package edu.northeastern.finalproject_group_1;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
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

        // Fake Data
        habitList = new ArrayList<>();
        habitList.add(new Habit("Drink Water", "Drink 8 glasses of water", false,
                R.drawable.baseline_water_drop_24, "Daily", 10));
        habitList.add(new Habit("Exercise", "30 minutes workout", false,
                R.drawable.baseline_fitness_center_24, "Daily", 20));

        habitAdapter = new HabitAdapter(habitList);
        habitRecyclerView.setAdapter(habitAdapter);

        // Long press Helper
        ItemTouchHelper.Callback callback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                0
        ) {
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
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return true;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(habitRecyclerView);
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


}
