package edu.northeastern.finalproject_group_1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class MarketplaceActivity extends AppCompatActivity {

//    private GridView gridView;
    private RecyclerView recyclerView;
    private MarketplaceAdapter adapter;
    private List<MarketplaceItem> allMarketplaceItems;
    private List<MarketplaceItem> dailyMarketplaceItems;
    private static final int DAILY_ITEM_COUNT = 4;
    private DatabaseReference inventoryReference;
    private DatabaseReference goldReference;
    private DatabaseReference marketReference;
    private FirebaseDatabase fbDatabase;
    private int userGold = 0;

    private String userId = "testUser1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_marketplace);

        recyclerView = findViewById(R.id.recyclerView);
        updateGridSpan();

        //get data from database
        goldReference = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(userId).child("bank");
        inventoryReference = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(userId).child("inventory");
        marketReference = FirebaseDatabase.getInstance().getReference("MARKET");

        allMarketplaceItems = new ArrayList<>();
        dailyMarketplaceItems = new ArrayList<>();

        //fetch the gold the user has
        loadUserGold();
        loadMarketItems();

    }

    private void updateGridSpan() {
        int spanCount = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? 4 : 2;
        recyclerView.setLayoutManager(new GridLayoutManager(this, spanCount));
    }

    private void loadUserGold() {
        goldReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    userGold = snapshot.getValue(Integer.class);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MarketplaceActivity.this, "Failed to load gold balance", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMarketItems() {
        marketReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allMarketplaceItems.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    MarketplaceItem item = itemSnapshot.getValue(MarketplaceItem.class);

                    Log.d("MarketplaceActivity", "Firebase item: " + item.getName() + " - imageId in DB: " + itemSnapshot.child("imageId").getValue());

                    if (itemSnapshot.child("imageId").exists()) {
                        Object imageIdObj = itemSnapshot.child("imageId").getValue();
                        if (imageIdObj instanceof Long) {
                            item.imageId = ((Long) imageIdObj).intValue();
                        } else if (imageIdObj instanceof Integer) {
                            item.imageId = (Integer) imageIdObj;
                        }
                    }

                    allMarketplaceItems.add(item);
                }
                selectDailyItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MarketplaceActivity.this, "Failed to load market items", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void selectDailyItems() {
        Collections.shuffle(allMarketplaceItems, new Random());
        dailyMarketplaceItems.clear();
        for (int i = 0; i < DAILY_ITEM_COUNT && i < allMarketplaceItems.size(); i++) {
            dailyMarketplaceItems.add(allMarketplaceItems.get(i));
        }
        adapter = new MarketplaceAdapter(this, dailyMarketplaceItems);
        recyclerView.setAdapter(adapter);
    }

    //buy an item from the store
    private void buyItem(MarketplaceItem item) {
        if (userGold >= item.getPrice()) {
            String itemId = inventoryReference.push().getKey();
            if (itemId != null) {
                inventoryReference.child(itemId).setValue(item);
                userGold -= item.getPrice();
                goldReference.setValue(userGold); // Update gold balance in Firebase
                Toast.makeText(this, "Purchased: " + item.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error purchasing item!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not enough gold!", Toast.LENGTH_SHORT).show();
        }
    }
}