package com.example.foodapp;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        //Setting theme before calling onCreate method.
        boolean value = sharedPreferences.getBoolean("nightTheme", false);
        if (!value) {
            setTheme(R.style.Theme_Day);
        } else {
            setTheme(R.style.Theme_Night);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        auth = FirebaseAuth.getInstance();

        binding.btnCreateAccount.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));
        });

        binding.btnLogin.setOnClickListener(view -> {
            loginEmailPasswordUser(binding.etEmail.getText().toString().trim(),
                    binding.etPassword.getText().toString().trim());
        });
    }

    private void loginEmailPasswordUser(String email, String password) {
        binding.loginProgress.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)) {
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                FirebaseUser user = auth.getCurrentUser();
                String currentUserId = user.getUid();

                collectionReference.whereEqualTo("userId", currentUserId)
                        .addSnapshotListener((value, error) -> {

                            assert value != null;
                            if (!value.isEmpty()) {
                                for (QueryDocumentSnapshot snapshot : value) {
                                    OrderApi orderApi = OrderApi.getInstance();
                                    orderApi.setUsername(snapshot.getString("username"));
                                    orderApi.setUserId(snapshot.getString("userId"));

                                    startActivity(new Intent(LoginActivity.this, OrderingActivity.class));

                                }
                            }

                        });
            }).addOnFailureListener(e -> {
                binding.loginProgress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Email and/or password are incorrect", Toast.LENGTH_SHORT).show();

            });

        } else {
            binding.loginProgress.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStop() {

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean("logout", false);
        if (value) {
            auth.signOut();
        }
        super.onStop();
    }
}