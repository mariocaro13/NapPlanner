package com.example.naplanner.features.signup.view;

import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentSignUpStudentBinding;
import com.example.naplanner.features.signup.viewmodel.SignUpViewModel;
import com.example.naplanner.models.UserModel;

public class StudentSignUpFragment extends Fragment {

    private final UserModel userModel = new UserModel();
    private FragmentSignUpStudentBinding binding;
    private SignUpViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpStudentBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setObservables();
        setupUI();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void setupUI() {
        binding.signUpFragmentSendButton.setOnClickListener(view -> setData());
        binding.signUpFragmentLogInTextView.setOnClickListener(view -> Navigation.findNavController(requireView()).navigate(R.id.action_studentSignUpFragment_to_LogInFragment));
    }

    private void setData() {
        String username = binding.signUpFragmentFormLayout.signUpFragmentUsernameEditText.getText().toString();
        if (username.length() > 18) {
            printMsg("Nombre muy largo (Maximo 18)");
            return;
        } else if (!username.isEmpty()) userModel.setUsername(username);
        else {
            printMsg("El Nombre de usuario esta vacio");
            return;
        }
        String mail = binding.signUpFragmentFormLayout.signUpFragmentMailEditText.getText().toString();
        if (validateEmail(mail)) userModel.setMail(mail);
        else {
            printMsg("Por favor introduzca un email valido");
            return;
        }

        String pass = binding.signUpFragmentFormLayout.signUpFragmentPassEditText.getText().toString();
        String conPass = binding.signUpFragmentFormLayout.signUpFragmentConfirmPassEditText.getText().toString();
        if (pass.isEmpty()) printMsg("Introduzca una contraseña");
        else if (pass.equals(conPass))
            viewModel.signUp(userModel, pass);
        else printMsg("Las contraseñas no son iguales");
    }

    private void setObservables() {
        viewModel.navigate.observe(getViewLifecycleOwner(),
                unused -> Navigation.findNavController(requireView()).navigate(R.id.action_teacherSignUpFragment_to_teacherTasksFragment));
        viewModel.notifySignUpException.observe(getViewLifecycleOwner(),
                exception -> printMsg(exception.getMessage()));
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private boolean validateEmail(CharSequence mail) {
        return !mail.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }
}