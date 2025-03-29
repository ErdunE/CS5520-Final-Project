package edu.northeastern.finalproject_group_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IconGridAdapter extends RecyclerView.Adapter<IconGridAdapter.IconViewHolder> {

    private int[] iconResIds;
    private OnIconClickListener listener;

    public interface OnIconClickListener {
        void onIconClicked(int iconResId);
    }

    public IconGridAdapter(int[] iconResIds, OnIconClickListener listener) {
        this.iconResIds = iconResIds;
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
        int iconResId = iconResIds[position];
        holder.iconImageView.setImageResource(iconResId);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onIconClicked(iconResId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return iconResIds.length;
    }

    static class IconViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;

        public IconViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
        }
    }
}