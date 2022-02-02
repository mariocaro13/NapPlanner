package com.example.naplanner.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naplanner.databinding.TasksListItemBinding;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.model.TaskModel;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<TaskModel> tasks;
    private TaskItemListener listener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return new ViewHolder(TasksListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext())));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tasksListItemNameTaskTextView.setText(tasks.get(position).getName());
        holder.binding.taskListItemCompleteTaskCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCheckboxTap();
            }
        });
        holder.binding.taskListItemEditTaskImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditTap();
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TasksListItemBinding binding;

        public ViewHolder(TasksListItemBinding mbinding) {
            super(mbinding.getRoot());
            binding = mbinding;
        }
    }

}
