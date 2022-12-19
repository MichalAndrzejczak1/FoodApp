package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.databinding.ActivityShowDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.Locale;

import Model.Order;
import Model.User;

public class ShowDetailActivity extends AppCompatActivity {
    ActivityShowDetailBinding binding;

    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private int activeCategoryNumber;
    private int activeProdcutNumber;
    private double price;
    private String title;
    private String description;
    private String money = "0";
    private Double dMoney;
    FirebaseAuth.AuthStateListener authStateListener;

    private CollectionReference collectionReference = db.collection("Users");
    private final CollectionReference collectionReference2 = db.collection("Orders");

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
        setContentView(R.layout.activity_show_detail);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_detail);


        //Dolne menu
//        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(ShowDetailActivity.this, OrderingActivity.class)));
//        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(ShowDetailActivity.this, FoodListActivity.class)));
//        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(ShowDetailActivity.this, AccountActivity.class)));
//        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(ShowDetailActivity.this, SettingsActivity.class)));

        //Firesbase powitanie
        collectionReference.whereEqualTo("userId", OrderApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot users : queryDocumentSnapshots){
                                User user = users.toObject(User.class);
                                money = user.getMoney();
                            }

                        }else {
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(ShowDetailActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            description = extras.getString("description");
            title = extras.getString("title");
            price = extras.getDouble("price");
            activeCategoryNumber = extras.getInt("activeCategoryNumber");
            activeProdcutNumber = extras.getInt("activeProductNumber");
        }
        binding.tvTitleShowDetails.setText(title);
        binding.tvDescription.setText(description);
        binding.tvPriceShowDetails.setText("$"+price);

        switch (activeCategoryNumber) {
            case 0:{
                //pizzas
                switch (activeProdcutNumber){
                    case 0:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.pizza1));
                        break;
                    }
                    case 1:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.pizza2));
                        break;
                    }
                    case 2:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.pizza3));
                        break;
                    }
                    case 3:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.pizza4));
                        break;
                    }
                    case 4:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.pizza5));
                        break;
                    }
                }
                break;
            }
            case 1:{
                //burgers
                switch (activeProdcutNumber){
                    case 0:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.burger1));
                        break;
                    }
                    case 1:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.burger2));
                        break;
                    }
                    case 2:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.burger3));
                        break;
                    }
                    case 3:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.burger4));
                        break;
                    }
                    case 4:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.burger5));
                        break;
                    }
                }
                break;
            }
            case 2:{
                //hotdogs
                switch (activeProdcutNumber){
                    case 0:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.hotdog1));
                        break;
                    }
                    case 1:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.hotdog2));
                        break;
                    }
                    case 2:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.hotdog3));
                        break;
                    }
                }
                break;
            }
            case 3:{
                //drinks
                switch (activeProdcutNumber){
                    case 0:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.drink1));
                        break;
                    }
                    case 1:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.drink2));
                        break;
                    }
                    case 2:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.drink3));
                        break;
                    }
                    case 3:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.drink4));
                        break;
                    }
                    case 4:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.drink5));
                        break;
                    }
                }
                break;
            }
            case 4:{
                //donuts
                switch (activeProdcutNumber){
                    case 0:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.donut1));
                        break;
                    }
                    case 1:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.donut2));
                        break;
                    }
                    case 2:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.donut3));
                        break;
                    }
                    case 3:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.donut4));
                        break;
                    }
                    case 4:{
                        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.donut5));
                        break;
                    }
                }
                break;
            }
        }

        //On click listener on plus button
        binding.ivPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(binding.tvCount.getText().toString());
                count++;
                binding.tvCount.setText(String.valueOf(count));
                binding.tvPriceShowDetails.setText("$"+ String.valueOf(price*count));
            }
        });

        //On click listener on minus button
        binding.ivMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(binding.tvCount.getText().toString());
                if(count != 1) {
                    count--;
                    binding.tvCount.setText(String.valueOf(count));
                }
                binding.tvPriceShowDetails.setText("$"+ String.valueOf(price*count));
            }
        });

        //On click listener on make order button
        binding.btnMakeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.pbShowDetail.setVisibility(View.VISIBLE);

                if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description))
                {
                    double tPrice = price * Double.parseDouble(binding.tvCount.getText().toString());
                    dMoney = Double.parseDouble(money);
                    if(dMoney >= tPrice) {
                        //You have enough money to make an order.

                        //Removing specific amount of money from your account
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
                                                .update("money", String.valueOf(dMoney-tPrice));          //
                                    }
                                }
                            }
                        });

                        //Adding order to OrdersCollection
                        Order order = new Order();
                        order.setTitle(title);
                        order.setDescription(description);
                        order.setTimeAdded(new Timestamp(new Date()));
                        order.setUserId(currentUser.getUid());
                        order.settPrice(price * Double.parseDouble(binding.tvCount.getText().toString()));
                        order.setCount(Integer.parseInt(binding.tvCount.getText().toString()));
                        order.setProductNumber(activeProdcutNumber);
                        order.setCategoryNumber(activeCategoryNumber);

                        collectionReference2.add(order).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                binding.pbShowDetail.setVisibility(View.INVISIBLE);
                                startActivity(new Intent(ShowDetailActivity.this, FoodListActivity.class));
                                Toast.makeText(ShowDetailActivity.this, "Making order done!", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                binding.pbShowDetail.setVisibility(View.INVISIBLE);
                                Log.d("ShowDetailActivity", "onFailure: + adding order + "+ e.getMessage());
                            }
                        });

                    }
                    else {
                        Toast.makeText(ShowDetailActivity.this, "Not enough money to make an order!", Toast.LENGTH_SHORT).show();
                    }


                    }
                binding.pbShowDetail.setVisibility(View.INVISIBLE);

            }
        });

        tts = new TextToSpeech(ShowDetailActivity.this, i -> {
            if (i != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });

    }
    //End of onCreate

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
                tts.speak(getString(R.string.show_detail_activity_hint), TextToSpeech.QUEUE_FLUSH, null);
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


}