package com.example.naplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naplanner.R;
import com.example.naplanner.databinding.StudentsListItemBinding;
import com.example.naplanner.interfaces.StudentListener;
import com.example.naplanner.model.UserModel;

import java.util.ArrayList;

public class StudentListRecycleAdapter extends RecyclerView.Adapter<StudentListRecycleAdapter.ViewHolder> {


    private ArrayList<UserModel> users;
    private StudentListener listener;

    public StudentListRecycleAdapter(ArrayList<UserModel> users, StudentListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public StudentListRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new StudentListRecycleAdapter.ViewHolder(StudentsListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListRecycleAdapter.ViewHolder holder, int position) {
        int pos = position;
        String userMail = users.get(pos).getMail();
        holder.binding.usersListItemNameTaskTextView.setText(users.get(pos).getUsername());
        holder.binding.usersListItemMailTaskTextView.setText(userMail);

        holder.binding.usersListItemLayout.setOnClickListener(v -> listener.onItemClicked(users.get(pos)));

    }


    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        StudentsListItemBinding binding;

        public ViewHolder(StudentsListItemBinding mbinding) {
            super(mbinding.getRoot());
            binding = mbinding;
        }
    }

}

