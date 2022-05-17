package com.example.naplanner.features.main.owntasks.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.naplanner.MainActivity;
import com.example.naplanner.databinding.FragmentOwnTasksBinding;
import com.example.naplanner.features.main.owntasks.viewmodel.OwnTasksViewModel;
import com.example.naplanner.interfaces.TaskItemListener;

public class OwnTasksFragment extends Fragment implements TaskItemListener {

    private FragmentOwnTasksBinding binding;
    private OwnTasksViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOwnTasksBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(OwnTasksViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
        viewModel.getIntance();
        setupUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        binding.ownTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        viewModel.getDatabaseTaks(binding, getContext(), this);
    }

    private void setupUI() {
        viewModel.getDatabaseUser();
    }

    @Override
    public void onEditTap(int taskID) {
        OwnTasksFragmentDirections.ActionOwnTasksFragmentToTaskForm action = OwnTasksFragmentDirections.actionOwnTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setTaskID(taskID);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int taskID) {
        viewModel.setTasksComplete(taskID);
    }
}