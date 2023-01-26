package com.example.foodapp;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;
import static androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DecimalFormat;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.foodapp.databinding.ActivityShowDetailBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

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
    private String picture;
    private String description;
    private String money = "0";
    private Double dMoney;

    private CollectionReference collectionReference = db.collection("Users");
    private final CollectionReference collectionReference2 = db.collection("Orders");


    @RequiresApi(api = Build.VERSION_CODES.N)
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
        setContentView(R.layout.activity_show_detail);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_show_detail);

        //Firesbase powitanie
        collectionReference.whereEqualTo("userId", OrderApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot users : queryDocumentSnapshots) {
                            User user = users.toObject(User.class);
                            money = user.getMoney();
                        }

                    } else {
                    }
                }).addOnFailureListener(e -> Toast.makeText(ShowDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            description = extras.getString("description");
            title = extras.getString("title");
            price = extras.getDouble("price");
            picture = extras.getString("picture");
            activeCategoryNumber = extras.getInt("activeCategoryNumber");
            activeProdcutNumber = extras.getInt("activeProductNumber");
        }
        binding.tvTitleShowDetails.setText(title);
        binding.tvDescription.setText(description);
        binding.tvPriceShowDetails.setText("$" + price);
        binding.ivProductShowDetail.setImageResource(this.getResources().getIdentifier(picture, "drawable", this.getPackageName()));
//        binding.ivProductShowDetail.setImageDrawable(getResources().getDrawable(R.drawable.burger1));

        NumberFormat formatter = new DecimalFormat("##.00");
        //On click listener on plus button
        binding.ivPlus.setOnClickListener(view -> {
            int count = Integer.parseInt(binding.tvCount.getText().toString());
            count++;
            binding.tvCount.setText(String.valueOf(count));

            binding.tvPriceShowDetails.setText("$" + formatter.format(price * count));
        });

        //On click listener on minus button
        binding.ivMinus.setOnClickListener(view -> {
            int count = Integer.parseInt(binding.tvCount.getText().toString());
            if (count != 1) {
                count--;
                binding.tvCount.setText(String.valueOf(count));
            }
            binding.tvPriceShowDetails.setText(formatter.format(price * count));
        });

        //On click listener on make order button
        binding.btnMakeOrder.setOnClickListener(view -> {
            binding.pbShowDetail.setVisibility(View.VISIBLE);

            if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)) {
                double tPrice = price * Double.parseDouble(binding.tvCount.getText().toString());
                dMoney = Double.parseDouble(money);
                if (dMoney >= tPrice) {
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
                                            .update("money", String.valueOf(dMoney - tPrice));          //
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
                    order.setPicture(picture);
                    order.settPrice(Math.round(price * Double.parseDouble(binding.tvCount.getText().toString())) * 100.0 / 100.0);
                    order.setCount(Integer.parseInt(binding.tvCount.getText().toString()));
                    order.setProductNumber(activeProdcutNumber);
                    order.setCategoryNumber(activeCategoryNumber);

                    collectionReference2.add(order).addOnSuccessListener(documentReference -> {
                        binding.pbShowDetail.setVisibility(View.INVISIBLE);
                        startActivity(new Intent(ShowDetailActivity.this, FoodListActivity.class));
                        Toast.makeText(ShowDetailActivity.this, getString(R.string.making_order_done), Toast.LENGTH_SHORT).show();
                        finish();
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            binding.pbShowDetail.setVisibility(View.INVISIBLE);
                            Log.d("ShowDetailActivity", "onFailure: + adding order + " + e.getMessage());
                        }
                    });

                } else {
                    Toast.makeText(ShowDetailActivity.this, getString(R.string.not_enough_money_to_order), Toast.LENGTH_SHORT).show();
                }


            }
            binding.pbShowDetail.setVisibility(View.INVISIBLE);

        });
    }
    //End of onCreate


}