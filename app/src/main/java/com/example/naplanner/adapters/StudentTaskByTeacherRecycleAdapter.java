package com.example.naplanner.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;
import com.example.naplanner.R;
import com.example.naplanner.databinding.TasksReducedListItemBinding;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.models.TaskModel;

import java.util.ArrayList;

public class StudentTaskByTeacherRecycleAdapter extends RecyclerView.Adapter<StudentTaskByTeacherRecycleAdapter.ViewHolder> {

    private final ArrayList<TaskModel> tasks;
    private final TaskItemListener listener;
    private final Context context;

    public StudentTaskByTeacherRecycleAdapter(ArrayList<TaskModel> tasks, TaskItemListener listener, Context context) {
        this.tasks = tasks;
        this.listener = listener;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        return new ViewHolder(TasksReducedListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.tasksListItemNameTaskTextView.setText(tasks.get(position).getName());
        holder.binding.taskListItemCompleteTaskCheckbox.setChecked(tasks.get(position).isComplete());
        useStudentPalette(holder, position);
        holder.binding.taskListItemCompleteTaskCheckbox.setOnClickListener(view -> listener.onCheckboxTap(tasks.get(position).getId()));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    private void useStudentPalette(ViewHolder holder, int position) {
        switch (tasks.get(position).getType()) {
            case LEGENDARY:
                holder.binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.border_task_legendary_green));
                break;
            case EPIC:
                holder.binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.border_task_epic_green));
                break;
            case NORMAL:
                holder.binding.getRoot().setBackground(AppCompatResources.getDrawable(context, R.drawable.border_task_normal_green));
                break;
            default:
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TasksReducedListItemBinding binding;

        public ViewHolder(TasksReducedListItemBinding mbinding) {
            super(mbinding.getRoot());
            binding = mbinding;
        }
    }
}
