package com.example.naplanner.features.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.naplanner.databinding.FragmentCreateTaskBinding;
import com.example.naplanner.databinding.FragmentTeacherTasksBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;
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

    private void setupRecyclerView(){
        binding.teacherTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        tasks = new ArrayList<>();
        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, this, getContext());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        tasks.add(dataSnapshot.getValue(TaskModel.class));
                        adapter.notifyItemInserted(tasks.size());
                    }, 300);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tasks.sort(new TasksSorter());
        binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.teacherTasksFragmentTasksListRecycleview.setAdapter(adapter);
    }

    private void setupUI(){
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    ((MainActivity) requireActivity()).setupToolbar(name.substring(0, 1).toUpperCase() + name.substring(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
        tasks.get(taskID-1).setComplete(!tasks.get(taskID-1).isComplete());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Task"+taskID).child("complete").setValue(tasks.get(taskID-1).isComplete());
    }
}