package edu.northeastern.finalproject_group_1;

import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private String userId = "testUser1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inventory);

        recyclerView = findViewById(R.id.recyclerViewInventory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        inventoryRef = FirebaseDatabase.getInstance().getReference("GARDENDATA").child(userId).child("inventory");
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