package com.example.naplanner;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.naplanner.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActionBar bottomBar;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        bottomBar = getSupportActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.bottom_menu_own_task) {
            //TODO: Navigate to Own Tasks
            Toast.makeText(getApplicationContext(), "TODO: Navigate to Own Tasks", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.bottom_menu_completed_tasks){
            //TODO: Navigate to Completed Tasks
            Toast.makeText(getApplicationContext(), "TODO: Navigate to Completed Tasks", Toast.LENGTH_SHORT).show();
            return true;
        }else if(id == R.id.bottom_menu_student_list){
            //TODO: Navigate to Students List
            Toast.makeText(getApplicationContext(), "TODO: Navigate to Students List", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}