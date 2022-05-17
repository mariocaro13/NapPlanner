package com.example.naplanner.features.profile.viewmodel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.net.Uri;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.TaskModel;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private final MutableLiveData<Void> navigate = new MutableLiveData<>();
    private final MutableLiveData<Integer> completedTaskCount = new MutableLiveData<>();
    private final MutableLiveData<Uri> imageUri = new MutableLiveData<>();
    private final MutableLiveData<String> username = new MutableLiveData<>();
    private final MutableLiveData<Void> uploadSelectedImageResponse = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifyProfileException = new MutableLiveData<>();


    public void loadCompleteTaskCount() {
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            int tasksCompleteCount = 0;

            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TaskModel task = dataSnapshot.getValue(TaskModel.class);

                    if (Objects.requireNonNull(task).isComplete())
                        tasksCompleteCount++;
                }
                completedTaskCount.postValue(tasksCompleteCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyProfileException.postValue(error.toException());
            }
        });

    }

    public View.OnClickListener updatePassword() {
        return view -> {
            EditText newPassword = new EditText(view.getContext());
            newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
            passwordReset.setTitle("Cambiar Contraseña?");
            passwordReset.setMessage("Introduzca una nueva contraseña (minimo 6 de longitud)");
            passwordReset.setView(newPassword);

            passwordReset.setPositiveButton("Confirmar", (dialogInterface, i) -> {
                String password = newPassword.getText().toString();
                Objects.requireNonNull(fAuth.getCurrentUser()).updatePassword(password).addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                        Toast.makeText(view.getContext(), "Contraseña Cambiada Correctamente", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(view.getContext(), "Un Error Ha Ocurrido", Toast.LENGTH_SHORT).show();
                });
            });
            passwordReset.setNegativeButton("Cancelar", (dialogInterface, i) -> {
            });

            passwordReset.create().show();
        };
    }

    public void loadImage() {
        fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl()
                .addOnSuccessListener(imageUri::postValue)
                .addOnFailureListener(notifyProfileException::postValue);
    }

    public void loadUsername(){
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username.postValue(Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                notifyProfileException.postValue(error.toException());
            }
        });
    }

    public void logout(){
        fAuth.signOut();
    }

    public void uploadSelectedImage(Uri uri) {
            if (uri != null) {
                UploadTask uploadTask = fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).putFile(uri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    uploadSelectedImageResponse.postValue(null);
                    loadImage();
                }).addOnFailureListener(notifyProfileException::postValue);
            }
    }

    public FirebaseUser getUser() {
        return fAuth.getCurrentUser();
    }
    public MutableLiveData<Void> getNavigate() {
        return navigate;
    }
    public MutableLiveData<Integer> getCompletedTaskCount() {
        return completedTaskCount;
    }
    public MutableLiveData<Uri> getImageUri() {
        return imageUri;
    }
    public MutableLiveData<String> getUsername() {
        return username;
    }
    public MutableLiveData<Void> getUploadSelectedImageResponse() {
        return uploadSelectedImageResponse;
    }
    public MutableLiveData<Exception> getNotifyProfileException() {
        return notifyProfileException;
    }
}
