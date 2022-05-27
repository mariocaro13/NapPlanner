package com.example.naplanner;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class MainActivityViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private final FirebaseDatabase fDatabase = FirebaseDatabase.getInstance(Constants.databaseURL);

    private final MutableLiveData<Void> navigateData = new MutableLiveData<>();
    public final LiveData<Void> navigate = navigateData;

    private final MutableLiveData<Boolean> userIsLoggedData = new MutableLiveData<>();
    public final LiveData<Boolean> userIsLogged = userIsLoggedData;

    private final MutableLiveData<Uri> imageUriData = new MutableLiveData<>();
    public final LiveData<Uri> imageUri = imageUriData;

    private final MutableLiveData<UserModel> userData = new MutableLiveData<>();
    public final LiveData<UserModel> user = userData;

    private final MutableLiveData<Exception> notifyUserLoadExceptionData = new MutableLiveData<>();
    public final LiveData<Exception> notifyUserLoadException = notifyUserLoadExceptionData;

    private final MutableLiveData<Exception> notifyImageLoadExceptionData = new MutableLiveData<>();
    public final LiveData<Exception> notifyImageLoadException = notifyImageLoadExceptionData;

    public void loadImage() {
        fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl()
                .addOnSuccessListener(imageUriData::postValue)
                .addOnFailureListener(notifyImageLoadExceptionData::postValue);
    }

    public void loadUser() {
        fDatabase.getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                fDatabase.getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                if (snapshot.exists()) {
                    userData.postValue(Objects.requireNonNull(snapshot.getValue(UserModel.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyUserLoadExceptionData.postValue(error.toException());
            }
        });
    }

    public void checkUserIsLogged() {
        userIsLoggedData.postValue(fAuth.getCurrentUser() != null);
    }
}
