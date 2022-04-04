package com.example.naplanner.features.signup;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentSignUpBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;
    private FirebaseAuth fAuth;
    private UserModel data = new UserModel();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
        if (SignUpFragmentArgs.fromBundle(getArguments()).getIsStudent()) {
            useStudentPalette();
            data.setStudent((true));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        fAuth = FirebaseAuth.getInstance();
    }

    private void setupUI() {
        binding.signUpFragmentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = binding.signUpFragmentFormLayout.signUpFragmentUsernameEditText.getText().toString();
                if (username.length() > 18) {
                    sendMsg("Nombre muy largo (Maximo 18)");
                    return;
                } else if (!username.isEmpty()) data.setUsername(username);
                else {
                    sendMsg("El Nombre de usuario esta vacio");
                    return;
                }

                String mail = binding.signUpFragmentFormLayout.signUpFragmentMailEditText.getText().toString();
                if (validateEmail(mail)) data.setMail(mail);
                else {
                    sendMsg("Por favor introduzca un email valido");
                    return;
                }

                String pass = binding.signUpFragmentFormLayout.signUpFragmentPassEditText.getText().toString();
                String conPass = binding.signUpFragmentFormLayout.signUpFragmentConfirmPassEditText.getText().toString();
                if (pass.isEmpty()) sendMsg("Introduzca una contraseña");
                else if (pass.equals(conPass))
                    fAuth.createUserWithEmailAndPassword(data.getMail(), pass).addOnCompleteListener(authComplete(data));
                else {
                    sendMsg("Las contraseñas no son iguales");
                }
            }
        });

        binding.signUpFragmentLogInTextView.setOnClickListener(view -> Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_FirstFragment));
    }

    private void useStudentPalette() {
        int bgColorID = getResources().getColor(R.color.background_green, null);
        int lightColorID = getResources().getColor(R.color.light_green, null);
        int darkColorID = getResources().getColor(R.color.dark_green, null);
        binding.getRoot().setBackgroundColor(bgColorID);

        binding.signUpFragmentFormLayout.signUpFragmentUsernameTextView.setBackgroundColor(darkColorID);
        binding.signUpFragmentFormLayout.signUpFragmentMailTextView.setBackgroundColor(darkColorID);
        binding.signUpFragmentFormLayout.signUpFragmentPassTextView.setBackgroundColor(darkColorID);
        binding.signUpFragmentFormLayout.signUpFragmentConfirmPassTextView.setBackgroundColor(darkColorID);

        binding.signUpFragmentSendButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), R.color.dark_green));

        binding.signUpFragmentFormLayout.signUpFragmentUsernameEditText.setBackgroundColor(lightColorID);
        binding.signUpFragmentFormLayout.signUpFragmentMailEditText.setBackgroundColor(lightColorID);
        binding.signUpFragmentFormLayout.signUpFragmentPassEditText.setBackgroundColor(lightColorID);
        binding.signUpFragmentFormLayout.signUpFragmentConfirmPassEditText.setBackgroundColor(lightColorID);

    }

    private boolean validateEmail(CharSequence mail) {
        return !mail.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private void sendMsg(String error) {
        Toast.makeText(requireActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

    private OnCompleteListener<AuthResult> authComplete(final UserModel user) {
        return new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    //Send verification mail
                    Objects.requireNonNull(fAuth.getCurrentUser()).sendEmailVerification();

                    sendMsg("Correctly Signed in");
                    user.setuID(Objects.requireNonNull(fAuth.getCurrentUser()).getUid());
                    FirebaseDatabase.getInstance(Constants.databaseURL).getReference("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_teacherTasksFragment);
                            } else sendMsg(Objects.requireNonNull(task.getException()).getMessage());
                        }
                    });

                } else {
                    sendMsg(Objects.requireNonNull(task.getException()).getMessage());
                }
            }
        };
    }
}