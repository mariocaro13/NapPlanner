package com.example.naplanner.features.main.studentlist.view;

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
import com.example.naplanner.adapters.StudentListRecycleAdapter;
import com.example.naplanner.databinding.FragmentListStudentBinding;
import com.example.naplanner.features.main.studentlist.viewmodel.StudentListViewModel;
import com.example.naplanner.interfaces.StudentListener;
import com.example.naplanner.models.UserModel;

public class StudentListFragment extends Fragment implements StudentListener {

    private StudentListViewModel viewModel;
    private FragmentListStudentBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentListStudentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(StudentListViewModel.class);
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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setObservables() {
        viewModel.students.observe(getViewLifecycleOwner(), userModels -> {
                    StudentListRecycleAdapter adapter = new StudentListRecycleAdapter(userModels, this);
                    binding.studentListFragmentTasksListRecycleview.setAdapter(adapter);
                }
        );

        viewModel.notifyStudentListException.observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
    }

    private void setupUI() {
        viewModel.loadStudents();
        binding.studentListFragmentTasksListRecycleview.setHasFixedSize(true);
        binding.studentListFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
    }

    @Override
    public void onItemClicked(UserModel user) {
        StudentListFragmentDirections.ActionStudentListFragmentToTeacherTasksFragment action = StudentListFragmentDirections.actionStudentListFragmentToTeacherTasksFragment();
        action.setId(user.getuID());
        Navigation.findNavController(requireView()).navigate(action);
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}