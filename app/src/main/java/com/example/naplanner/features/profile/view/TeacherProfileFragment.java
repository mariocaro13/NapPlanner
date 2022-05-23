package com.example.naplanner.features.profile.view;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.naplanner.MainActivity;
import com.example.naplanner.R;
import com.example.naplanner.databinding.FragmentProfileTeacherBinding;
import com.example.naplanner.features.profile.viewmodel.ProfileViewModel;
import com.example.naplanner.utils.BitmapCropper;

import java.util.Objects;

public class TeacherProfileFragment extends Fragment {

    private FragmentProfileTeacherBinding binding;
    private ProfileViewModel viewModel;
    private final ActivityResultLauncher<String> imagePickerLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), UploadSelectedImage());

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileTeacherBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        return binding.getRoot();
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
        ((MainActivity) requireActivity()).hideInteractionBars();
        setupUI();
    }

    public void setupUI() {
        viewModel.loadCompleteTaskCount();
        viewModel.loadImage();
        viewModel.loadUsername();

        binding.profileFragmentUserMailTextView.setText(Objects.requireNonNull(viewModel.getUser().getEmail()));
        binding.fragmentProfileResetPasswordTextView.setOnClickListener(viewModel.updatePassword());
        binding.profileFragmentAppIconImageView.setOnClickListener(getPhotoFromImagePicker());
        binding.profileFragmentBackButton.setOnClickListener(view1 -> Navigation.findNavController(requireView()).navigateUp());
        binding.profileFragmentLogOutButton.setOnClickListener(v -> {
            viewModel.logout();
            Navigation.findNavController(requireView()).navigate(R.id.loginFragment);
        });
    }

    private ActivityResultCallback<Uri> UploadSelectedImage() {
        return uri -> {
            if (uri != null) {
                viewModel.uploadSelectedImage(uri);
            }
        };
    }

    private View.OnClickListener getPhotoFromImagePicker() {
        return view -> imagePickerLauncher.launch("image/*");
    }

    private void setObservables() {
        viewModel.getNavigate().observe(getViewLifecycleOwner(),
                unused -> Navigation.findNavController(requireView()).navigate(R.id.action_LoginFragment_to_ownTasksFragment));
        viewModel.getUsername().observe(getViewLifecycleOwner(),
                name -> {
                    String shortenedString = name.substring(0, 1).toUpperCase() + name.substring(1);
                    binding.profileFragmentUserNameTextView.setText(shortenedString);
                });
        viewModel.getImageUri().observe(getViewLifecycleOwner(), this::setUserImage);
        viewModel.getCompletedTaskCount().observe(getViewLifecycleOwner(),
                completedTaskCount -> binding.profileFragmentTasksCountTextView.setText(String.valueOf(completedTaskCount)));
        viewModel.getUploadSelectedImageResponse().observe(getViewLifecycleOwner(),
                unused -> printMsg("Imagen Subida Correctamente"));
        viewModel.getNotifyProfileException().observe(getViewLifecycleOwner(),
                exception -> printMsg(exception.getMessage()));
    }

    private void printMsg(String msg) {
        Toast.makeText(requireActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void setUserImage(Uri uri) {
        Glide.with(requireActivity())
                .asBitmap()
                .load(uri)
                .fitCenter()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        BitmapDrawable croppedResource = new BitmapDrawable(getResources(), BitmapCropper.getRoundCroppedBitmap(resource));
                        binding.profileFragmentAppIconImageView.setImageDrawable(croppedResource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }
}
