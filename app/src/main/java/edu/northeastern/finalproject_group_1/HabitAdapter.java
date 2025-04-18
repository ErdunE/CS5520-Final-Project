package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
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

        Log.d("HabitAdapter", "Binding habit at position " + position + ": " + habit.getTitle());
        Log.d("HabitAdapter", "→ iconResId = " + habit.getIconResId());
        Log.d("HabitAdapter", "→ customIconUri = " + habit.getCustomIconUri());
        Log.d("HabitAdapter", "→ color = " + habit.getCustomColor());

        try {
            String iconName = holder.itemView.getContext().getResources()
                    .getResourceEntryName(habit.getIconResId());
            Log.d("HabitAdapter", "→ icon name = " + iconName);
        } catch (Exception e) {
            Log.e("HabitAdapter", "iconResId invalid: " + habit.getIconResId(), e);
        }

        if (habit.getCustomIconUri() != null) {
            holder.icon.setImageURI(Uri.parse(habit.getCustomIconUri()));
        } else {
            holder.icon.setImageResource(habit.getIconResId());
        }

        holder.icon.setColorFilter(habit.getCustomColor());

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

            if (habit.getCustomColor() != 0) {
                holder.icon.setColorFilter(habit.getCustomColor());
            } else {
                holder.icon.clearColorFilter();
            }
        }
    }
}