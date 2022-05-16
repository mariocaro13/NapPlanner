package com.example.naplanner.features.main;

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
import com.example.naplanner.adapters.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentCompleteTasksBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.models.TaskModel;
import com.example.naplanner.utils.TasksSorter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CompleteTasksFragment extends Fragment implements TaskItemListener {

    private FragmentCompleteTasksBinding binding;
    private FirebaseAuth fAuth;
    private DatabaseReference dRef;
    public ArrayList<TaskModel> tasks = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCompleteTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
        fAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();
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
        binding.completeTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        tasks = new ArrayList<>();
        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, this, getContext());
        dRef.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        TaskModel task = dataSnapshot.getValue(TaskModel.class);
                        if (Objects.requireNonNull(task).isComplete()) {
                            tasks.add(dataSnapshot.getValue(TaskModel.class));
                            adapter.notifyItemInserted(tasks.size());
                        }
                    }, 300);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        tasks.sort(new TasksSorter());
        binding.completeTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.completeTasksFragmentTasksListRecycleview.setAdapter(adapter);
    }

    @Override
    public void onEditTap(int taskID) {
        CompleteTasksFragmentDirections.ActionCompleteTasksFragmentToTaskForm action = CompleteTasksFragmentDirections.actionCompleteTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setTaskID(taskID);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int taskID) {
        for (TaskModel task: tasks) {
            if(task.getId() == taskID) {
                task.setComplete(!task.isComplete());
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("Task" + taskID).child("complete").setValue(task.isComplete());
            }
        }

    }
}