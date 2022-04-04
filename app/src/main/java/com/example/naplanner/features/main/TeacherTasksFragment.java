package com.example.naplanner.features.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.adapter.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentTeacherTasksBinding;
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

public class TeacherTasksFragment extends Fragment implements TaskItemListener {

    public ArrayList<TaskModel> tasks = new ArrayList<>();
    private FragmentTeacherTasksBinding binding;
    private FirebaseAuth fAuth;
    private DatabaseReference dRef;
    private String id;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();
        dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();
        if (!TeacherTasksFragmentArgs.fromBundle(getArguments()).getId().equals("-1")) {
            id = TeacherTasksFragmentArgs.fromBundle(getArguments()).getId();
            ((MainActivity) requireActivity()).hideInteractionBars();
        } else {
            id = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
            ((MainActivity) requireActivity()).showInteractionBars();
            hideButtons();
        }
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
        binding.teacherTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        tasks = new ArrayList<>();

        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, this, getContext());
        dRef.child("Tasks").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(id).removeEventListener(this);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        TaskModel task = dataSnapshot.getValue(TaskModel.class);
                        if (!Objects.requireNonNull(task).getCreatorID().equals("0") && !Objects.requireNonNull(task).getCreatorID().equals(id) && id.equals(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())) {
                            tasks.add(dataSnapshot.getValue(TaskModel.class));
                            adapter.notifyItemInserted(tasks.size());
                        } else if (task.getCreatorID().equals(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())) {
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
        binding.teacherTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.teacherTasksFragmentTasksListRecycleview.setAdapter(adapter);
    }

    @Override
    public void onEditTap(int taskID) {
        TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm();
        action.setIsEdit(true);
        action.setTaskID(taskID);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int taskID) {
        tasks.get(taskID - 1).setComplete(!tasks.get(taskID - 1).isComplete());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Task" + taskID).child("complete").setValue(tasks.get(taskID - 1).isComplete());
    }

    private void setupUI() {
        dRef.child("User").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("User").child(id).removeEventListener(this);
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    ((MainActivity) requireActivity()).setupToolbar(name.substring(0, 1).toUpperCase() + name.substring(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        binding.teacherTasksFragmentReturnButton.setOnClickListener(v -> Navigation.findNavController(requireView()).navigateUp());
        binding.teacherTasksFragmentCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm();
                action.setUserID(id);
                action.setTeacherID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                Navigation.findNavController(requireView()).navigate(action);
            }
        });
    }

    public void hideButtons() {
        binding.teacherTasksFragmentReturnButton.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = binding.getRoot();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.teacher_tasks_fragment_return_button, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.clear(R.id.teacher_tasks_fragment_return_button, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);

        binding.teacherTasksFragmentCreateButton.setVisibility(View.GONE);
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.teacher_tasks_fragment_create_button, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.clear(R.id.teacher_tasks_fragment_create_button, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);
    }
}