package com.example.naplanner.features.main;

import android.annotation.SuppressLint;
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
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;
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
    private String id;
    private String teacherID;
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
        if (!CreateTaskFragmentArgs.fromBundle(getArguments()).getUserID().equals("-1"))
            id = CreateTaskFragmentArgs.fromBundle(getArguments()).getUserID();
        else
            id = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        if (!CreateTaskFragmentArgs.fromBundle(getArguments()).getTeacherID().equals("-1"))
            teacherID = CreateTaskFragmentArgs.fromBundle(getArguments()).getTeacherID();
        else teacherID = "0";
        setupUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupUI() {
        if (CreateTaskFragmentArgs.fromBundle(getArguments()).getIsEdit())
            editTask();
        else
            createTask();

        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").child(id).removeEventListener(this);
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    binding.taskFormFragmentUsernameTextView.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        binding.taskFormFragmentCancelButton.setOnClickListener(view -> Navigation.findNavController(requireView()).navigateUp());
    }

    private void sendErrorMsg(String error) {
        Toast.makeText(requireActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }


    private void editTask() {
        int taskID = CreateTaskFragmentArgs.fromBundle(getArguments()).getTaskID();
        if (taskID != -1)
            FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(id).child("Task" + taskID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        TaskModel task = snapshot.getValue(TaskModel.class);
                        binding.taskFormFragmentNameEdittext.setText(Objects.requireNonNull(task).getName());
                        binding.taskFormRadioButtonLeg.setChecked(task.getType() == TaskModel.TaskType.LEGENDARY);
                        binding.taskFormRadioButtonEpic.setChecked(task.getType() == TaskModel.TaskType.EPIC);
                        binding.taskFormRadioButtonNormal.setChecked(task.getType() == TaskModel.TaskType.NORMAL);
                        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(id).child("Task" + taskID).removeEventListener(this);
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
                if (!binding.taskFormFragmentNameEdittext.getText().toString().isEmpty())
                    task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
                else {
                    sendErrorMsg("Introduzca un nombre para la tarea");
                    return;
                }
                task.setId((taskID));
                task.setCreatorID(teacherID);
                if (binding.taskFormRadioButtonLeg.isChecked())
                    task.setType(TaskModel.TaskType.LEGENDARY);
                else if (binding.taskFormRadioButtonEpic.isChecked())
                    task.setType(TaskModel.TaskType.EPIC);
                else if (binding.taskFormRadioButtonNormal.isChecked())
                    task.setType(TaskModel.TaskType.NORMAL);
                else {
                    sendErrorMsg("Seleccione una opcion de importancia de tarea");
                    return;
                }

                database.child("Tasks").child(id).child("Task" + (task.getId())).setValue(task)
                        .addOnCompleteListener(task1 -> Navigation.findNavController(requireView()).navigateUp());
            }
        });
    }

    private void createTask() {
        binding.taskFormFragmentConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TaskModel task = new TaskModel();

                if (!binding.taskFormFragmentNameEdittext.getText().toString().isEmpty())
                    task.setName(binding.taskFormFragmentNameEdittext.getText().toString());
                else {
                    sendErrorMsg("Introduzca un nombre para la tarea");
                    return;
                }

                if (binding.taskFormRadioButtonLeg.isChecked())
                    task.setType(TaskModel.TaskType.LEGENDARY);
                else if (binding.taskFormRadioButtonEpic.isChecked())
                    task.setType(TaskModel.TaskType.EPIC);
                else if (binding.taskFormRadioButtonNormal.isChecked())
                    task.setType(TaskModel.TaskType.NORMAL);
                else {
                    sendErrorMsg("Seleccione una opcion de tipo de tarea");
                    return;
                }

                task.setCreatorID(teacherID);
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        database.child("Tasks").child(id).removeEventListener(this);
                        if (snapshot.exists()) {
                            task.setId((int) (snapshot.getChildrenCount() + 1));
                            Log.d("ID: ", Integer.toString(task.getId()));
                            database.child("Tasks").child(id).child("Task" + (task.getId())).setValue(task)
                                    .addOnCompleteListener(task1 -> Navigation.findNavController(requireView()).navigateUp());
                        } else {
                            task.setId(1);
                            database.child("Tasks").child(id).child("Task1").setValue(task)
                                    .addOnCompleteListener(task1 -> Navigation.findNavController(requireView()).navigateUp());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };

                database.child("Tasks").child(id).addValueEventListener(eventListener);
            }
        });
    }
}

