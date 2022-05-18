package com.example.naplanner.features.main.studentlist.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class StudentListViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();

    private final MutableLiveData<Void> navigate = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<UserModel>> students = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifyStudentListException = new MutableLiveData<>();

    public void loadStudents() {
        dRef.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dRef.child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                ArrayList<UserModel> users = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UserModel user = dataSnapshot.getValue(UserModel.class);

                    if (Objects.requireNonNull(user).getStudent()) {
                        String name = Objects.requireNonNull(user).getUsername();
                        user.setUsername(name.substring(0, 1).toUpperCase() + name.substring(1));
                        users.add(user);
                    }
                }
                students.postValue(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyStudentListException.postValue(error.toException());
            }
        });
    }

    public MutableLiveData<Void> getNavigate() {
        return navigate;
    }

    public MutableLiveData<ArrayList<UserModel>> getStudents() {
        return students;
    }
    public MutableLiveData<Exception> getNotifyStudentListException() {
        return notifyStudentListException;
    }
}
