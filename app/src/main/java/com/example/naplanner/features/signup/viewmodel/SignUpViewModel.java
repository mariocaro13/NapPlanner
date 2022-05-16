package com.example.naplanner.features.signup.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpViewModel extends ViewModel {

    private final MutableLiveData<Void> navigate = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifySignUpException = new MutableLiveData<>();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public void signUp(final UserModel userModel, String pass) {
        fAuth.createUserWithEmailAndPassword(userModel.getMail(), pass).addOnCompleteListener(authComplete(userModel));
    }

    private OnCompleteListener<AuthResult> authComplete(final UserModel userModel) {
        return task -> {
            if (task.isSuccessful()) {

                Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification();

                userModel.setuID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).setValue(userModel).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) navigate.postValue(null);
                });

            } else
                notifySignUpException.postValue(new Exception(Objects.requireNonNull(task.getException()).getMessage()));
        };
    }

    public MutableLiveData<Void> getNavigate() {
        return navigate;
    }

    public MutableLiveData<Exception> getNotifySignUpException() {
        return notifySignUpException;
    }
}
