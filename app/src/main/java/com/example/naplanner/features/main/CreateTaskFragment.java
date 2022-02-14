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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class CreateTaskFragment extends Fragment {

    private FragmentCreateTaskBinding binding;
    private FirebaseAuth fAuth;
    private DatabaseReference database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).hideInteractionBars();
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();
        setupUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void setupUI(){
        binding.taskFormFragmentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskModel task = new TaskModel();
                task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
                task.setComplete(CreateTaskFragmentArgs.fromBundle(getArguments()).getIsComplete());
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

                database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        task.setId(Integer.parseInt("" + (snapshot.getChildrenCount() + 1)));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("Task" + (task.getId() - 1)).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Navigation.findNavController(requireView()).navigateUp();
                    }
                });
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