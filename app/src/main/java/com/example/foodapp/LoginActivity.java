package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        tts = new TextToSpeech(getApplicationContext(), i -> {
            if (i != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });

    }

    private void loginEmailPasswordUser(String email, String password) {
        binding.loginProgress.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(authResult -> {
                FirebaseUser user = auth.getCurrentUser();
                String currentUserId = user.getUid();

                collectionReference.whereEqualTo("userId", currentUserId)
                        .addSnapshotListener((value, error) -> {
                            //if(error != null);          //unnecessary atm

                            assert value != null;
                            if(!value.isEmpty()){
                                for (QueryDocumentSnapshot snapshot : value){
                                    FoodApi foodApi = FoodApi.getInstance();
                                    foodApi.setUsername(snapshot.getString("username"));
                                    foodApi.setUserId(snapshot.getString("userId"));

                                    startActivity(new Intent(LoginActivity.this, OrderingActivity.class));

                                    //finish();
                                }
                            }

                        });
            }).addOnFailureListener(e -> {
                binding.loginProgress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Email and/or password are incorrect", Toast.LENGTH_SHORT).show();

            });

        }
        else {
            binding.loginProgress.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_speak:
                if (currentUser != null && auth != null) {
                    tts.speak(getString(R.string.loginActivityHint), TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            case R.id.action_signout:
                if (currentUser != null && auth != null) {
                    auth.signOut();
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}