package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InventoryCategoryAdapter extends RecyclerView.Adapter<InventoryCategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<String> categories;
    private Map<String, List<MarketplaceItem>> categoryItemMap;
    private Map<String, Boolean> expandedMap = new HashMap<>();

    public InventoryCategoryAdapter(Context context, Map<String, List<MarketplaceItem>> originalCategoryItemMap) {
        this.context = context;
        this.categoryItemMap = new HashMap<>();
        this.categories = Arrays.asList("plant", "tree", "decor", "furniture");

        for (String category : categories) {
            List<MarketplaceItem> items = originalCategoryItemMap.getOrDefault(category, new ArrayList<>());
            this.categoryItemMap.put(category, items);
            expandedMap.put(category, false);
        }
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_inventory_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        String category = categories.get(position);
        holder.categoryTitle.setText(category);

        int iconRes = R.drawable.default_image;
        if (category.equalsIgnoreCase("plant")) iconRes = R.drawable.fern;
        else if (category.equalsIgnoreCase("decor")) iconRes = R.drawable.zen_stones;
        else if (category.equalsIgnoreCase("tree")) iconRes = R.drawable.orange_tree;
        else if (category.equalsIgnoreCase("furniture")) iconRes = R.drawable.bridge;

        holder.categoryIcon.setImageResource(iconRes);

        List<MarketplaceItem> items = categoryItemMap.get(category);
        boolean isExpanded = expandedMap.get(category);

        if (items != null && !items.isEmpty()) {
            InventoryItemAdapter itemAdapter = new InventoryItemAdapter(context, items);
            holder.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.itemsRecyclerView.setAdapter(itemAdapter);
            holder.itemsRecyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            holder.noItemsMessage.setVisibility(View.GONE);
        } else {
            holder.itemsRecyclerView.setVisibility(View.GONE);
            holder.noItemsMessage.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            expandedMap.put(category, !isExpanded);
            notifyItemChanged(position);
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }


    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        ImageView categoryIcon;
        RecyclerView itemsRecyclerView;
        TextView noItemsMessage;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            categoryIcon = itemView.findViewById(R.id.categoryIcon);
            itemsRecyclerView = itemView.findViewById(R.id.itemsRecyclerView);
            noItemsMessage = itemView.findViewById(R.id.noItemsMessage);
        }
    }
}
