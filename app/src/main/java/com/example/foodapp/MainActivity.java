package com.example.foodapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodapp.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db =  FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        //Setting theme before calling onCreate method.
        boolean var = sharedPreferences.getBoolean("nightTheme", false);
        if (!var) {
            setTheme(R.style.Theme_Day);
        } else {
            setTheme(R.style.Theme_Night);
        }

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = firebaseAuth -> {
            currentUser = firebaseAuth.getCurrentUser();
            if(currentUser != null){
                currentUser = firebaseAuth.getCurrentUser();
                final String currentUserId = currentUser.getUid();

                collectionReference.whereEqualTo("userId",currentUserId)
                        .addSnapshotListener((value, error) -> {
                            if(error != null){
                                return;
                            }
                            String name;

                            if(!value.isEmpty()){
                                for (QueryDocumentSnapshot snapshot : value){
                                    OrderApi orderApi = OrderApi.getInstance();
                                    orderApi.setUserId(snapshot.getString("userId"));
                                    orderApi.setUsername(snapshot.getString("username"));

                                    startActivity(new Intent(MainActivity.this, OrderingActivity.class));
                                    finish();
                                }
                            }
                        });


            }
        };



        //Get started button
        binding.btnGetStarted.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });

        tts = new TextToSpeech(MainActivity.this, i -> {
            if (i != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });

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
                tts.speak(getString(R.string.mainActivityHint), TextToSpeech.QUEUE_FLUSH, null);
                break;
            case R.id.action_signout:
                Toast.makeText(MainActivity.this, R.string.sign_out_on_main_activity_toast, Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }

}