package com.example.naplanner.features.main.tasks.viewmodel;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.TaskModel;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Objects;

public class TasksViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();
    private final MutableLiveData<ArrayList<TaskModel>> tasks = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifyTaskViewModelException = new MutableLiveData<>();

    public void loadUsername() {
        if (fAuth.getCurrentUser() != null)
            dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                    if (snapshot.exists()) {
                        String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                        username.postValue(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelException.postValue(error.toException());
                }
            });
    }

    public void loadUsername(String targetUserId) {
        dRef.child("User").child(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    username.postValue(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyTaskViewModelException.postValue(error.toException());
            }
        });
    }

    public void decideTaskToLoad(String userIdToCompare) {
        if (userIdToCompare.equals(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())) loadOwnTasks();
        else loadAllTeacherTasks();
    }

    public void loadOwnTasks() {
        if (fAuth.getCurrentUser() != null)
            dRef.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<TaskModel> tempTasks = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TaskModel task = dataSnapshot.getValue(TaskModel.class);
                        if (Objects.requireNonNull(task).getCreatorID().equals(fAuth.getCurrentUser().getUid()))
                            tempTasks.add(task);
                    }
                    tasks.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelException.postValue(error.toException());
                }
            });
    }

    public void loadOwnTasks(boolean isComplete) {
        if (fAuth.getCurrentUser() != null)
            dRef.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<TaskModel> tempTasks = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TaskModel task = dataSnapshot.getValue(TaskModel.class);
                        if (Objects.requireNonNull(task).getCreatorID().equals(fAuth.getCurrentUser().getUid()) && task.isComplete() == isComplete)
                            tempTasks.add(task);
                    }
                    tasks.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelException.postValue(error.toException());
                }
            });
    }

    public void loadAllTeacherTasks() {
        if (fAuth.getCurrentUser() != null)
            dRef.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<TaskModel> tempTasks = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TaskModel task = dataSnapshot.getValue(TaskModel.class);
                        if (!Objects.requireNonNull(task).getCreatorID().equals(fAuth.getCurrentUser().getUid()))
                            tempTasks.add(task);
                    }
                    tasks.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelException.postValue(error.toException());
                }
            });
    }

    public void setTasksComplete(int taskID) {
        for (TaskModel task : Objects.requireNonNull(tasks.getValue())) {
            if (task.getId() == taskID) {
                task.setComplete(!task.isComplete());
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Task" + taskID).child("complete").setValue(task.isComplete());
            }
        }
    }

    public MutableLiveData<Exception> getNotifyTaskViewModelException() {
        return notifyTaskViewModelException;
    }

    public MutableLiveData<ArrayList<TaskModel>> getTasks() {
        return tasks;
    }

    public MutableLiveData<String> getUsername() {
        return username;
    }
}
