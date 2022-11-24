package com.example.foodapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.example.foodapp.databinding.ActivityOrderingBinding;
import com.example.foodapp.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, SettingsActivity.class)));


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}