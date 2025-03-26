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
    public interface OnHabitCheckListener {
        void onHabitCheckChanged(int position, boolean isChecked);
    }
    private List<Habit> habitList;
    private OnHabitCheckListener checkListener;

    public HabitAdapter(List<Habit> habitList, OnHabitCheckListener checkListener) {
        this.habitList = habitList;
        this.checkListener = checkListener;
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

        holder.title.setText(habit.getTitle());
        holder.description.setText(habit.getDescription());
        holder.icon.setImageResource(habit.getIcon());
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(habit.isCompleted());

        updateHabitUI(holder, habit);

        // checkbox logic
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (checkListener != null) {
                int currentPosition = holder.getAdapterPosition();
                checkListener.onHabitCheckChanged(currentPosition, isChecked);
            }
        });

        // Edit
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            showHabitDialog(v.getContext(), habit, pos);
        });

        // Long press
        holder.itemView.setOnLongClickListener(v -> {
            int pos = holder.getAdapterPosition();
            Toast.makeText(v.getContext(), "Long press on item " + pos, Toast.LENGTH_SHORT).show();
            return true;
        });
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

    private void showHabitDialog(Context context, Habit habit, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Habit Options")
                .setMessage("What do you want to do with '" + habit.getTitle() + "'?")
                .setPositiveButton("Edit", (dialog, which) -> {
                    ((DashboardActivity) context).showEditHabitDialog(habit, position);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setCancelable(true)
                .show();
    }

    private void updateHabitUI(HabitViewHolder holder, Habit habit) {
        if (habit.isCompleted()) {
            holder.title.setTextColor(Color.GRAY);
            holder.description.setTextColor(Color.GRAY);
            holder.icon.setColorFilter(Color.GRAY);
        } else {
            holder.title.setTextColor(Color.BLACK);
            holder.description.setTextColor(Color.BLACK);
            holder.icon.setColorFilter(null);
        }
    }
}