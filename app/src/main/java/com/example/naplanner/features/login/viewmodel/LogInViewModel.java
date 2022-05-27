package com.example.naplanner.features.login.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.features.login.model.AuthModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LogInViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private final MutableLiveData<Void> navigateData = new MutableLiveData<>();
    public final LiveData<Void> navigate = navigateData;

    private final MutableLiveData<Exception> notifyLoginExceptionData = new MutableLiveData<>();
    public final LiveData<Exception> notifyLoginException = notifyLoginExceptionData;

    private final MutableLiveData<String> notifyResetPassResponseData = new MutableLiveData<>();
    public final LiveData<String> notifyResetPassResponse = notifyResetPassResponseData;

    public void login(AuthModel authModel) {
        fAuth.signInWithEmailAndPassword(authModel.getEmail(), authModel.getPassword())
                .addOnSuccessListener(authResult -> {
                    checkEmailVerified();
                }).addOnFailureListener(notifyLoginExceptionData::postValue);
    }

    public void resetPassword(String email) {
        fAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> notifyResetPassResponseData.postValue("Enlace Enviado"))
                .addOnFailureListener(error -> notifyResetPassResponseData.postValue(error.getMessage()));
    }

    private void checkEmailVerified() {
        if (Objects.requireNonNull(fAuth.getCurrentUser()).isEmailVerified())
            navigateData.postValue(null);
        else
            notifyLoginExceptionData.postValue(new Exception("Verifique su correo"));
    }

}
