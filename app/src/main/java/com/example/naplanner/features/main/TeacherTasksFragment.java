package com.example.naplanner.features.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.adapters.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentTeacherTasksBinding;
import com.example.naplanner.features.main.tasks.view.CompleteTasksFragmentDirections;
import com.example.naplanner.features.main.tasks.viewmodel.TasksViewModel;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.models.UserModel;

import java.util.Locale;
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
        setObservables();
        boolean isTeacher = !TeacherTasksFragmentArgs.fromBundle(getArguments()).getId().equals("-1");
        if (isTeacher) {
            studentId = TeacherTasksFragmentArgs.fromBundle(getArguments()).getId();
            ((MainActivity) requireActivity()).hideInteractionBars();
        } else {
            ((MainActivity) requireActivity()).showInteractionBars();
            studentId = viewModel.getUserId();
            hideButtons();
        }
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
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> ((MainActivity) requireActivity()).setupToolbar(username.substring(0, 1).toUpperCase() + username.substring(1)));
        viewModel.getNotifyTaskViewModelException().observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
    }

    @Override
    public void onCheckboxTap(int taskID) {
        viewModel.setTasksComplete(taskID);
    }

    @Override
    public void onEditTap(int taskID) {
        CompleteTasksFragmentDirections.ActionCompleteTasksFragmentToTaskForm action = CompleteTasksFragmentDirections.actionCompleteTasksFragmentToTaskForm();
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

    private void setupUI() {
        viewModel.decideTaskToLoad(studentId);

        binding.teacherTasksFragmentReturnButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.teacherTasksFragmentCreateButton.setOnClickListener(v -> {
            TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm();
            action.setUserID(studentId);
            action.setTeacherID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
            Navigation.findNavController(requireView()).navigate(action);
        });
    }

    public void hideButtons() {
        binding.teacherTasksFragmentReturnButton.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = binding.getRoot();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.teacher_tasks_fragment_return_button, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.clear(R.id.teacher_tasks_fragment_return_button, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);

        binding.teacherTasksFragmentCreateButton.setVisibility(View.GONE);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.teacher_tasks_fragment_create_button, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.clear(R.id.teacher_tasks_fragment_create_button, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);
    }
}