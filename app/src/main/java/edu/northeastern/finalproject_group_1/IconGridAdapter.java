package edu.northeastern.finalproject_group_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IconGridAdapter extends RecyclerView.Adapter<IconGridAdapter.IconViewHolder> {

    private String[] iconNames;
    private OnIconClickListener listener;

    public interface OnIconClickListener {
        void onIconClicked(String iconName);
    }

    public IconGridAdapter(String[] iconNames, OnIconClickListener listener) {
        this.iconNames = iconNames;
        this.listener = listener;
    }


    @NonNull
    @Override
    public IconViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon, parent, false);
        return new IconViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IconViewHolder holder, int position) {
        String iconName = iconNames[position];
        int resId = holder.itemView.getContext().getResources().getIdentifier(
                iconName, "drawable", holder.itemView.getContext().getPackageName());
        holder.iconImageView.setImageResource(resId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIconClicked(iconName);
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconNames.length;
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
}