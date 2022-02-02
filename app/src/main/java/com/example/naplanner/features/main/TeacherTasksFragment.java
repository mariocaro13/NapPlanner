package com.example.naplanner.features.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.naplanner.adapter.CustomAdapter;
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

        tasks.add(new TaskModel(1, "hi", TaskModel.typeOfTask.LEGENDARY, false));
        tasks.add(new TaskModel(2, "hi", TaskModel.typeOfTask.EPIC, false));
        tasks.add(new TaskModel(3, "hi", TaskModel.typeOfTask.NORMAL, false));
        tasks.sort(new TasksSorter());
        binding.teacherTasksFragmentTasksListRecycleview.setAdapter(new CustomAdapter(tasks, this));
        binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditTap() {

    }

    @Override
    public void onCheckboxTap() {

    }
}