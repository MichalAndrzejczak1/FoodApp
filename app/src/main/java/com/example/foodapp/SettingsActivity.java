package com.example.foodapp;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.media.AudioManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;

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
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextToSpeech tts;
    AudioManager audioManager;

    int lastKnownVolume;


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
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        setContentView(R.layout.activity_settings);

        //Firebase auth
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        lastKnownVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        //Menu
        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, AccountActivity.class)));
//        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(SettingsActivity.this, SettingsActivity.class)));
        binding.sbSoundLevel.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        binding.sbSoundLevel.setProgress(lastKnownVolume);


        //Settings
        value = sharedPreferences.getBoolean("logout", false);
        if (value) {
            binding.swLogout.setChecked(true);
        }

        value = sharedPreferences.getBoolean("nightTheme", false);
        if (value) {
            binding.swNightTheme.setChecked(true);
        }

        value = sharedPreferences.getBoolean("silentMode", false);
        if (value) {
            binding.swSilentMode.setChecked(true);
        }

        binding.sbSoundLevel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        //Settings listeners
        binding.swLogout.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor.remove("logout");
                if (binding.swLogout.isChecked()) {
                    editor.putBoolean("logout", true);
                    editor.apply();
                } else {
                    editor.putBoolean("logout", false);
                    editor.apply();
                }
            }
        });
        binding.swNightTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor.remove("nightTheme");
                if (binding.swNightTheme.isChecked()) {
                    editor.putBoolean("nightTheme", true);
                    editor.apply();
                } else {
                    editor.putBoolean("nightTheme", false);
                    editor.apply();
                }
            }
        });
        binding.swSilentMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                editor.remove("silentMode");
                if (binding.swSilentMode.isChecked()) {
                    lastKnownVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
                    binding.sbSoundLevel.setProgress(0);
                    binding.sbSoundLevel.setEnabled(false);
                    editor.putBoolean("silentMode", true);
                    editor.apply();
                } else {
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    binding.sbSoundLevel.setProgress(lastKnownVolume);
                    binding.sbSoundLevel.setEnabled(true);
                    editor.putBoolean("silentMode", false);
                    editor.apply();
                }
            }
        });

        // ----------------------- TTS CONFIG -----------------------
        tts = new TextToSpeech(SettingsActivity.this, i -> {
            if (i != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });
        tts.setSpeechRate(2.5f);
        // ----------------------------------------------------------
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Bundle params = new Bundle();
        params.putFloat(KEY_PARAM_VOLUME, 0.5f);
        switch (item.getItemId()) {
            case R.id.action_speak:
                if (currentUser != null && auth != null) {
                    tts.speak(getString(R.string.settingsActivityHint), TextToSpeech.QUEUE_FLUSH, params, null);
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