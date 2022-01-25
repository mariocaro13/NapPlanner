package com.example.naplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.naplanner.databinding.FragmentLogInBinding;
import com.example.naplanner.databinding.FragmentSignUpBinding;
import com.example.naplanner.model.UserModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class SignUpFragment extends Fragment {

    private FragmentSignUpBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }


    private void setupUI(){
        binding.signUpFragmentSendButton.setOnClickListener(new View.OnClickListener() {
            UserModel data = new UserModel();

            @Override
            public void onClick(View view) {

                String username = binding.signUpFragmentFormLayout.signUpFragmentUsernameEditText.getText().toString();
                if(!username.isEmpty())
                    data.setUsername(username);
                else {
                    sendErrorMsg("El Nombre de usuario esta vacio");
                    return;
                }

                String mail = binding.signUpFragmentFormLayout.signUpFragmentMailEditText.getText().toString();
                if(validateEmail(mail))
                    data.setMail(mail);
                else {
                    sendErrorMsg("Por favor introduzca un email valido");
                    return;
                }

                String pass = binding.signUpFragmentFormLayout.signUpFragmentPassEditText.getText().toString();
                String conPass = binding.signUpFragmentFormLayout.signUpFragmentConfirmPassEditText.getText().toString();
                if(pass.isEmpty())
                    sendErrorMsg("Introduzca una contraseña");
                else if(pass.equals(conPass))
                    data.setPass(pass);
                else{
                    sendErrorMsg("Las contraseñas no son iguales");
                }

            }
        });

        binding.signUpFragmentLogInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(requireView()).navigate(R.id.action_signUpFragment_to_FirstFragment);
            }
        });
    }

    private boolean validateEmail(CharSequence mail){
        return  !mail.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private void sendErrorMsg(String error){
        Toast.makeText(requireActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }
}