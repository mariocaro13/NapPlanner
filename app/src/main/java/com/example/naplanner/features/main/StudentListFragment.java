package com.example.naplanner.features.main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.naplanner.MainActivity;
import com.example.naplanner.adapter.StudentListRecycleAdapter;
import com.example.naplanner.databinding.FragmentStudentListBinding;
import com.example.naplanner.helperclasses.Constants;
import com.example.naplanner.model.TaskModel;
import com.example.naplanner.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class StudentListFragment extends Fragment {

    private FragmentStudentListBinding binding;
    private FirebaseAuth fAuth;
    public ArrayList<UserModel> users = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentStudentListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        ((MainActivity) requireActivity()).showInteractionBars();
        fAuth = FirebaseAuth.getInstance();
        setupUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupRecyclerView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setupRecyclerView() {
        binding.studentListFragmentTasksListRecycleview.setHasFixedSize(true);
        users = new ArrayList<>();
        StudentListRecycleAdapter adapter = new StudentListRecycleAdapter(users, getContext());
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("Tasks").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).removeEventListener(this);
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {

                        UserModel user = dataSnapshot.getValue(UserModel.class);

                        if (Objects.requireNonNull(user).getStudent()) {
                            String name = Objects.requireNonNull(user).getUsername();
                            user.setUsername(name.substring(0, 1).toUpperCase() + name.substring(1));
                            users.add(user);
                            adapter.notifyItemInserted(users.size());
                        }

                    }, 300);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.studentListFragmentTasksListRecycleview.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));
        binding.studentListFragmentTasksListRecycleview.setAdapter(adapter);
    }

    private void setupUI() {
        FirebaseDatabase.getInstance(Constants.databaseURL).getReference().child("User").child(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String name = Objects.requireNonNull(snapshot.getValue(UserModel.class)).getUsername();
                    ((MainActivity) requireActivity()).setupToolbar(name.substring(0, 1).toUpperCase() + name.substring(1));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}