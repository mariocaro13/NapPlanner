package com.example.naplanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.example.naplanner.R;
import com.example.naplanner.databinding.TasksListItemBinding;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.model.TaskModel;

import java.util.ArrayList;

public class TaskRecycleAdapter extends RecyclerView.Adapter<TaskRecycleAdapter.ViewHolder> {

    private ArrayList<TaskModel> tasks;
    private TaskItemListener listener;
    private Context context;

    public TaskRecycleAdapter(ArrayList<TaskModel> tasks, TaskItemListener listener, Context context) {
        this.tasks = tasks;
        this.listener = listener;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(TasksListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int pos = position;


        holder.binding.tasksListItemNameTaskTextView.setText(tasks.get(pos).getName());

        switch (tasks.get(pos).getType()) {
            case LEGENDARY:
                holder.binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.border_task_legendary_blue));
                break;
            case EPIC:
                holder.binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.border_task_epic_blue));
                break;
            case NORMAL:
                holder.binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.border_task_normal_blue));
                break;
            default:
        }

        holder.binding.taskListItemCompleteTaskCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCheckboxTap(tasks.get(pos).getId());
            }
        });
        holder.binding.taskListItemEditTaskImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onEditTap(tasks.get(pos).getId());
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
