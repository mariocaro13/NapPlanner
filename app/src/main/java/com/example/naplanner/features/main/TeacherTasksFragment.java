package com.example.naplanner.features.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.naplanner.MainActivity;
import com.example.naplanner.adapter.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentTeacherTasksBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.utils.TasksSorter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TeacherTasksFragment extends Fragment implements TaskItemListener {

    private FragmentTeacherTasksBinding binding;
    private FirebaseAuth fAuth;
    public ArrayList<TaskModel> tasks = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
        fAuth = FirebaseAuth.getInstance();
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

    private void setupRecyclerView(){
        binding.teacherTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        tasks = new ArrayList<>();
        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, this, getContext());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    tasks.add(dataSnapshot.getValue(TaskModel.class));
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tasks.sort(new TasksSorter());
        binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.teacherTasksFragmentTasksListRecycleview.setAdapter(adapter);
    }

    @Override
    public void onEditTap(int taskID) {
        TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setId(taskID);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int taskID) {
        tasks.get(taskID).setComplete(!tasks.get(taskID).isComplete());
        FirebaseDatabase.getInstance().getReference(Constants.databaseURL).child("Tasks").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Task"+taskID).child("complete").setValue(tasks.get(taskID).isComplete());

    }
}