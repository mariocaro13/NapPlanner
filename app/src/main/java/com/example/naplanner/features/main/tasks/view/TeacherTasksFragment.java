package com.example.naplanner.features.main.tasks.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.naplanner.MainActivity;
import com.example.naplanner.adapters.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentTeacherTasksBinding;
import com.example.naplanner.features.main.tasks.viewmodel.TasksViewModel;
import com.example.naplanner.interfaces.TaskItemListener;

import java.util.Objects;

public class TeacherTasksFragment extends Fragment implements TaskItemListener {

    private FragmentTeacherTasksBinding binding;
    private TasksViewModel viewModel;
    private String studentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherTasksBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        studentId = TeacherTasksFragmentArgs.fromBundle(getArguments()).getId();

        setObservables();
        setupUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setObservables() {
        viewModel.getTasks().observe(getViewLifecycleOwner(), taskModels -> {
            TaskRecycleAdapter adapter = new TaskRecycleAdapter(taskModels, this, requireContext());
            binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.teacherTasksFragmentTasksListRecycleview.setAdapter(adapter);
        });
        viewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
            TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm();
            action.setUserID(studentId);
            action.setTeacherID(userId);
            Navigation.findNavController(requireView()).navigate(action);
        });
        viewModel.getNotifyTaskViewModelException().observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
    }

    private void setupUI() {
        viewModel.loadTaskByTeacher(studentId);
        ((MainActivity) requireActivity()).hideInteractionBars();
        binding.teacherTasksFragmentReturnButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.teacherTasksFragmentCreateButton.setOnClickListener(v -> viewModel.loadUserId());
    }

    @Override
    public void onCheckboxTap(int taskID) {
        viewModel.setTasksComplete(taskID);
    }

    @Override
    public void onEditTap(int taskID) {
        TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setTaskID(taskID);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}