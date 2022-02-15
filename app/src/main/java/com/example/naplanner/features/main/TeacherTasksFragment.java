package com.example.naplanner.features.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.naplanner.MainActivity;
import com.example.naplanner.adapter.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentTeacherTasksBinding;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.utils.TasksSorter;

import java.util.ArrayList;

public class TeacherTasksFragment extends Fragment implements TaskItemListener {

    private FragmentTeacherTasksBinding binding;
    public ArrayList<TaskModel> tasks = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.teacherTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        tasks.add(new TaskModel(3, "Complete", TaskModel.TaskType.NORMAL, false));
        tasks.add(new TaskModel(3, "Normal", TaskModel.TaskType.NORMAL, false));
        tasks.add(new TaskModel(1, "Legendary", TaskModel.TaskType.LEGENDARY, false));
        tasks.add(new TaskModel(3, "Normal", TaskModel.TaskType.NORMAL, false));
        tasks.add(new TaskModel(3, "Normal", TaskModel.TaskType.NORMAL, false));
        tasks.add(new TaskModel(1, "Legendary", TaskModel.TaskType.LEGENDARY, false));
        tasks.add(new TaskModel(2, "Epic", TaskModel.TaskType.EPIC, false));

        tasks.sort(new TasksSorter());
        binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.teacherTasksFragmentTasksListRecycleview.setAdapter(new TaskRecycleAdapter(tasks, this, getContext()));
    }


    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditTap(boolean isComplete) {
        TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm(isComplete);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int position) {
        tasks.get(position).setComplete(!tasks.get(position).isComplete());

    }
}