package edu.northeastern.finalproject_group_1;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddFriendDialog extends DialogFragment {

    private final String currentUsername;
    private final OnFriendAddedListener onFriendAddedListener;
    private List<String> allUsers = new ArrayList<>();
    private List<String> filteredUsers = new ArrayList<>();
    private AddFriendAdapter adapter;

    public interface OnFriendAddedListener {
        void onFriendAdded();
    }
    public AddFriendDialog(String currentUsername, OnFriendAddedListener listener) {
        this.currentUsername = currentUsername;
        this.onFriendAddedListener = listener;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_friend, null);
        EditText searchBar = view.findViewById(R.id.editSearchFriend);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewAllUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AddFriendAdapter(filteredUsers, currentUsername, this, onFriendAddedListener);
        recyclerView.setAdapter(adapter);

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadAllUsers();

        return new AlertDialog.Builder(requireContext())
                .setView(view)
                .setTitle("Add Friend")
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create();
    }


    private void loadAllUsers() {
        FirebaseDatabase.getInstance().getReference("FRIENDSLIST").child(currentUsername)
                .get().addOnSuccessListener(friendSnapshot -> {
                    List<String> existingFriends = new ArrayList<>();
                    for (DataSnapshot child : friendSnapshot.getChildren()) {
                        existingFriends.add(child.getKey());
                    }

                    FirebaseDatabase.getInstance().getReference("USERS")
                            .get().addOnSuccessListener(userSnapshot -> {
                                allUsers.clear();
                                for (DataSnapshot child : userSnapshot.getChildren()) {
                                    String user = child.getKey();
                                    if (!user.equals(currentUsername) && !existingFriends.contains(user)) {
                                        allUsers.add(user);
                                    }
                                }
                                filterUsers("");
                            });
                });
    }

    private void filterUsers(String keyword) {
        filteredUsers.clear();
        for (String user : allUsers) {
            if (user.toLowerCase().contains(keyword.toLowerCase())) {
                filteredUsers.add(user);
            }
        }
        adapter.notifyDataSetChanged();
    }
}