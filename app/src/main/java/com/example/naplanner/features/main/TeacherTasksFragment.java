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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TeacherTasksFragment extends Fragment implements TaskItemListener {

    private FragmentTeacherTasksBinding binding;
    public ArrayList<TaskModel> tasks = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentTeacherTasksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.teacherTasksFragmentTasksListRecycleview.setHasFixedSize(true);
        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, this, getContext());

        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    tasks.add(dataSnapshot.getValue(TaskModel.class));
                    adapter.notifyDataSetChanged();
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
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onEditTap(boolean isComplete) {
        TeacherTasksFragmentDirections.ActionTeacherTasksFragmentToTaskForm action = TeacherTasksFragmentDirections.actionTeacherTasksFragmentToTaskForm(isComplete);
        Navigation.findNavController(requireView()).navigate(action);
    }

    @Override
    public void onCheckboxTap(int position) {
        tasks.get(position).setComplete(!tasks.get(position).isComplete());

    }
}