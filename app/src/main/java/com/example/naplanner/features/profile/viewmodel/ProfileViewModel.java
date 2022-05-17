package com.example.naplanner.features.profile.viewmodel;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.naplanner.databinding.FragmentProfileBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.TaskModel;
import com.example.naplanner.models.UserModel;
import com.example.naplanner.utils.BitmapCropper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.Objects;

public class ProfileViewModel extends ViewModel {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private final MutableLiveData<Void> navigate = new MutableLiveData<>();
    private final MutableLiveData<Exception> notifyProfileException = new MutableLiveData<>();

    public void countCompleteTasks(FragmentProfileBinding binding) {
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
                binding.profileFragmentTasksCountTextView.setText(Integer.toString(tasksCompleteCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
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

    public void loadImage(FragmentProfileBinding binding, Context context, Resources resources) {
        fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl().addOnSuccessListener(uri ->
                Glide.with(context)
                        .asBitmap()
                        .load(uri)
                        .fitCenter()
                        .into(new CustomTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                BitmapDrawable croppedResource = new BitmapDrawable(resources, BitmapCropper.getRoundCroppedBitmap(resource));
                                binding.profileFragmentAppIconImageView.setImageDrawable(croppedResource);
                            }

                            @Override
                            public void onLoadCleared(@Nullable Drawable placeholder) {
                            }
                        }));
    }

    public void getInstance(FragmentProfileBinding binding){
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    binding.profileFragmentUserNameTextView.setText(name.substring(0, 1).toUpperCase() + name.substring(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public FirebaseUser getUser() {
        return fAuth.getCurrentUser();
    }


    public MutableLiveData<Void> getNavigate() {
        return navigate;
    }
}
