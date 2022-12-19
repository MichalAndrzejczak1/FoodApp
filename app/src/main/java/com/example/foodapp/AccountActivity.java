package com.example.foodapp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.example.foodapp.databinding.ActivityAccountBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.User;
//import UI.OrderRecyclerAdapter;

public class AccountActivity extends AppCompatActivity {
    ActivityAccountBinding binding;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    ActivityResultLauncher<String> takePhoto;

    TextToSpeech tts;


    private List<User> userList = new ArrayList<>();

    private StorageReference storageReference;
    private Uri imageUri;
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
        setContentView(R.layout.activity_account);

        storageReference = storage.getReference();
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_account);

        // binding.pbAccount.setVisibility(View.VISIBLE);
        takePhoto = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
            binding.ivAccountImageAccountActivity.setImageURI(result);
            imageUri = result;
            saveIMG();
        });

        collectionReference.whereEqualTo("userId", OrderApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot users : queryDocumentSnapshots) {
                            User user = users.toObject(User.class);
                            binding.tvEmailKontaText.setText(auth.getCurrentUser().getEmail());
                            binding.tvNazwaKontaText.setText(user.getUsername());
                            binding.tvSrodkiKontaText.setText(user.getMoney().toString());
                            binding.tvHasloKontaText.setText(user.getPassword());
                            Picasso.get()
                                    .load(Uri.parse(user.getImageURL()))
                                    .placeholder(R.drawable.ic_accountimage)
                                    .fit()
                                    .into(binding.ivAccountImageAccountActivity);
                        }

                    }
                }).addOnFailureListener(e -> Toast.makeText(AccountActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());

        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, SettingsActivity.class)));
        binding.ivAccountImageAccountActivity.setOnClickListener(view -> {
            takePhoto.launch("image/*");
        });

        //Charge Button
        binding.btnDoladuj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(AccountActivity.this, getString(R.string.it_may_take_some_time), Toast.LENGTH_SHORT).show();
                binding.pbAccount.setVisibility(View.VISIBLE);

                //// Wyłuskiwanie dokumentu korzystając z query
                Query query = collectionReference.whereEqualTo("userId", auth.getUid());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                //Uaktualnianie URL użytkownika
                                db.collection("Users")
                                        .document(document.getId())
                                        .update("money", "500");
                                Toast.makeText(AccountActivity.this, getString(R.string.recharging_completed), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                binding.pbAccount.setVisibility(View.INVISIBLE);
            }
        });

        tts = new TextToSpeech(AccountActivity.this, i -> {
            if (i != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });

    }

    private void saveIMG() {
        binding.pbAccount.setVisibility(View.VISIBLE);
        if (imageUri != null) {
            //Ścieżka gdzie ma być w Cloud Storage zapisywany obrazek
            StorageReference filepath = storageReference.child("foodapp_images")
                    .child("myImage" + Timestamp.now().getSeconds());

            //Wrzucenie obrazka
            filepath.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> filepath.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageURl = uri.toString();

                        //// Wyłuskiwanie dokumentu korzystając z query
                        Query query = collectionReference.whereEqualTo("userId", auth.getUid());
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        //Uaktualnianie URL użytkownika
                                        db.collection("Users")
                                                .document(document.getId())
                                                .update("imageURL", imageURl);
                                        Toast.makeText(AccountActivity.this, getString(R.string.loading_image_into_database_done), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });


                    }).addOnFailureListener(e -> Toast.makeText(AccountActivity.this, getString(R.string.loading_image_into_database_went_wrong), Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Uri is empty", Toast.LENGTH_SHORT).show());
        }
        binding.pbAccount.setVisibility(View.INVISIBLE);
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
                tts.speak(getString(R.string.accountActivityHint), TextToSpeech.QUEUE_FLUSH, null);
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