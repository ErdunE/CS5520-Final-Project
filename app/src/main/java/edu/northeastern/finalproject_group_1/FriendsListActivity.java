package edu.northeastern.finalproject_group_1;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FriendsListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FriendsListAdapter adapter;
    private List<String> friendUsernames = new ArrayList<>();
    private String currentUsername;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_list);

        Toolbar toolbar = findViewById(R.id.friendsToolbar);
        toolbar.setTitle("Friends List");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle("Friends List");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        recyclerView = findViewById(R.id.recyclerViewFriends);
        emptyView = findViewById(R.id.emptyView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendsListAdapter(friendUsernames);
        recyclerView.setAdapter(adapter);

        currentUsername = getSharedPreferences("HabitendPrefs", MODE_PRIVATE)
                .getString("logged_in_username", null);

        if (currentUsername != null) {
            fetchFriendsFromFirebase(currentUsername);
        }

        Button backButton = findViewById(R.id.backButton);
        Button addFriendButton = findViewById(R.id.addFriendButton);

        backButton.setOnClickListener(v -> onBackPressed());

        addFriendButton.setOnClickListener(v -> {
            AddFriendDialog dialog = new AddFriendDialog(currentUsername, () -> {
                fetchFriendsFromFirebase(currentUsername);
            });
            dialog.show(getSupportFragmentManager(), "AddFriendDialog");
        });
    }

    private void fetchFriendsFromFirebase(String username) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("FRIENDSLIST").child(username);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                friendUsernames.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    friendUsernames.add(child.getKey());
                }
                adapter.notifyDataSetChanged();

                emptyView.setVisibility(friendUsernames.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FriendsListActivity", "Failed to read friends", error.toException());
            }
        });
    }
}