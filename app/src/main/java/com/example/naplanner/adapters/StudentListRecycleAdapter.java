package com.example.naplanner.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.naplanner.databinding.ListItemStudentsBinding;
import com.example.naplanner.interfaces.StudentListener;
import com.example.naplanner.models.UserModel;

import java.util.ArrayList;

public class StudentListRecycleAdapter extends RecyclerView.Adapter<StudentListRecycleAdapter.ViewHolder> {

    final private ArrayList<UserModel> users;
    final private StudentListener listener;

    public StudentListRecycleAdapter(ArrayList<UserModel> users, StudentListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentListRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(ListItemStudentsBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListRecycleAdapter.ViewHolder holder, int position) {
        String userMail = users.get(position).getMail();
        holder.binding.usersListItemNameTaskTextView.setText(users.get(position).getUsername());
        holder.binding.usersListItemMailTaskTextView.setText(userMail);

        holder.binding.usersListItemLayout.setOnClickListener(v -> listener.onItemClicked(users.get(position)));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ListItemStudentsBinding binding;

        public ViewHolder(ListItemStudentsBinding mbinding) {
            super(mbinding.getRoot());
            binding = mbinding;
        }
    }

}

