package edu.northeastern.finalproject_group_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class SettingOptionAdapter extends RecyclerView.Adapter<SettingOptionAdapter.ViewHolder> {

    private List<SettingOption> options;
    private OnOptionClickListener listener;

    public interface OnOptionClickListener {
        void onOptionClick(SettingOption option);
    }

    public SettingOptionAdapter(List<SettingOption> options, OnOptionClickListener listener) {
        this.options = options;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon, arrow;
        TextView title;

        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.optionIcon);
            arrow = itemView.findViewById(R.id.optionArrow);
            title = itemView.findViewById(R.id.optionTitle);
        }

        public void bind(SettingOption option, OnOptionClickListener listener) {
            icon.setImageResource(option.getIconResId());
            title.setText(option.getTitle());
            itemView.setOnClickListener(v -> listener.onOptionClick(option));
        }
    }

    @NonNull
    @Override
    public SettingOptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_settings_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingOptionAdapter.ViewHolder holder, int position) {
        holder.bind(options.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return options.size();
    }
}