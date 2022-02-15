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
import com.example.naplanner.databinding.FragmentTaskFormBinding;
import com.example.naplanner.model.TaskModel;

public class TaskFormFragment extends Fragment {

    private FragmentTaskFormBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTaskFormBinding.inflate(inflater, container, false);
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
                task.setComplete(TaskFormFragmentArgs.fromBundle(getArguments()).getIsComplete());
                String taskType;

                binding.taskFormFragmentTaksImportanceRadioGroup.getChildAt(binding.taskFormFragmentTaksImportanceRadioGroup.getCheckedRadioButtonId()).toString();
                switch (binding.taskFormFragmentTaksImportanceRadioGroup.getCheckedRadioButtonId()) {
                    case 1:
                        taskType = "LEGENDARY";
                        break;
                    case 2:
                        taskType = "EPIC";
                        break;
                    case 3:
                        taskType = "NORMAL";
                        break;
                    default:
                        return;
                }
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