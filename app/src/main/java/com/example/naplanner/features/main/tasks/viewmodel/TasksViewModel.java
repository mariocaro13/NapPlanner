package com.example.naplanner.features.main.tasks.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.TaskModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class TasksViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();

    private final MutableLiveData<ArrayList<TaskModel>> tasksData = new MutableLiveData<>();
    public final LiveData<ArrayList<TaskModel>> tasks = tasksData;

    private final MutableLiveData<String> userIdData = new MutableLiveData<>();
    public final LiveData<String> userId = userIdData;

    private final MutableLiveData<Exception> notifyTaskViewModelExceptionData = new MutableLiveData<>();
    public final LiveData<Exception> notifyTaskViewModelException = notifyTaskViewModelExceptionData;

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
                    tasksData.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelExceptionData.postValue(error.toException());
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
                    tasksData.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelExceptionData.postValue(error.toException());
                }
            });
    }

    public void loadTaskByTeacher(String studentId) {
        if (fAuth.getCurrentUser() != null)
            dRef.child("Tasks").child(studentId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<TaskModel> tempTasks = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        TaskModel task = dataSnapshot.getValue(TaskModel.class);
                        if (Objects.requireNonNull(task).getCreatorID().equals(fAuth.getCurrentUser().getUid()))
                            tempTasks.add(task);
                    }
                    tasksData.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelExceptionData.postValue(error.toException());
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
                    tasksData.postValue(tempTasks);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyTaskViewModelExceptionData.postValue(error.toException());
                }
            });
    }

    public void loadUserId() {
        userIdData.postValue(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
    }

    public void setTasksComplete(int taskID) {
        for (TaskModel task : Objects.requireNonNull(tasks.getValue())) {
            if (task.getId() == taskID) {
                task.setComplete(!task.isComplete());
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()).child("Task" + taskID).child("complete").setValue(task.isComplete());
            }
        }
    }
}
