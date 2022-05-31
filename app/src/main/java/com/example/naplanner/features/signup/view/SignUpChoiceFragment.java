package com.example.naplanner.features.signup.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentSignUpChoiceBinding;

public class SignUpChoiceFragment extends Fragment {

    private FragmentSignUpChoiceBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpChoiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupUI() {
        binding.signUpChoiceFragmentProfessorButton.setOnClickListener(view -> Navigation.findNavController(requireView()).navigate(R.id.action_signUpChoiceFragment_to_teacherSignUpFragment));

        binding.signUpChoiceFragmentStudentButton.setOnClickListener(view -> Navigation.findNavController(requireView()).navigate(R.id.action_signUpChoiceFragment_to_studentSignUpFragment));

        binding.signUpChoiceFragmentLogInTextView.setOnClickListener(view -> Navigation.findNavController(requireView()).navigateUp());
    }
}