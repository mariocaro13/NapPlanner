package com.example.naplanner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

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

    private void setupUI(){
        binding.signUpChoiceFragmentProfessorButton.setOnClickListener(choiceButtonListener(1));

        binding.signUpChoiceFragmentStudentButton.setOnClickListener(choiceButtonListener(2));

        binding.signUpChoiceFragmentLogInTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Navigation.findNavController(requireView()).navigateUp();
            }
        });
    }


    private View.OnClickListener choiceButtonListener(int choice) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignUpChoiceFragmentDirections.ActionSignUpChoiceFragmentToSignUpFragment action = SignUpChoiceFragmentDirections.actionSignUpChoiceFragmentToSignUpFragment(choice);
                Navigation.findNavController(requireView()).navigate(action);
            }
        };
    }
}