package com.example.naplanner.features.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentProfileBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;
import com.example.naplanner.utils.BitmapCropper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private int tasksCompleteCount;
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), UploadSelectedImage());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).hideInteractionBars();
        setupUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.profileFragmentBackButton.setOnClickListener(view1 -> Navigation.findNavController(requireView()).navigateUp());
        binding.profileFragmentLogOutButton.setOnClickListener(v -> {
            fAuth.signOut();
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_LoginFragment);
        });
    }

    public void setupUI() {

        countCompleteTasks();
        loadImage();

        binding.profileFragmentUserMailTextView.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());
        binding.fragmentProfileResetPasswordTextView.setOnClickListener(updatePassword());
        binding.profileFragmentAppIconImageView.setOnClickListener(selectAndUploadImage());
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

    public void countCompleteTasks() {
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
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

    private View.OnClickListener updatePassword() {
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
                        Toast.makeText(ProfileFragment.this.getContext(), "Contraseña Cambiada Correctamente", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(ProfileFragment.this.getContext(), "Un Error Ha Ocurrido", Toast.LENGTH_SHORT).show();
                });
            });
            passwordReset.setNegativeButton("Cancelar", (dialogInterface, i) -> {
            });

            passwordReset.create().show();
        };
    }

    private void loadImage() {
        fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(requireContext())
                    .asBitmap()
                    .load(uri)
                    .fitCenter()
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            BitmapDrawable croppedResource = new BitmapDrawable(getResources(), BitmapCropper.getRoundCroppedBitmap(resource));
                            binding.profileFragmentAppIconImageView.setImageDrawable(croppedResource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        });
    }


    private ActivityResultCallback<Uri> UploadSelectedImage() {
        return uri -> {
            if(uri != null) {
                UploadTask uploadTask = fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).putFile(uri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(ProfileFragment.this.getContext(), "Imagen Subida Correctamente", Toast.LENGTH_SHORT).show();
                    loadImage();
                }).addOnFailureListener(e -> {
                    Toast.makeText(ProfileFragment.this.getContext(), "Fallo en la carga de la Imagen", Toast.LENGTH_SHORT).show();
                    Log.d("Image Load Failure: ", e.getMessage());
                });
            }
        };
    }

    private View.OnClickListener selectAndUploadImage() {
        return view -> mGetContent.launch("image/*");
    }
}
