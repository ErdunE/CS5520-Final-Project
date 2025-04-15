package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
    private String userId;
    private TextView goldBalance;
    BottomNavigationView bottomNavigationView;
    FloatingActionButton gardenButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_marketplace);

        Intent parentIntent = getIntent();
        if (parentIntent.hasExtra("USERNAME")) {
            userId = parentIntent.getStringExtra("USERNAME");
        }

        recyclerView = findViewById(R.id.recyclerViewInventory);
        goldBalance = findViewById(R.id.goldBalanceTV);
        gardenButton = findViewById(R.id.gardenButton);

        // Setup garden FAB
        gardenButton.setOnClickListener(v -> {
                    Intent intent = new Intent(MarketplaceActivity.this, DashboardActivity.class);
                    intent.putExtra("USERNAME", userId);
                    startActivity(intent);
        });
        updateGridSpan();

        // nav menu
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Inventory) {
                    startActivity(new Intent(MarketplaceActivity.this, InventoryActivity.class));
                    return true;
                } else if (itemId == R.id.Challenges) {
                    startActivity(new Intent(MarketplaceActivity.this, ChallengesActivity.class));
                    return true;
                } else if (itemId == R.id.Stats) {
                    startActivity(new Intent(MarketplaceActivity.this, StatsActivity.class));
                    return true;
                } else if (itemId == R.id.Settings) {
                    startActivity(new Intent(MarketplaceActivity.this, SettingsActivity.class));
                    return true;
                }
                return false;
            }
        });

        findViewById(R.id.fragment_garden).setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);

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

    private void showMarketplace(boolean show) {
        recyclerView.setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.fragment_garden).setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private void loadUserGold() {
        goldReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Object goldValue = snapshot.getValue();
                if (goldValue instanceof Long) {
                    userGold = ((Long) goldValue).intValue();
                } else if (goldValue instanceof String) {
                    try {
                        userGold = Integer.parseInt((String) goldValue);
                    } catch (NumberFormatException e) {
                        userGold = 0;
                    }
                }
                goldBalance.setText(String.valueOf(userGold));
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

        boolean rareAdded = false;
        for (MarketplaceItem item : allMarketplaceItems) {
            if (dailyMarketplaceItems.size() >= DAILY_ITEM_COUNT) break;

            if (item.isRare() && !rareAdded) {
                dailyMarketplaceItems.add(item);
                rareAdded = true;
            } else if (!item.isRare()) {
                dailyMarketplaceItems.add(item);
            }
        }

        adapter = new MarketplaceAdapter(this, dailyMarketplaceItems, this::buyItem);
        recyclerView.setAdapter(adapter);
    }

    //buy an item from the store
    private void buyItem(MarketplaceItem item) {
        if (userGold >= item.getPrice()) {
            String itemId = inventoryReference.push().getKey();
            if (itemId != null) {

                Map<String, Object> itemData = new HashMap<>();
                itemData.put("name", item.getName());
                itemData.put("imageId", item.imageId);
                itemData.put("price", item.getPrice());
                itemData.put("category", item.getCategory());
                itemData.put("description", item.getDescription());
                itemData.put("rare", item.isRare());
                itemData.put("level", 0);
                itemData.put("levelProgress", 0);
                itemData.put("linkedHabit", 0);

                inventoryReference.child(itemId).setValue(itemData);
                userGold -= item.getPrice();
                goldReference.setValue(userGold);
                goldBalance.setText(String.valueOf(userGold));
                Toast.makeText(this, "Purchased: " + item.getName(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error purchasing item!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Not enough gold!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            showMarketplace(true);
        } else {
            super.onBackPressed();
        }
    }
}