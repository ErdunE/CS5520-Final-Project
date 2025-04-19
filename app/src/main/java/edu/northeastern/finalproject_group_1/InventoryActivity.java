package edu.northeastern.finalproject_group_1;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class InventoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private InventoryCategoryAdapter adapter;
    private List<MarketplaceItem> inventoryItems;
    private DatabaseReference inventoryRef;
    private String currentUser;
    private BottomNavigationView bottomNavigationView;
    private FloatingActionButton gardenButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        if (savedInstanceState != null) {
            this.currentUser = savedInstanceState.getString("currentUser");
        } else {
            //Storing current user from parent activity
            Intent parentIntent = getIntent();
            if (parentIntent.hasExtra("USERNAME")) {
                currentUser = parentIntent.getStringExtra("USERNAME");
            }
        }
        recyclerView = findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        gardenButton = findViewById(R.id.gardenButton);

        // Setup garden FAB
        gardenButton.setOnClickListener(v -> {
            Intent intent = new Intent(InventoryActivity.this, DashboardActivity.class);
            intent.putExtra("USERNAME", currentUser);
            startActivity(intent);
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.Shop) {
                    Intent intent = new Intent(InventoryActivity.this, MarketplaceActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Challenges) {
                    Intent intent = new Intent(InventoryActivity.this, ChallengesActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Stats) {
                    Intent intent = new Intent(InventoryActivity.this, StatsActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                } else if (itemId == R.id.Settings) {
                    Intent intent = new Intent(InventoryActivity.this, SettingsActivity.class);
                    intent.putExtra("USERNAME", currentUser);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });


        inventoryRef = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(currentUser).child("inventory");
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, List<MarketplaceItem>> categoryMap = new HashMap<>();

                for (DataSnapshot itemSnap : snapshot.getChildren()) {
                    MarketplaceItem item = itemSnap.getValue(MarketplaceItem.class);
                    if (item != null) {
                        String category = item.getCategory();
                        if (!categoryMap.containsKey(category)) {
                            categoryMap.put(category, new ArrayList<>());
                        }
                        categoryMap.get(category).add(item);
                    }
                }

                InventoryCategoryAdapter adapter = new InventoryCategoryAdapter(InventoryActivity.this, categoryMap);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
            }
        });
        loadInventory();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("currentUser", this.currentUser);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.currentUser = savedInstanceState.getString("currentUser");
    }

    private void loadInventory() {
        inventoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, List<MarketplaceItem>> categorizedItems = new HashMap<>();

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
                    if (!categorizedItems.containsKey(item.getCategory())) {
                        categorizedItems.put(item.getCategory(), new ArrayList<>());
                    }
                    categorizedItems.get(item.getCategory()).add(item);
                }

                adapter = new InventoryCategoryAdapter(InventoryActivity.this, categorizedItems);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InventoryActivity.this, "Failed to load inventory", Toast.LENGTH_SHORT).show();
            }
        });
    }

}