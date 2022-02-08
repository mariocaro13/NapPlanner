package com.example.naplanner;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        setupToolbar();
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupNavigationBar();
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

        if(id == R.id.action_add_task){
            //TODO: NAVIGATE TO CREATE TASK SCREEN
            Log.d("Action Check: ", "TODO: Navigate to add Task Screen");
            return true;
        }else if(id == R.id.action_profile){
            //TODO: NAVIGATE TO PROFILE SCREEN
            Log.d("Action Check: ", "TODO: Navigate to Profile Screen");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar(){
        binding.activityMainToolbar.setTitle("Login");
        setSupportActionBar(binding.activityMainToolbar);
    }


    // Bottom Navigation Bar Config

    private void setupNavigationBar() {
        binding.activityMainBottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.bottom_menu_own_task) {
                    //TODO: NAVIGATE TO OWN TASKS
                    Log.d("Navigation Check: ", "TODO: Navigate to Own Tasks");
                    return true;
                } else if (id == R.id.bottom_menu_completed_tasks) {
                    //TODO: NAVIGATE TO COMPLETED TASKS
                    Log.d("Navigation Check: ", "TODO: Navigate to Completed Tasks");
                    return true;
                } else if (id == R.id.bottom_menu_student_list) {
                    //TODO: NAVIGATE TO STUDENTS LISTS
                    Log.d("Navigation Check: ", "TODO: Navigate to Students List");
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