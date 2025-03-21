package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.HabitViewHolder> {
    private List<Habit> habitList;

    public HabitAdapter(List<Habit> habitList) {
        this.habitList = habitList;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = habitList.get(position);

        // Set title
        holder.title.setText(habit.getTitle());
        // Set description
        holder.description.setText(habit.getDescription());
        // Set icon
        holder.icon.setImageResource(habit.getIcon());
        // set checkbox status
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(habit.isCompleted());

        updateHabitUI(holder, habit);

        // checkbox logic
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            habit.setCompleted(isChecked);
            notifyItemChanged(position);
        });

        holder.itemView.setOnClickListener(v -> showHabitDialog(v.getContext(), habit));
    }

    @Override
    public int getItemCount() {
        return habitList.size();
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView icon;
        CheckBox checkBox;

        public HabitViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.habit_title);
            description = itemView.findViewById(R.id.habit_description);
            icon = itemView.findViewById(R.id.habit_icon);
            checkBox = itemView.findViewById(R.id.checkbox_complete);
        }
    }

    private void showHabitDialog(Context context, Habit habit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Habit Options")
                .setMessage("What do you want to do with '" + habit.getTitle() + "'?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    // TODO: Edit Habit Detail
                    Toast.makeText(context, "Edit feature coming soon!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void updateHabitUI(HabitViewHolder holder, Habit habit) {
        if (habit.isCompleted()) {
            holder.title.setTextColor(Color.GRAY);
            holder.description.setTextColor(Color.GRAY);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.description.setTextColor(Color.BLACK);
        }
    }
}