package com.example.naplanner.features.signup.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final DatabaseReference dRef = FirebaseDatabase.getInstance(Constants.databaseURL).getReference();

    private final MutableLiveData<Void> navigateData = new MutableLiveData<>();
    public final LiveData<Void> navigate = navigateData;

    private final MutableLiveData<Exception> notifySignUpExceptionData = new MutableLiveData<>();
    public final LiveData<Exception> notifySignUpException = notifySignUpExceptionData;

    public void signUp(final UserModel userModel, String pass) {
        fAuth.createUserWithEmailAndPassword(userModel.getMail(), pass).addOnCompleteListener(authComplete(userModel));
    }

    private OnCompleteListener<AuthResult> authComplete(final UserModel userModel) {
        return task -> {
            if (task.isSuccessful()) {

                Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification();

                userModel.setuID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                dRef.child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).setValue(userModel).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) navigateData.postValue(null);
                });

            } else
                notifySignUpExceptionData.postValue(new Exception(Objects.requireNonNull(task.getException()).getMessage()));
        };
    }
}
