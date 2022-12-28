package com.example.foodapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.foodapp.databinding.ActivityOrderingBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import Model.Category;
import Model.Order;
import Model.Product;
import Model.User;
import UI.CategoryRecyclerAdapter;
import UI.ProductRecyclerAdapter;

public class OrderingActivity extends AppCompatActivity {
    ActivityOrderingBinding binding;

    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener authStateListener;
    FirebaseUser currentUser;

    OrderApi orderApi = OrderApi.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference collectionReferenceCategories = db.collection("Categories");
    private CollectionReference collectionReferenceProducts = db.collection("Products");

    private List<Order> productList = new ArrayList<>();
    ArrayList<Category> categories;
    ArrayList<Product> products;

    RecyclerView recyclerViewCategoryList;
    RecyclerView.Adapter adapter;

    RecyclerView recyclerViewCategoryList2;
    RecyclerView.Adapter adapter2;

    private int activeCategoryNumber;
    private int activeProductNumber;



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
        setContentView(R.layout.activity_ordering);

        //Firebase + binding
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ordering);

        //Dolne menu
        binding.ivOrderFood.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, OrderingActivity.class)));
        binding.ivListOfOrders.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, FoodListActivity.class)));
        binding.ivAccount.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, AccountActivity.class)));
        binding.ivSettings.setOnClickListener(view -> startActivity(new Intent(OrderingActivity.this, SettingsActivity.class)));

        //Firesbase powitanie
        collectionReference.whereEqualTo("userId", OrderApi.getInstance()
                        .getUserId())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
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
                }).addOnFailureListener(e -> Toast.makeText(OrderingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());

        categories = new ArrayList<>();
        products = new ArrayList<>();
        collectionReferenceCategories.get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(!queryDocumentSnapshots.isEmpty()){
                        for (QueryDocumentSnapshot cats : queryDocumentSnapshots){
                            Category cat = cats.toObject(Category.class);
                            categories.add(cat);
                        }
                        recyclerViewCategory();
                    }

                }).addOnFailureListener(e -> Toast.makeText(OrderingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());
//            Klasa odpowiadająca za 2 recyclerViewy

    }

    private void recyclerViewCategory(){
        //Creating categories
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCategoryList = binding.recyclerView;
        recyclerViewCategoryList2 = binding.recyclerView2;
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);
        recyclerViewCategoryList2.setLayoutManager(linearLayoutManager2);


        adapter = new CategoryRecyclerAdapter(OrderingActivity.this, categories);
        recyclerViewCategoryList.setAdapter(adapter);




        //Creating onClickListeners on category
        CategoryRecyclerAdapter adapter = new CategoryRecyclerAdapter(categories);
        adapter.setOnItemClickListener(new CategoryRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                activeCategoryNumber = position;

                collectionReferenceProducts.whereEqualTo("category", activeCategoryNumber+1).get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if(!queryDocumentSnapshots.isEmpty()){
                                products.clear();
                                for (QueryDocumentSnapshot produs : queryDocumentSnapshots){
                                    Product product = produs.toObject(Product.class);
                                    products.add(product);
                                }
                                adapter2  = new ProductRecyclerAdapter(OrderingActivity.this, products);
                                recyclerViewCategoryList2.setAdapter(adapter2);
                                ProductRecyclerAdapter adapterProduct = new ProductRecyclerAdapter(products);
                                adapterProduct.setOnItemClickListener(new ProductRecyclerAdapter.ClickListener() {
                                    @Override
                                    public void onItemClick(int position, View v) {
                                        activeProductNumber = position;
                                        Intent i = new Intent(OrderingActivity.this, ShowDetailActivity.class);
                                        i.putExtra("activeCategoryNumber",activeCategoryNumber);
                                        i.putExtra("activeProductNumber",activeProductNumber);

                                        i.putExtra("description", products.get(position).getDescription());
                                        i.putExtra("title", products.get(position).getTitle());
                                        i.putExtra("price", products.get(position).getPrice());
                                        i.putExtra("picture", products.get(position).getPicture());

                                        startActivity(i);


                                    }

                                    @Override
                                    public void onItemLongClick(int position, View v) {

                                    }


                                });
                            }

                        }).addOnFailureListener(e -> Toast.makeText(OrderingActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show());


            }

            @Override
            public void onItemLongClick(int position, View v) {

            }


        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_speak: {
                if (currentUser != null && auth != null) {
//                    startActivity(new Intent(com.example.foodapp.FoodListActivity.this, PostJournalActivity.class));
                }
            }
            break;
            case R.id.action_signout: {
                if (currentUser != null && auth != null) {
                    auth.signOut();
                }
                startActivity(new Intent(this, MainActivity.class));
                finish();
                break;
            }
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
