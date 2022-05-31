package com.example.naplanner.features.main.createtasks.view;

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

import com.example.naplanner.MainActivity;
import com.example.naplanner.databinding.FragmentCreateTaskTeacherBinding;
import com.example.naplanner.features.main.createtasks.viewmodel.CreateTaskViewModel;
import com.example.naplanner.models.TaskModel;

import java.util.Objects;

public class TeacherCreateTaskFragment extends Fragment {

    private CreateTaskViewModel viewModel;
    private FragmentCreateTaskTeacherBinding binding;
    private String studentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateTaskTeacherBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(CreateTaskViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        setupVariables();
        setObservables();
        setupUI();

        printMsg("Im Started");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupVariables() {
        studentId = TeacherCreateTaskFragmentArgs.fromBundle(getArguments()).getUserID();
        if (getArguments() != null)
            studentId = getArguments().getString("userID");
    }

    private void setupUI() {

        viewModel.loadUsername();
        viewModel.loadUserId();

        ((MainActivity) requireActivity()).hideInteractionBars();
        binding.taskFormFragmentCancelButton.setOnClickListener(view -> Navigation.findNavController(requireView()).navigateUp());
    }

    private void setObservables() {
        viewModel.username.observe(getViewLifecycleOwner(), username -> {
            String name = username.substring(0, 1).toUpperCase() + username.substring(1);
            binding.taskFormFragmentUsernameTextView.setText(name);
        });
        viewModel.task.observe(getViewLifecycleOwner(), task -> {
            binding.taskFormFragmentNameEdittext.setText(Objects.requireNonNull(task).getName());
            binding.taskFormRadioButtonLeg.setChecked(task.getType() == TaskModel.TaskType.LEGENDARY);
            binding.taskFormRadioButtonEpic.setChecked(task.getType() == TaskModel.TaskType.EPIC);
            binding.taskFormRadioButtonNormal.setChecked(task.getType() == TaskModel.TaskType.NORMAL);
        });
        viewModel.userId.observe(getViewLifecycleOwner(), userId -> {
            if (studentId.equals("-1")) studentId = userId;

            if (TeacherCreateTaskFragmentArgs.fromBundle(getArguments()).getIsEdit())
                editTask();
            else
                createTask();
        });
        viewModel.navigate.observe(getViewLifecycleOwner(), unused -> Navigation.findNavController(requireView()).navigateUp());
        viewModel.notifyCreateTaskViewModelException.observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
    }

    private void editTask() {
        int taskID = TeacherCreateTaskFragmentArgs.fromBundle(getArguments()).getTaskID();
        if (taskID != -1)
            viewModel.loadTask(taskID, studentId);

        binding.taskFormFragmentConfirmButton.setOnClickListener(view -> {
            TaskModel task = getData();
            if (task != null) viewModel.editTask(taskID, studentId, task);
        });
    }

    private void createTask() {
        binding.taskFormFragmentConfirmButton.setOnClickListener(view -> {
            TaskModel task = getData();
            if (task != null) viewModel.createTask(studentId, task);
        });
    }

    private TaskModel getData() {
        TaskModel task = new TaskModel();
        if (!binding.taskFormFragmentNameEdittext.getText().toString().isEmpty())
            task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
        else {
            printMsg("Introduzca un nombre para la tarea");
            return null;
        }

        if (binding.taskFormRadioButtonLeg.isChecked())
            task.setType(TaskModel.TaskType.LEGENDARY);
        else if (binding.taskFormRadioButtonEpic.isChecked())
            task.setType(TaskModel.TaskType.EPIC);
        else if (binding.taskFormRadioButtonNormal.isChecked())
            task.setType(TaskModel.TaskType.NORMAL);
        else {
            printMsg("Seleccione una opcion de tipo de tarea");
            return null;
        }

        return task;
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}


