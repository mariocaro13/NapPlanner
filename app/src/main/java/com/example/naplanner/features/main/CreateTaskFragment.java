package com.example.naplanner.features.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.naplanner.MainActivity;
import com.example.naplanner.databinding.FragmentCreateTaskBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.interfaces.OnDataChange;
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

public class CreateTaskFragment extends Fragment implements OnDataChange {

    private FragmentCreateTaskBinding binding;
    private FirebaseAuth fAuth;
    private DatabaseReference database;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCreateTaskBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).hideInteractionBars();
        setupUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    private void setupUI() {

        if (CreateTaskFragmentArgs.fromBundle(getArguments()).getIsEdit()) {
            editTask();
        } else {
            createTask();
        }

        binding.taskFormFragmentCancelButton.setOnClickListener(view -> Navigation.findNavController(requireView()).navigateUp());
    }

    private void sendErrorMsg(String error) {
        Toast.makeText(requireActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }


    private void editTask() {
        int id = CreateTaskFragmentArgs.fromBundle(getArguments()).getId();
        if (id != -1)
            FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("Task" + id).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        TaskModel task = snapshot.getValue(TaskModel.class);
                        binding.taskFormFragmentNameEdittext.setText(Objects.requireNonNull(task).getName());
                        binding.taskFormRadioButtonLeg.setChecked(task.getType() == TaskModel.TaskType.LEGENDARY);
                        binding.taskFormRadioButtonEpic.setChecked(task.getType() == TaskModel.TaskType.EPIC);
                        binding.taskFormRadioButtonNormal.setChecked(task.getType() == TaskModel.TaskType.NORMAL);
                        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("Task" + id).removeEventListener(this);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        binding.taskFormFragmentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskModel task = new TaskModel();
                task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
                task.setId((id));
                if (binding.taskFormRadioButtonLeg.isChecked())
                    task.setType(TaskModel.TaskType.LEGENDARY);
                else if (binding.taskFormRadioButtonEpic.isChecked())
                    task.setType(TaskModel.TaskType.EPIC);
                else if (binding.taskFormRadioButtonNormal.isChecked())
                    task.setType(TaskModel.TaskType.NORMAL);
                else
                    sendErrorMsg("Seleccione una opcion de tipo de tarea");

                database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("Task" + (task.getId())).setValue(task)
                        .addOnCompleteListener(task1 -> Navigation.findNavController(requireView()).navigateUp());
            }
        });
    }

    private void createTask() {

        binding.taskFormFragmentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskModel task = new TaskModel();
                task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
                if (binding.taskFormRadioButtonLeg.isChecked())
                    task.setType(TaskModel.TaskType.LEGENDARY);
                else if (binding.taskFormRadioButtonEpic.isChecked())
                    task.setType(TaskModel.TaskType.EPIC);
                else if (binding.taskFormRadioButtonNormal.isChecked())
                    task.setType(TaskModel.TaskType.NORMAL);
                else
                    sendErrorMsg("Seleccione una opcion de tipo de tarea");


                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            task.setId((int) (snapshot.getChildrenCount() + 1));
                            Log.d("ID: ", Integer.toString(task.getId()));
                            onDataChanged(task);
                            database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                        } else {
                            task.setId(1);
                            database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("Task1").setValue(task)
                                    .addOnCompleteListener(task1 -> Navigation.findNavController(requireView()).navigateUp());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                };

                database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(eventListener);
            }
        });
    }

    @Override
    public void onDataChanged(TaskModel task) {
        database.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).child("Task" + (task.getId())).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });

    }

}

