package edu.northeastern.finalproject_group_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FriendsListAdapter extends RecyclerView.Adapter<FriendsListAdapter.ViewHolder> {

    private List<String> friends;

    public FriendsListAdapter(List<String> friends) {
        this.friends = friends;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView username, userId;

        public ViewHolder(View itemView) {
            super(itemView);
            avatar = itemView.findViewById(R.id.friendAvatar);
            username = itemView.findViewById(R.id.friendUsername);
            userId = itemView.findViewById(R.id.friendUserId);
        }

        public void bind(String friendName) {
            username.setText(friendName);
            userId.setText("ID: " + friendName);
        }
    }

    @NonNull
    @Override
    public FriendsListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsListAdapter.ViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }
}