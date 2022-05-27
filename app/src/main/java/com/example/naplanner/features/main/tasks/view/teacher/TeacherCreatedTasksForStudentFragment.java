package com.example.naplanner.features.main.tasks.view.teacher;

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
import com.example.naplanner.adapters.TeacherTaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentTasksTeacherBinding;
import com.example.naplanner.features.main.tasks.viewmodel.TasksViewModel;
import com.example.naplanner.interfaces.TaskItemListener;

public class TeacherCreatedTasksForStudentFragment extends Fragment implements TaskItemListener {

    private FragmentTasksTeacherBinding binding;
    private TasksViewModel viewModel;
    private String studentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTasksTeacherBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(TasksViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        studentId = TeacherCreatedTasksForStudentFragmentArgs.fromBundle(getArguments()).getId();

        setObservables();
        setupUI();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setObservables() {
        viewModel.tasks.observe(getViewLifecycleOwner(), taskModels -> {
            TeacherTaskRecycleAdapter adapter = new TeacherTaskRecycleAdapter(taskModels, this, requireContext());
            binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.teacherTasksFragmentTasksListRecycleview.setAdapter(adapter);
        });
        viewModel.userId.observe(getViewLifecycleOwner(), userId -> {
            TeacherCreatedTasksForStudentFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherCreatedTasksForStudentFragmentDirections.actionTeacherTasksFragmentToTaskForm();
            action.setUserID(studentId);
            action.setTeacherID(userId);
            Navigation.findNavController(requireView()).navigate(action);
        });
        viewModel.notifyTaskViewModelException.observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
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
        TeacherCreatedTasksForStudentFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherCreatedTasksForStudentFragmentDirections.actionTeacherTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setTaskID(taskID);
        action.setUserID(studentId);
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