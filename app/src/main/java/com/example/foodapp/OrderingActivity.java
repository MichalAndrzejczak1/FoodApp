package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.databinding.ActivityFoodListBinding;
import com.example.foodapp.databinding.ActivityOrderingBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Model.Order;
import Model.User;

public class OrderingActivity extends AppCompatActivity {
    ActivityOrderingBinding binding;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    private List<Order> orderList = new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordering);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ordering);

        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, SettingsActivity.class)));


        collectionReference.whereEqualTo("userId", FoodApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if(!queryDocumentSnapshots.isEmpty()){
                            for (QueryDocumentSnapshot users : queryDocumentSnapshots){
                                User user = users.toObject(User.class);
                                binding.tvGreetings.setText("Hi "+ user.getUsername()+"!");
                                Picasso.get()
                                        .load(Uri.parse(user.getImageURL()))
                                        .placeholder(R.drawable.ic_accountimage)
                                        .fit()
                                        .into(binding.ivOrderAccountImage);
                            }

                        }else {
                        }
                    }
                }).addOnFailureListener(e -> Toast.makeText(OrderingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_speak:
                if( currentUser != null && auth != null){
//                    startActivity(new Intent(com.example.foodapp.FoodListActivity.this, PostJournalActivity.class));
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

}
