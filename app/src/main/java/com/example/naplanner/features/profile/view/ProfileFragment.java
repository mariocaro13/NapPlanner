package com.example.naplanner.features.profile.view;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentProfileBinding;
import com.example.naplanner.features.profile.viewmodel.ProfileViewModel;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();
    private FragmentProfileBinding binding;
    private ProfileViewModel viewModel;
    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), UploadSelectedImage());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
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
        setObservables();
        binding.profileFragmentBackButton.setOnClickListener(view1 -> Navigation.findNavController(requireView()).navigateUp());
        binding.profileFragmentLogOutButton.setOnClickListener(v -> {
            fAuth.signOut();
            Navigation.findNavController(requireView()).navigate(R.id.action_profileFragment_to_LoginFragment);
        });
    }

    public void setupUI() {
        viewModel.countCompleteTasks(binding);
        viewModel.loadImage(binding, requireContext(), getResources());

        binding.profileFragmentUserMailTextView.setText(Objects.requireNonNull(viewModel.getUser().getEmail()));
        binding.fragmentProfileResetPasswordTextView.setOnClickListener(viewModel.updatePassword());
        binding.profileFragmentAppIconImageView.setOnClickListener(selectAndUploadImage());

        viewModel.getInstance(binding);
    }

    private ActivityResultCallback<Uri> UploadSelectedImage() {
        return uri -> {
            if (uri != null) {
                UploadTask uploadTask = fStorage.getReference().child("/users/" + Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).putFile(uri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    Toast.makeText(ProfileFragment.this.getContext(), "Imagen Subida Correctamente", Toast.LENGTH_SHORT).show();
                    viewModel.loadImage(binding, requireContext(), getResources());
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

    private void setObservables() {
        viewModel.getNavigate().observe(getViewLifecycleOwner(),
                unused -> Navigation.findNavController(requireView()).navigate(R.id.action_LoginFragment_to_ownTasksFragment));

    }
}
