package edu.northeastern.finalproject_group_1;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.ViewHolder> {

    private final List<String> users;
    private final String currentUser;
    private final DialogFragment parentDialog;
    private final AddFriendDialog.OnFriendAddedListener listener;

    public AddFriendAdapter(List<String> users, String currentUser, DialogFragment parentDialog,
                            AddFriendDialog.OnFriendAddedListener listener) {
        this.users = users;
        this.currentUser = currentUser;
        this.parentDialog = parentDialog;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        ImageButton addButton;

        public ViewHolder(View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.textViewCandidateUsername);
            addButton = itemView.findViewById(R.id.buttonAddCandidate);
        }
    }

    @NonNull
    @Override
    public AddFriendAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_candidate_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendAdapter.ViewHolder holder, int position) {
        String friendUsername = users.get(position);
        holder.usernameText.setText(friendUsername);

        holder.addButton.setOnClickListener(v -> {
            new AlertDialog.Builder(v.getContext())
                    .setTitle("Add Friend")
                    .setMessage("Add " + friendUsername + " as your friend?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseDatabase.getInstance()
                                .getReference("FRIENDSLIST")
                                .child(currentUser)
                                .child(friendUsername)
                                .setValue("")
                                .addOnSuccessListener(task -> {
                                    Toast.makeText(v.getContext(), "Added " + friendUsername, Toast.LENGTH_SHORT).show();
                                    if (listener != null) {
                                        listener.onFriendAdded(); // 刷新主列表
                                    }
                                    parentDialog.dismiss();
                                });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}