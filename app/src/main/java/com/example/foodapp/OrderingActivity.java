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
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");

    private List<Order> orderList = new ArrayList<>();

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

//            Klasa odpowiadająca za 2 recyclerViewy
            recyclerViewCategory();
    }

    private void recyclerViewCategory(){
        //Creating categories
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerViewCategoryList = binding.recyclerView;
        recyclerViewCategoryList2 = binding.recyclerView2;
        recyclerViewCategoryList.setLayoutManager(linearLayoutManager);
        recyclerViewCategoryList2.setLayoutManager(linearLayoutManager2);

        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("Pizza","burger1"));
        categories.add(new Category("Burger","cat_2"));
        categories.add(new Category("Hotdog","cat_3"));
        categories.add(new Category("Drink","cat_3"));
        categories.add(new Category("Donut","cat_3"));

        adapter = new CategoryRecyclerAdapter(categories);
        recyclerViewCategoryList.setAdapter(adapter);

        //Creating products

        //Arrays for products
        ArrayList<Product> pizzas = new ArrayList<>();
        pizzas.add(new Product("Small Pizza","pizza1","Small, but very tasty.",5.70));
        pizzas.add(new Product("Italian Pizza","pizza2","For those who appreciate bland dishes. ",3.70));
        pizzas.add(new Product("Super Spicy Pizza","pizza3","Spicy as a dragon's breath.",8.70));
        pizzas.add(new Product("Salami Pizza","pizza4","Pizza with meat.",1.70));
        pizzas.add(new Product("Pizza with mushrooms","pizza5","WhY dO I fEeL sO sTrAnGe?",2.70));

        adapter2 = new ProductRecyclerAdapter(this,pizzas);


        ArrayList<Product> burgers = new ArrayList<>();
        burgers.add(new Product("Small Burger","burger1","Small but very tasty.",5.70));
        burgers.add(new Product("Medium Burger","burger2","Medium-sized, tasty burger.",3.70));
        burgers.add(new Product("BigBurger","burger3","Can replace a whole meal.",8.70));
        burgers.add(new Product("Vege Burger","burger4","Tastes like a chicken",1.70));
        burgers.add(new Product("Golden Burger","burger5","Expensive product for the richest",2.70));

        ArrayList<Product> hotdogs = new ArrayList<>();
        hotdogs.add(new Product("Hotdog with mustard","hotdog1","The cheapest hotdog.",1.70));
        hotdogs.add(new Product("VegeDog","hotdog2","Tastes like meat but contains vegetables and bread.",3.70));
        hotdogs.add(new Product("ChickenDog","hotdog3","Tastes like a chicken but cotains dog in its name.",8.70));

        ArrayList<Product> drinks = new ArrayList<>();
        drinks.add(new Product("Premium Lemonade","drink1","Flavour of lemons.",5.70));
        drinks.add(new Product("Coca Cola Zero","drink2","Very delicious but unhealthy.",3.70));
        drinks.add(new Product("Water (1l)","drink3","Not mineralised.",8.70));
        drinks.add(new Product("Milk","drink4","Cow milk",1.70));
        drinks.add(new Product("Beer","drink5","Happy hour!",2.70));

        ArrayList<Product> donuts = new ArrayList<>();
        donuts.add(new Product("Choconut","donut1","Donut with chocolate.",3.20));
        donuts.add(new Product("Choconut with sprinkles","donut2","Donut with chocolate and sprinkles on top.",3.70));
        donuts.add(new Product("Raspberrynut with sprinkles","donut3","Tastes like raspberries.",5.00));
        donuts.add(new Product("Vanilla Donut with sprinkles","donut4","The cheapest but very good.",1.50));
        donuts.add(new Product("Honeynut with icing","donut5","Heaven in your mouth.",4.50));


        recyclerViewCategoryList2.setAdapter(adapter2);
        //Creating onClickListeners on category
        CategoryRecyclerAdapter adapter = new CategoryRecyclerAdapter(categories);
        adapter.setOnItemClickListener(new CategoryRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                activeCategoryNumber = position;
                switch (position) {
                    case 0: {
                        ProductRecyclerAdapter tmp = new ProductRecyclerAdapter(OrderingActivity.this, pizzas);
                        adapter2 =  tmp;
                       break;
                    }
                    case 1: {
                        ProductRecyclerAdapter tmp = new ProductRecyclerAdapter(OrderingActivity.this, burgers);
                        adapter2 =  tmp;
                        break;
                    }
                    case 2: {
                        ProductRecyclerAdapter tmp = new ProductRecyclerAdapter(OrderingActivity.this, hotdogs);
                        adapter2 =  tmp;
                        break;
                    }
                    case 3: {
                        ProductRecyclerAdapter tmp = new ProductRecyclerAdapter(OrderingActivity.this, drinks);
                        adapter2 =  tmp;
                        break;
                    }
                    case 4: {
                        ProductRecyclerAdapter tmp = new ProductRecyclerAdapter(OrderingActivity.this,donuts);
                        adapter2 =  tmp;
                        break;
                    }
                }
                recyclerViewCategoryList2.setAdapter(adapter2);
//                Toast.makeText(OrderingActivity.this, "onItemClick position: " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(int position, View v) {

            }

        });

        //Creating onClickListeners on products
        ProductRecyclerAdapter adapterProduct = new ProductRecyclerAdapter(pizzas);
        adapterProduct.setOnItemClickListener(new ProductRecyclerAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                activeProductNumber = position;
                Intent i = new Intent(OrderingActivity.this, ShowDetailActivity.class);
                i.putExtra("activeCategoryNumber",activeCategoryNumber);
                i.putExtra("activeProductNumber",activeProductNumber);
                switch (activeCategoryNumber){
                    case 0:{
                        i.putExtra("description", pizzas.get(position).getDescription());
                        i.putExtra("title", pizzas.get(position).getTitle());
                        i.putExtra("price", pizzas.get(position).getPrice());
                        break;
                    }
                    case 1:{
                        i.putExtra("description", burgers.get(position).getDescription());
                        i.putExtra("title", burgers.get(position).getTitle());
                        i.putExtra("price", burgers.get(position).getPrice());
                        break;
                    }
                    case 2:{
                        i.putExtra("description", hotdogs.get(position).getDescription());
                        i.putExtra("title", hotdogs.get(position).getTitle());
                        i.putExtra("price", hotdogs.get(position).getPrice());
                        break;
                    }
                    case 3:{
                        i.putExtra("description", drinks.get(position).getDescription());
                        i.putExtra("title", drinks.get(position).getTitle());
                        i.putExtra("price", drinks.get(position).getPrice());
                        break;
                    }
                    case 4:{
                        i.putExtra("description", donuts.get(position).getDescription());
                        i.putExtra("title", donuts.get(position).getTitle());
                        i.putExtra("price", donuts.get(position).getPrice());
                        break;
                    }
                }
                startActivity(i);


            }

            @Override
            public void onItemLongClick(int position, View v) {
//                Toast.makeText(OrderingActivity.this, "onItemLongClick pos = " + position, Toast.LENGTH_SHORT).show();
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
