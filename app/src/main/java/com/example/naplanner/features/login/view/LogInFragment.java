package com.example.naplanner.features.login.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentLogInBinding;
import com.example.naplanner.features.login.model.AuthModel;
import com.example.naplanner.features.login.viewmodel.LogInViewModel;

public class LogInFragment extends Fragment {

    private FragmentLogInBinding binding;
    private LogInViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLogInBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(LogInViewModel.class);
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
        ((MainActivity) requireActivity()).hideInteractionBars();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void setObservables() {
        viewModel.getLoginResponse().observe(getViewLifecycleOwner(), isStudent -> {
            ((MainActivity) requireActivity()).setupNavigationBar(isStudent);
            viewModel.loadUsername();
        });
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            ((MainActivity) requireActivity()).setupToolbar(username.substring(0, 1).toUpperCase() + username.substring(1));
            Navigation.findNavController(requireView()).navigate(R.id.action_LoginFragment_to_ownTasksFragment);
        });
        viewModel.getNotifyLoginException().observe(getViewLifecycleOwner(), exception -> printMsg(exception.getMessage()));
        viewModel.getNotifyResetPassResponse().observe(getViewLifecycleOwner(), this::printMsg);
    }

    private void setupUI() {
        binding.logInFragmentSendButton.setOnClickListener(view -> {
            if (!getCredentials().getPassword().isEmpty()) viewModel.login(getCredentials());
        });

        binding.logInFragmentLinkToSignUpText.setOnClickListener(view -> Navigation.findNavController(requireView()).navigate(R.id.action_LoginFragment_to_signUpChoiceFragment));
        binding.logInFragmentForgotPasswordTextView.setOnClickListener(resetPassword());
    }

    private boolean validateEmail(CharSequence mail) {
        return !mail.toString().isEmpty() && Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener resetPassword() {
        return view -> {
            EditText mailToSend = new EditText(view.getContext());
            mailToSend.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            AlertDialog.Builder passwordReset = new AlertDialog.Builder(view.getContext());
            passwordReset.setTitle("Restablecer Contraseña?");
            passwordReset.setMessage("Introduzca su correo para obtener un enlace");

            passwordReset.setView(mailToSend);
            passwordReset.setPositiveButton("Enviar", (dialogInterface, i) -> {
                String mail = mailToSend.getText().toString();
                if (!mail.isEmpty()) viewModel.resetPassword(mail);
                else printMsg("Por favor introduzca un correo");
            });
            passwordReset.setNegativeButton("Cancelar", (dialogInterface, i) -> {
            });
            passwordReset.create().show();
        };
    }

    private AuthModel getCredentials() {
        final AuthModel authModel = new AuthModel();

        String input = binding.logInFragmentMailEditText.getText().toString();
        String pass = binding.logInFragmentPasswordEditText.getText().toString();

        if (!input.isEmpty() && validateEmail(input)) authModel.setEmail(input);
        else printMsg("Introduzca un correo valido");

        if (!pass.isEmpty() && !authModel.getEmail().isEmpty()) authModel.setPassword(pass);
        else printMsg("Introduzca una contraseña");

        return authModel;
    }
}