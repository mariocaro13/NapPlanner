package com.example.naplanner.features.main.owntasks.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.GnssAntennaInfo;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.naplanner.adapters.TaskRecycleAdapter;
import com.example.naplanner.databinding.FragmentOwnTasksBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.interfaces.TaskItemListener;
import com.example.naplanner.models.TaskModel;
import com.example.naplanner.models.UserModel;
import com.example.naplanner.utils.TasksSorter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Objects;

public class OwnTasksViewModel extends ViewModel {

    private ArrayList<TaskModel> tasks;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();;
    private final DatabaseReference dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();

    public void getDatabaseUser() {
        if(fAuth.getCurrentUser() != null)
        dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    //((MainActivity) requireActivity()).setupToolbar(name.substring(0, 1).toUpperCase() + name.substring(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getDatabaseTasks(FragmentOwnTasksBinding binding, Context context, TaskItemListener listener) {
        tasks = new ArrayList<>();
        TaskRecycleAdapter adapter = new TaskRecycleAdapter(tasks, listener, context);
        if(fAuth.getCurrentUser() != null)
        dRef.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
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
        binding.ownTasksFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        binding.ownTasksFragmentTasksListRecycleview.setAdapter(adapter);
    }

    public void setTasksComplete(int taskID){
        for (TaskModel task : tasks) {
            if (task.getId() == taskID) {
                task.setComplete(!task.isComplete());
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks")
                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        .child("Task" + taskID).child("complete").setValue(task.isComplete());
            }
        }
    }
}
