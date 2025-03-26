package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MarketplaceAdapter extends RecyclerView.Adapter<MarketplaceAdapter.ViewHolder> {
    private Context context;
    private List<MarketplaceItem> marketplaceItems;

    private OnBuyClickListener onBuyClickListener;

    public interface OnBuyClickListener {
        void onBuy(MarketplaceItem item);
    }

    public MarketplaceAdapter(Context context, List<MarketplaceItem> marketplaceItems, OnBuyClickListener onBuyClickListener)    {
        this.context = context;
        this.marketplaceItems = marketplaceItems;
        this.onBuyClickListener = onBuyClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.marketplace_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MarketplaceItem item = marketplaceItems.get(position);
        holder.itemImage.setImageResource(item.getImageId(context));
        holder.itemName.setText(item.getName());
        holder.itemPrice.setText("Price: " + item.getPrice() + " coins");

        holder.buyButton.setOnClickListener(v -> {
            if ( onBuyClickListener != null) {
                onBuyClickListener.onBuy(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return marketplaceItems.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        TextView itemName, itemPrice;
        Button buyButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage = itemView.findViewById(R.id.inventoryItemIcon);
            itemName = itemView.findViewById(R.id.inventoryItemName);
            itemPrice = itemView.findViewById(R.id.itemPrice);
            buyButton = itemView.findViewById(R.id.buyButton);
        }
    }
}

