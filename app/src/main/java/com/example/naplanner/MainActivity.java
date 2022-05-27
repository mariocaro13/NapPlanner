package com.example.naplanner;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.naplanner.databinding.ActivityMainBinding;
import com.example.naplanner.utils.BitmapCropper;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainActivityViewModel viewModel;

    private AppBarConfiguration appBarConfiguration;
    private NavController navController;

    private MenuItem iconItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.activityMainToolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel.checkUserIsLogged();
        setObservables(iconItem);
        setupToolbar("Login");
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    // Toolbar Config
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }


    public void setupToolbar(String title) {
        binding.activityMainToolbar.setTitle(title);
        iconItem = binding.activityMainBottomNav.getMenu().findItem(R.id.profileFragment);
        //NavigationUI.setupWithNavController(binding.activityMainToolbar, navController);
    }

    public void setupNavigationBar(boolean isStudent) {
        binding.activityMainBottomNav.getMenu().clear();
        if(isStudent)
            binding.activityMainBottomNav.inflateMenu(R.menu.bottom_nav_menu_student);
        else
            binding.activityMainBottomNav.inflateMenu(R.menu.bottom_nav_menu_teacher);

        NavigationUI.setupWithNavController(binding.activityMainBottomNav, navController);
    }

    private void setObservables(MenuItem menuItem) {

        viewModel.userIsLogged.observe(this, aBoolean -> {
            if (aBoolean)
                loadUserInfo();
            else
                navController.navigate(R.id.loginFragment, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build());
        });

        viewModel.user.observe(this, user -> {
            setupToolbar(user.getUsername());
            setupNavigationBar(user.getStudent());
        });

        viewModel.imageUri.observe(this, uri -> {
            if (uri != null)
                setUserImage(uri, menuItem);
        });

        viewModel.notifyUserLoadException.observe(this,
                exception -> printMsg(exception.getMessage()));

        viewModel.notifyImageLoadException.observe(this,
                exception -> printMsg(exception.getMessage()));
    }

    public void loadUserInfo() {
        viewModel.loadUser();
        viewModel.loadImage();
    }

    private void setUserImage(Uri uri, MenuItem profileItem) {
        Glide.with(getApplicationContext())
                .asBitmap()
                .load(uri)
                .fitCenter()
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        BitmapDrawable croppedResource = new BitmapDrawable(getResources(), BitmapCropper.getRoundCroppedBitmap(resource));
                        if (profileItem != null)
                            profileItem.setIcon(croppedResource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
    }

    // Helper Functions to hide and Show the Navigation Bars
    public void showInteractionBars() {
        binding.activityMainBottomNav.setVisibility(View.VISIBLE);
        ConstraintLayout constraintLayout = binding.getRoot();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.activity_main_bottom_nav, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.clear(R.id.activity_main_bottom_nav, ConstraintSet.TOP);
        constraintSet.applyTo(constraintLayout);
        binding.activityMainToolbar.setVisibility(View.VISIBLE);
    }

    public void hideInteractionBars() {
        binding.activityMainBottomNav.setVisibility(View.GONE);
        ConstraintLayout constraintLayout = binding.getRoot();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.activity_main_bottom_nav, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, 0);
        constraintSet.clear(R.id.activity_main_bottom_nav, ConstraintSet.BOTTOM);
        constraintSet.applyTo(constraintLayout);

        binding.activityMainToolbar.setVisibility(View.GONE);
    }
    private void printMsg(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
}
}