package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class InventoryItemAdapter extends RecyclerView.Adapter<InventoryItemAdapter.ItemViewHolder> {
    private Context context;
    private List<MarketplaceItem> items;

    public InventoryItemAdapter(Context context, List<MarketplaceItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public InventoryItemAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inventory_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryItemAdapter.ItemViewHolder holder, int position) {
        MarketplaceItem item = items.get(position);
        holder.itemName.setText(item.getName());
        holder.itemIcon.setImageResource(item.getImageId(context));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemName;
        ImageView itemIcon;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.inventoryItemName);
            itemIcon = itemView.findViewById(R.id.inventoryItemIcon);
        }
    }
}
