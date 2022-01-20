package com.example.naplanner;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naplanner.databinding.FragmentLogInBinding;
import com.example.naplanner.databinding.FragmentSignUpBinding;
import com.example.naplanner.model.UserModel;

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

        binding.signUpFragmentSendUp.setOnClickListener(new View.OnClickListener() {
            UserModel data = new UserModel();

            @Override
            public void onClick(View view) {
                data.setMail(binding.signUpFragmentFormLayout.signUpFragmentMailTextView.getText().toString());

            }
        });
    }

    private boolean validateEmail(CharSequence mail){
        //return Patterns.EMAIL_ADDRESS.matcher(mail).
    }
}