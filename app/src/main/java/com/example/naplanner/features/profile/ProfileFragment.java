package com.example.naplanner.features.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentProfileBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth fAuth;
    private int tasksCompleteCount;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).hideInteractionBars();
        fAuth = FirebaseAuth.getInstance();
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
        binding.profileFragmentLogOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_LoginFragment);
            }
        });
    }

    public void setupUI() {

        countCompleteTasks();

        binding.profileFragmentUserMailTextView.setText(Objects.requireNonNull(fAuth.getCurrentUser()).getEmail());
        binding.fragmentProfileResetPasswordTextView.setOnClickListener(updatePassword());
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
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText newPassword = new EditText(view.getContext());
                newPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
                passwordReset.setTitle("Cambiar Contraseña?");
                passwordReset.setMessage("Introduzca una nueva contraseña (minimo 6 de longitud)");
                passwordReset.setView(newPassword);

                passwordReset.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = newPassword.getText().toString();
                        Objects.requireNonNull(fAuth.getCurrentUser()).updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful())
                                    Toast.makeText(ProfileFragment.this.getContext(), "Contraseña Cambiada Correctamente", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(ProfileFragment.this.getContext(), "Un Error Ha Ocurrido", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordReset.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

                passwordReset.create().show();
            }
        };
    }
}