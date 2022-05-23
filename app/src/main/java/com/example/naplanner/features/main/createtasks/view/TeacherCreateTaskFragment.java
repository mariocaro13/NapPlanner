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

    private final TaskModel task = new TaskModel();
    private CreateTaskViewModel viewModel;
    private FragmentCreateTaskTeacherBinding binding;
    private String studentId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateTaskTeacherBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(CreateTaskViewModel.class);
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupVariables() {
        studentId = TeacherCreateTaskFragmentArgs.fromBundle(getArguments()).getUserID();
    }

    private void setupUI() {

        viewModel.loadUsername();
        viewModel.loadUserId();

        ((MainActivity) requireActivity()).hideInteractionBars();
        binding.taskFormFragmentCancelButton.setOnClickListener(view -> Navigation.findNavController(requireView()).navigateUp());
    }

    private void setObservables() {
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            String name = username.substring(0, 1).toUpperCase() + username.substring(1);
            binding.taskFormFragmentUsernameTextView.setText(name);
        });
        viewModel.getTask().observe(getViewLifecycleOwner(), task -> {
            binding.taskFormFragmentNameEdittext.setText(Objects.requireNonNull(task).getName());
            binding.taskFormRadioButtonLeg.setChecked(task.getType() == TaskModel.TaskType.LEGENDARY);
            binding.taskFormRadioButtonEpic.setChecked(task.getType() == TaskModel.TaskType.EPIC);
            binding.taskFormRadioButtonNormal.setChecked(task.getType() == TaskModel.TaskType.NORMAL);
        });
        viewModel.getUserId().observe(getViewLifecycleOwner(), userId -> {
            if (studentId.equals("-1")) studentId = userId;

            if (TeacherCreateTaskFragmentArgs.fromBundle(getArguments()).getIsEdit())
                editTask();
            else
                createTask();
        });
        viewModel.getNavigate().observe(getViewLifecycleOwner(), unused -> Navigation.findNavController(requireView()).navigateUp());
        viewModel.getNotifyCreateTaskViewModelException().observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
    }

    private void editTask() {
        int taskID = TeacherCreateTaskFragmentArgs.fromBundle(getArguments()).getTaskID();
        if (taskID != -1)
            viewModel.loadTask(taskID, studentId);

        binding.taskFormFragmentConfirmButton.setOnClickListener(view -> {
            getData();
            viewModel.editTask(taskID, studentId, task);
        });
    }

    private void createTask() {
        binding.taskFormFragmentConfirmButton.setOnClickListener(view -> {
            getData();
            viewModel.createTask(studentId, task);
        });
    }

    private void getData() {
        if (!binding.taskFormFragmentNameEdittext.getText().toString().isEmpty())
            task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
        else {
            printMsg("Introduzca un nombre para la tarea");
            return;
        }

        if (binding.taskFormRadioButtonLeg.isChecked())
            task.setType(TaskModel.TaskType.LEGENDARY);
        else if (binding.taskFormRadioButtonEpic.isChecked())
            task.setType(TaskModel.TaskType.EPIC);
        else if (binding.taskFormRadioButtonNormal.isChecked())
            task.setType(TaskModel.TaskType.NORMAL);
        else {
            printMsg("Seleccione una opcion de tipo de tarea");
        }
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}


