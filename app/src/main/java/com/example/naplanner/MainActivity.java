package com.example.naplanner;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.naplanner.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupNavigationBar(false);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        setupToolbar("Login");
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
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
        int id = item.getItemId();
        if (id == R.id.action_add_task) {
            navController.navigate(R.id.taskForm);
            return true;
        } else if (id == R.id.action_profile) {
            navController.navigate(R.id.profileFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setupToolbar(String title) {
        binding.activityMainToolbar.setTitle(title);
        setSupportActionBar(binding.activityMainToolbar);
    }

    // Bottom Navigation Bar Config
    public void setupNavigationBar(boolean isStudent) {
        binding.activityMainBottomNav.setSelectedItemId(R.id.bottom_menu_own_task);
        if (!isStudent)
            binding.activityMainBottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bottom_menu_own_task) {
                        navController.navigate(R.id.ownTasksFragment);
                        return true;
                    } else if (id == R.id.bottom_menu_completed_tasks) {
                        navController.navigate(R.id.completeTasksFragment);
                        return true;
                    } else if (id == R.id.bottom_menu_student_list) {
                        navController.navigate(R.id.studentListFragment);
                        return true;
                    }
                    return true;
                }
            });
        else
            binding.activityMainBottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.bottom_menu_own_task) {
                        navController.navigate(R.id.ownTasksFragment);
                        return true;
                    } else if (id == R.id.bottom_menu_completed_tasks) {
                        navController.navigate(R.id.completeTasksFragment);
                        return true;
                    } else if (id == R.id.bottom_menu_student_list) {
                        navController.navigate(R.id.teacherTasksFragment);
                        return true;
                    }
                    return true;
                }
            });

        binding.activityMainBottomNav.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
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
}