package com.example.naplanner.features.main.createtasks.viewmodel;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.TaskModel;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class CreateTaskViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();

    private final MutableLiveData<ArrayList<TaskModel>> tasksData = new MutableLiveData<>();
    public LiveData<ArrayList<TaskModel>> tasks = tasksData;

    private final MutableLiveData<String> usernameData = new MutableLiveData<>();
    public LiveData<String> username = usernameData;

    private final MutableLiveData<Void> navigateData = new MutableLiveData<>();
    public LiveData<Void> navigate = navigateData;

    private final MutableLiveData<TaskModel> taskData = new MutableLiveData<>();
    public LiveData<TaskModel> task = taskData;

    private final MutableLiveData<String> userIdData = new MutableLiveData<>();
    public LiveData<String> userId = userIdData;

    private final MutableLiveData<Exception> notifyCreateTaskViewModelExceptionData = new MutableLiveData<>();
    public LiveData<Exception> notifyCreateTaskViewModelException = notifyCreateTaskViewModelExceptionData;

    public void loadUsername() {
        if (fAuth.getCurrentUser() != null)
            dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                    if (snapshot.exists()) {
                        String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                        usernameData.postValue(name);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    notifyCreateTaskViewModelExceptionData.postValue(error.toException());
                }
            });
    }

    public void editTask(int taskId, String studentId, TaskModel task) {
        dRef.child("Tasks").child(studentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("Tasks").child(studentId).removeEventListener(this);
                if (snapshot.exists()) {
                    task.setId(taskId);
                    task.setCreatorID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                    dRef.child("Tasks").child(studentId).child("Task" + (task.getId())).setValue(task)
                            .addOnCompleteListener(task1 -> navigateData.postValue(null))
                            .addOnFailureListener(notifyCreateTaskViewModelExceptionData::postValue);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyCreateTaskViewModelExceptionData.postValue(error.toException());
            }
        });
    }

    public void createTask(final String studentId, TaskModel task) {
        dRef.child("Tasks").child(studentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("Tasks").child(studentId).removeEventListener(this);
                if (snapshot.exists()) {
                    task.setId((int) (snapshot.getChildrenCount() + 1));
                    task.setCreatorID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                    dRef.child("Tasks").child(studentId).child("Task" + (task.getId())).setValue(task)
                            .addOnCompleteListener(task1 -> navigateData.postValue(null))
                            .addOnFailureListener(notifyCreateTaskViewModelExceptionData::postValue);
                } else {
                    task.setId(1);
                    task.setCreatorID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                    dRef.child("Tasks").child(studentId).child("Task1").setValue(task)
                            .addOnCompleteListener(task1 -> navigateData.postValue(null))
                            .addOnFailureListener(notifyCreateTaskViewModelExceptionData::postValue);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyCreateTaskViewModelExceptionData.postValue(error.toException());
            }
        });
    }

    public void loadTask(int taskId, String studentId) {
        dRef.child("Tasks").child(studentId).child("Task" + taskId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("Tasks").child(studentId).child("Task" + taskId).removeEventListener(this);
                if (snapshot.exists()) {
                    TaskModel snapTask = snapshot.getValue(TaskModel.class);
                    taskData.postValue(snapTask);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyCreateTaskViewModelExceptionData.postValue(error.toException());
            }
        });
    }

    public void loadUserId() {
        userIdData.postValue(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
    }
}
