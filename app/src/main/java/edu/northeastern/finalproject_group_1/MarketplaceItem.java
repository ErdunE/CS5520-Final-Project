package edu.northeastern.finalproject_group_1;

import android.content.Context;
import android.util.Log;

public class MarketplaceItem {
    private String name;
    int imageId;
    private int price;
    private String category;
    private String description;
    private boolean isRare;

    public MarketplaceItem() {}


    public MarketplaceItem(String name, int imageId, int price, String category, String description, boolean isRare) {
        this.name = name;
        this.imageId= imageId;
        this.price = price;
        this.category = category;
        this.description = description;
        this.isRare = isRare;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory(){
        return category;
    }

    public String getDescription(){
        return description;
    }

    public Boolean isRare(){
        return isRare;
    }

    // Map imageId to actual drawable resource
    public int getImageId(Context context) {
        int[] imageResources = {
                R.drawable.apple_tree, // 1
                R.drawable.bonsai, // 2
                R.drawable.succulent, // 3
                R.drawable.fern, // 4
                R.drawable.zen_stones  // 5
        };

        Log.d("MarketplaceItem", "Image ID received: " + imageId);

        if (imageId < 1 || imageId > imageResources.length) {
            Log.e("MarketplaceItem", "Invalid imageId: " + imageId + ", using default image.");
            return R.drawable.default_image;
        }
        Log.d("MarketplaceItem", "Mapped to drawable: " + imageResources[imageId - 1]);
        return imageResources[imageId-1];
    }
}
