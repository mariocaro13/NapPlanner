package com.example.naplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naplanner.databinding.StudentsListItemBinding;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;

import java.util.ArrayList;

public class StudentListRecycleAdapter  extends RecyclerView.Adapter<StudentListRecycleAdapter.ViewHolder> {


    private ArrayList<UserModel> users;
    private Context context;

    public StudentListRecycleAdapter(ArrayList<UserModel> tasks, Context context) {
        this.users = tasks;
        this.context = context;
    }


    @NonNull
    @Override
    public StudentListRecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new StudentListRecycleAdapter.ViewHolder(StudentsListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StudentListRecycleAdapter.ViewHolder holder, int position) {
        int pos = position;


        holder.binding.usersListItemNameTaskTextView.setText(users.get(pos).getUsername());

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

