package com.example.naplanner;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.naplanner.databinding.FragmentLogInBinding;
import com.example.naplanner.model.UserModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class LogInFragment extends Fragment {

    private FragmentLogInBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    @Override
    public void onStart() {
        super.onStart();
        //setupNavBar(false);
    }

    private void setupNavBar(Boolean isVisible) {
        int visibility = isVisible? View.VISIBLE : View.INVISIBLE;
        BottomNavigationView navBar = getActivity().findViewById(R.id.activity_main_bottom_nav);
        navBar.setVisibility(visibility);
    }

    private void setupUI() {
        binding.logInFragmentSendButton.setOnClickListener(new View.OnClickListener() {
            UserModel data = new UserModel();

            @Override
            public void onClick(View view) {

                String input = binding.logInFragmentMailEditText.getText().toString();
                if (!input.isEmpty() && validateEmail(input)) {
                    data.setMail(input);
                } else if (!input.isEmpty()) {
                    data.setUsername(input);
                } else {
                    sendErrorMsg("El Nombre de usuario esta vacio");
                    return;
                }

                String pass = binding.logInFragmentPasswordEditText.getText().toString();
                if (!pass.isEmpty())
                    data.setPass(pass);
                else {
                    sendErrorMsg("Introduzca una contraseña");
                }

            }
        });

        binding.logInFragmentLinkToSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(requireView()).navigate(R.id.action_FirstFragment_to_signUpChoiceFragment);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private boolean validateEmail(CharSequence mail) {
        return !mail.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private void sendErrorMsg(String error) {
        Toast.makeText(requireActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
    }

}