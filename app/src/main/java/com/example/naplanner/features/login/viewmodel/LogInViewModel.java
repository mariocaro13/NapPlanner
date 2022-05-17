package com.example.naplanner.features.login.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.features.login.model.AuthModel;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LogInViewModel extends ViewModel {

    private final MutableLiveData<Boolean> loginResponse = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifyLoginException = new MutableLiveData<>();
    private final MutableLiveData<String> notifyResetPassResponse = new MutableLiveData<>();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseDatabase fDatabase = FirebaseDatabase.getInstance(Constants.databaseURL);

    public void login(AuthModel authModel) {
        fAuth.signInWithEmailAndPassword(authModel.getEmail(), authModel.getPassword())
                .addOnSuccessListener(authResult -> {
                    checkEmailVerified();
                }).addOnFailureListener(notifyLoginException::postValue);
    }

    public void resetPassword(String email) {
        fAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> notifyResetPassResponse.postValue("Enlace Enviado"))
                .addOnFailureListener(error -> notifyResetPassResponse.postValue(error.getMessage()));
    }

    private void checkEmailVerified() {
        if (Objects.requireNonNull(fAuth.getCurrentUser()).isEmailVerified())
            getUserType();
        else
            notifyLoginException.postValue(new Exception("Verifique su correo"));
    }

    private void getUserType(){
        fDatabase.getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginResponse.postValue(Objects.requireNonNull(snapshot.getValue(UserModel.class)).getStudent());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyLoginException.postValue(error.toException());
            }
        });
    }

    public MutableLiveData<Boolean> getLoginResponse() {
        return loginResponse;
    }

    public MutableLiveData<Exception> getNotifyLoginException() {
        return notifyLoginException;
    }

    public MutableLiveData<String> getNotifyResetPassResponse() {
        return notifyResetPassResponse;
    }
}
