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
import com.example.naplanner.databinding.FragmentOwnTasksBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;
import com.example.naplanner.utils.TasksSorter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class OwnTasksFragment extends Fragment implements TaskItemListener {

    public ArrayList<TaskModel> tasks = new ArrayList<>();
    private FragmentOwnTasksBinding binding;
    private FirebaseAuth fAuth;
    private DatabaseReference dRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentOwnTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
        fAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();
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
        tasks = new ArrayList<>();
        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, this, getContext());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Handler handler = new Handler();
                    TaskModel task = dataSnapshot.getValue(TaskModel.class);
                    handler.postDelayed(() -> {
                        if (Objects.requireNonNull(task).getCreatorID().equals("0") && !task.isComplete())
                            tasks.add(task);
                        adapter.notifyItemInserted(tasks.size());
                    }, 300);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        tasks.sort(new TasksSorter());
        binding.ownTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.ownTasksFragmentTasksListRecycleview.setAdapter(adapter);
    }

    private void setupUI() {
        dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    ((MainActivity) requireActivity()).setupNavigationBar(Objects.requireNonNull(snapshot.getValue(UserModel.class)).getStudent());
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
        OwnTasksFragmentDirections.ActionOwnTasksFragmentToTaskForm action = OwnTasksFragmentDirections.actionOwnTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setTaskID(taskID);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int taskID) {
        tasks.get(taskID - 1).setComplete(!tasks.get(taskID - 1).isComplete());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Task" + taskID).child("complete").setValue(tasks.get(taskID - 1).isComplete());
    }


}