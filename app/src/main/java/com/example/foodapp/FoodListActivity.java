package com.example.foodapp;

import static android.speech.tts.TextToSpeech.Engine.KEY_PARAM_VOLUME;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodapp.databinding.ActivityFoodListBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Model.Order;
import UI.OrderRecyclerAdapter;

public class FoodListActivity extends AppCompatActivity {
    ActivityFoodListBinding binding;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Orders");

    private List<Order> orderList = new ArrayList<>();
    private OrderRecyclerAdapter orderRecyclerAdapter;

    TextToSpeech tts;

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
        setContentView(R.layout.activity_food_list);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_food_list);

        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(FoodListActivity.this, OrderingActivity.class)));
//        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(FoodListActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(FoodListActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(FoodListActivity.this, SettingsActivity.class)));

        binding.rvList.setHasFixedSize(true);
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));

        // ----------------------- TTS CONFIG -----------------------
        tts = new TextToSpeech(FoodListActivity.this, i -> {
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
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        Bundle params = new Bundle();
        params.putFloat(KEY_PARAM_VOLUME, sharedPreferences.getFloat("volume", 0.5f));
        switch (item.getItemId()) {
            case R.id.action_speak:
                if (currentUser != null && auth != null) {
                    tts.speak(getString(R.string.foodListActivityHint), TextToSpeech.QUEUE_FLUSH, params, null);
                }
                break;
            case R.id.action_signout:
                if (currentUser != null && auth != null) {
                    auth.signOut();
                }
                startActivity(new Intent(com.example.foodapp.FoodListActivity.this, MainActivity.class));
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();


        collectionReference.whereEqualTo("userId", OrderApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot orderS : queryDocumentSnapshots) {
                            Order order = orderS.toObject(Order.class);
                            orderList.add(order);
                        }

                        OrderRecyclerAdapter myRecyclerViewAdapter = new OrderRecyclerAdapter(FoodListActivity.this, orderList);
                        binding.setMyAdapter(myRecyclerViewAdapter);
                        orderRecyclerAdapter = new OrderRecyclerAdapter(FoodListActivity.this, orderList);
                        binding.rvList.setAdapter(orderRecyclerAdapter);
                        orderRecyclerAdapter.notifyDataSetChanged();

                    } else {
                        binding.tvListNoEntry.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(e -> Toast.makeText(com.example.foodapp.FoodListActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());
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