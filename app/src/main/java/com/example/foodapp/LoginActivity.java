package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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

        tts = new TextToSpeech(LoginActivity.this, i -> {
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
                                    OrderApi orderApi = OrderApi.getInstance();
                                    orderApi.setUsername(snapshot.getString("username"));
                                    orderApi.setUserId(snapshot.getString("userId"));

                                    startActivity(new Intent(LoginActivity.this, OrderingActivity.class));

                                    //finish();
                                }
                            }

                        });
            }).addOnFailureListener(e -> {
                binding.loginProgress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, getString(R.string.email_andor_password_are_incorrect), Toast.LENGTH_SHORT).show();

            });

        }
        else {
            binding.loginProgress.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, getString(R.string.please_enter_email_and_password), Toast.LENGTH_SHORT).show();
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
                tts.speak(getString(R.string.loginActivityHint), TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.action_signout:
                Toast.makeText(LoginActivity.this, R.string.sign_out_before_login_toast,  Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {

        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean value = sharedPreferences.getBoolean("logout", false);
        if( value){
            auth.signOut();
        }
        super.onStop();
    }
}