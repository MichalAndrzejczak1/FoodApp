package com.example.foodapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

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

import java.util.Locale;

import Model.User;

public class AccountActivity extends AppCompatActivity {
    ActivityAccountBinding binding;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    ActivityResultLauncher<String> takePhoto;

    private StorageReference storageReference;
    private Uri imageUri;
    private CollectionReference collectionReference = db.collection("Users");

    TextToSpeech tts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

        collectionReference.whereEqualTo("userId", FoodApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot users : queryDocumentSnapshots) {
                            User user = users.toObject(User.class);
                            binding.tvEmailKontaText.setText(auth.getCurrentUser().getEmail());
                            binding.tvNazwaKontaText.setText(user.getUsername());
                            binding.tvSrodkiKontaText.setText(user.getMoney());
                            binding.tvHasloKontaText.setText(user.getPassword());
                            Picasso.get()
                                    .load(Uri.parse(user.getImageURL()))
                                    .placeholder(R.drawable.ic_accountimage)
                                    .fit()
                                    .into(binding.ivAccountImageAccountActivity);
                        }

                    }
                }).addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());

        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(AccountActivity.this, SettingsActivity.class)));
        binding.ivAccountImageAccountActivity.setOnClickListener(view -> {
            takePhoto.launch("image/*");
        });

        tts = new TextToSpeech(getApplicationContext(), i -> {
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
                                    }
                                }
                            }
                        });


                    }).addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Loading image into database went wrong", Toast.LENGTH_SHORT).show())).addOnFailureListener(e -> Toast.makeText(AccountActivity.this, "Uri is empty", Toast.LENGTH_SHORT).show());
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
                if (currentUser != null && auth != null) {
                    tts.speak(getString(R.string.accountActivityHint), TextToSpeech.QUEUE_FLUSH, null);
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