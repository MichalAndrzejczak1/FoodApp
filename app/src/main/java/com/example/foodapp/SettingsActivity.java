package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;

import com.example.foodapp.databinding.ActivityOrderingBinding;
import com.example.foodapp.databinding.ActivitySettingsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    ActivitySettingsBinding binding;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextToSpeech tts;



    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Setting theme before calling onCreate method.
        boolean value = sharedPreferences.getBoolean("nightTheme", false);
        if (!value) {
            setTheme(R.style.Theme_Day);
        } else {
            setTheme(R.style.Theme_Night);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Firebase auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);



        //Menu
        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, SettingsActivity.class)));


        //Settings
        value = sharedPreferences.getBoolean("logout", false);
        if(value){
            binding.swLogout.setChecked(true);
        };
        value = sharedPreferences.getBoolean("nightTheme", false);
        if(value){
            binding.swNightTheme.setChecked(true);
        };
        value = sharedPreferences.getBoolean("silentMode", false);
        if(value){
            binding.swSilentMode.setChecked(true);
        };



        //Settings listeners
        binding.swLogout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor.remove("logout");
                if(binding.swLogout.isChecked()) {
                    editor.putBoolean("logout", true);
                    editor.apply();
                }
                else {
                    editor.putBoolean("logout", false);
                    editor.apply();
                }
            }
        });
        binding.swNightTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor.remove("nightTheme");
                if(binding.swNightTheme.isChecked()) {
                    editor.putBoolean("nightTheme", true);
                    editor.apply();
                }
                else {
                    editor.putBoolean("nightTheme", false);
                    editor.apply();
                }
            }
        });
        binding.swSilentMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor.remove("silentMode");
                if(binding.swSilentMode.isChecked()) {
                    editor.putBoolean("silentMode", true);
                    editor.apply();
                }
                else {
                    editor.putBoolean("silentMode", false);
                    editor.apply();
                }
            }
        });

        tts = new TextToSpeech(SettingsActivity.this, i -> {
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
        switch (item.getItemId()){
            case R.id.action_speak:
                if( currentUser != null && auth != null){
                    tts.speak(getString(R.string.settingsActivityHint), TextToSpeech.QUEUE_FLUSH, null);
                }
                break;
            case R.id.action_signout:
                if( currentUser != null && auth != null){
                    auth.signOut();
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
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