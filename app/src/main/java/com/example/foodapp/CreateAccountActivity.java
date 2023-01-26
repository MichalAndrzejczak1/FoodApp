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

import com.example.foodapp.databinding.ActivityCreateAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ActivityCreateAccountBinding binding;
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        //Setting theme before calling onCreate method.
        boolean value = sharedPreferences.getBoolean("nightTheme", false);
        if (!value) {
            setDefaultNightMode(MODE_NIGHT_NO);
        } else {
            setDefaultNightMode(MODE_NIGHT_YES);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_account);

        auth = FirebaseAuth.getInstance();
        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();

            if (currentUser != null) {
                //user logged in
            } else {
                //no user yet
            }
        };
        //btn login to  do zrobienia konta przycisk, zapomniałem id zmienić :/ a potem się już nie dało
        binding.btnLogin.setOnClickListener(view -> {
            if (!TextUtils.isEmpty(binding.email.getText().toString().trim())
                    && !TextUtils.isEmpty(binding.etPassword.getText().toString().trim())
                    && !TextUtils.isEmpty(binding.etUsername.getText().toString().trim())) {

                String email = binding.email.getText().toString().trim();
                String password = binding.etPassword.getText().toString().trim();
                String username = binding.etUsername.getText().toString().trim();
                createUserEmailAccount(email, password, username);

            } else {
                Toast.makeText(
                                CreateAccountActivity.this,
                                "Empty fields aren't allowed",
                                Toast.LENGTH_SHORT)
                        .show();
            }

        });
    }

    private void createUserEmailAccount(String email, String password, String username) {
        if (!TextUtils.isEmpty(email)
                && !TextUtils.isEmpty(password)
                && !TextUtils.isEmpty(username)) {
            binding.loginProgress.setVisibility(View.VISIBLE);

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            //take user to order activity
                            currentUser = auth.getCurrentUser();
                            assert currentUser != null;
                            String currentUserId = currentUser.getUid();

                            //Create a UserMap so we can create a user in the User collection
                            Map<String, String> userObj = new HashMap<>();

                            userObj.put("userId", currentUserId);
                            userObj.put("username", username);
                            userObj.put("password", password);
                            userObj.put("email", email);
                            userObj.put("money", "0");
                            userObj.put("imageURL", "0");

                            //Save to firestore
                            collectionReference.add(userObj)
                                    .addOnSuccessListener(documentReference -> documentReference.get().addOnCompleteListener(task1 -> {
                                        if (task1.getResult().exists()) {
                                            binding.loginProgress.setVisibility(View.INVISIBLE);
                                            String name = task1.getResult().getString("username");

                                            OrderApi orderApi = OrderApi.getInstance();
                                            orderApi.setUserId(currentUserId);
                                            orderApi.setUsername(name);

                                            Intent intent = new Intent(CreateAccountActivity.this, OrderingActivity.class);
                                            intent.putExtra("username", name);
                                            intent.putExtra("userId", currentUserId);
                                            startActivity(intent);
                                        } else {

                                        }
                                    })).addOnFailureListener(e -> {

                                    });
                        }
                    }).addOnFailureListener(e -> {

                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = auth.getCurrentUser();
        auth.addAuthStateListener(authStateListener);
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