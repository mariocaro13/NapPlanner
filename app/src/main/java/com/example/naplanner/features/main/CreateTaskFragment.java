package com.example.naplanner.features.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.naplanner.MainActivity;
import com.example.naplanner.databinding.FragmentCreateTaskBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.model.TaskModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CreateTaskFragment extends Fragment {

    private FragmentCreateTaskBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).hideInteractionBars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.taskFormFragmentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskModel task = new TaskModel();
                task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
                task.setComplete(CreateTaskFragmentArgs.fromBundle(getArguments()).getIsComplete());
                task.setId(2);
                binding.taskFormFragmentTaksImportanceRadioGroup.getChildAt(binding.taskFormFragmentTaksImportanceRadioGroup.getCheckedRadioButtonId()).toString();
                switch (binding.taskFormFragmentTaksImportanceRadioGroup.getCheckedRadioButtonId()) {
                    case 1:
                        task.setType(TaskModel.TaskType.LEGENDARY);
                        break;
                    case 2:
                        task.setType(TaskModel.TaskType.EPIC);
                        break;
                    case 3:
                        task.setType(TaskModel.TaskType.NORMAL);
                        break;
                    default:
                        return;
                }
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child("Task").setValue(task);
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
        binding.taskFormFragmentCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }
}