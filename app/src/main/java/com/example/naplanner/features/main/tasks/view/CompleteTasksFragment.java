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
import com.example.naplanner.databinding.FragmentCompleteTasksBinding;
import com.example.naplanner.features.main.tasks.viewmodel.TasksViewModel;
import com.example.naplanner.interfaces.TaskItemListener;

public class CompleteTasksFragment extends Fragment implements TaskItemListener {

    private FragmentCompleteTasksBinding binding;
    private TasksViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompleteTasksBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
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
            binding.completeTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
            binding.completeTasksFragmentTasksListRecycleview.setAdapter(adapter);
        });
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> ((MainActivity) requireActivity()).setupToolbar(username.substring(0, 1).toUpperCase() + username.substring(1)));
        viewModel.getNotifyTaskViewModelException().observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
    }

    private void setupUI() {
        viewModel.loadUsername();
        viewModel.loadOwnTasks(true);
        binding.completeTasksFragmentTasksListRecycleview.setHasFixedSize(true);
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
}