package com.example.foodapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodapp.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    TextToSpeech tts;

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

        tts = new TextToSpeech(getApplicationContext(), i -> {
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
                if (currentUser != null && auth != null) {
                    tts.speak(getString(R.string.settingsActivityHint), TextToSpeech.QUEUE_FLUSH, null);
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