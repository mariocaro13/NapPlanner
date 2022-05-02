package com.example.naplanner.features.login.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.features.login.model.AuthModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInViewModel extends ViewModel {

    private final MutableLiveData<Void> navigate = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifyLoginException = new MutableLiveData<>();
    private final MutableLiveData<String> notifyResetPassResponse = new MutableLiveData<>();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public void login(AuthModel authModel){
        fAuth.signInWithEmailAndPassword(authModel.getEmail(), authModel.getPassword())
                .addOnSuccessListener(authResult -> {
                    checkEmailVerified();
                }).addOnFailureListener(notifyLoginException::postValue);
    }

    public void resetPassword(String email){
        fAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful())
                notifyResetPassResponse.postValue("Enlace Enviado");
            else
                notifyResetPassResponse.postValue("Un Error Ha Ocurrido");
        });
    }

    private void checkEmailVerified(){
        if(Objects.requireNonNull(fAuth.getCurrentUser()).isEmailVerified())
            navigate.postValue(null);
        else
            notifyLoginException.postValue(new Exception("Verifique su correo"));
    }

    public MutableLiveData<Void> getNavigate() {
        return navigate;
    }
    public MutableLiveData<Exception> getNotifyLoginException() {
        return notifyLoginException;
    }
    public MutableLiveData<String> getNotifyResetPassResponse() {
        return notifyResetPassResponse;
    }
}
