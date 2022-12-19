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
import android.speech.tts.TextToSpeech;
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
import java.util.Locale;

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
                }).addOnFailureListener(e -> Toast.makeText(OrderingActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show());

//            Klasa odpowiadająca za 2 recyclerViewy
            recyclerViewCategory();

        tts = new TextToSpeech(OrderingActivity.this, i -> {
            if (i != TextToSpeech.ERROR) {
                tts.setLanguage(Locale.getDefault());
            }
        });

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
        pizzas.add(new Product(getString(R.string.small_pizza),"pizza1",getString(R.string.small_but_very_tasty),5.70));
        pizzas.add(new Product(getString(R.string.italian_pizza),"pizza2",getString(R.string.for_those_who_appreciate_bland_dishes),3.70));
        pizzas.add(new Product(getString(R.string.super_spicy_pizza),"pizza3",getString(R.string.spicy_as_dragons_breath),8.70));
        pizzas.add(new Product(getString(R.string.salami_pizza),"pizza4",getString(R.string.pizza_with_meat),1.70));
        pizzas.add(new Product(getString(R.string.pizza_with_mushrooms),"pizza5",getString(R.string.why_do_i_feel_so_strange),2.70));

        adapter2 = new ProductRecyclerAdapter(this,pizzas);


        ArrayList<Product> burgers = new ArrayList<>();
        burgers.add(new Product(getString(R.string.small_burger),"burger1",getString(R.string.small_but_very_tasty),5.70));
        burgers.add(new Product(getString(R.string.medium_burger),"burger2",getString(R.string.medium_sized_tasty_burger),3.70));
        burgers.add(new Product(getString(R.string.big_burger),"burger3",getString(R.string.can_replace_a_whole_meal),8.70));
        burgers.add(new Product(getString(R.string.vege_burger),"burger4",getString(R.string.tastes_like_a_chicken),1.70));
        burgers.add(new Product(getString(R.string.golden_burger),"burger5",getString(R.string.expensive_product_for_the_richest),2.70));

        ArrayList<Product> hotdogs = new ArrayList<>();
        hotdogs.add(new Product(getString(R.string.hotdog_with_mustard),"hotdog1",getString(R.string.the_cheapest_hotdog),1.70));
        hotdogs.add(new Product(getString(R.string.vege_dog),"hotdog2",getString(R.string.tastes_like_meat_but_contains_vegetables_and_bread),3.70));
        hotdogs.add(new Product(getString(R.string.chicken_dog),"hotdog3",getString(R.string.tastes_like_a_chicken_but_contains_dog_in_its_name),8.70));

        ArrayList<Product> drinks = new ArrayList<>();
        drinks.add(new Product(getString(R.string.premium_lemonade),"drink1",getString(R.string.flavour_of_lemons),5.70));
        drinks.add(new Product(getString(R.string.coca_cola_Zero),"drink2",getString(R.string.very_delicious_but_unhealthy),3.70));
        drinks.add(new Product(getString(R.string.water_one_liter),"drink3",getString(R.string.not_mineralised),8.70));
        drinks.add(new Product(getString(R.string.milk),"drink4",getString(R.string.cow_milk),1.70));
        drinks.add(new Product(getString(R.string.beer),"drink5",getString(R.string.happy_hour),2.70));

        ArrayList<Product> donuts = new ArrayList<>();
        donuts.add(new Product(getString(R.string.choconut),"donut1",getString(R.string.donut_with_chocolate),3.20));
        donuts.add(new Product(getString(R.string.choconut_with_sprinkles),"donut2",getString(R.string.donut_with_chocolate_and_sprinkles_on_top),3.70));
        donuts.add(new Product(getString(R.string.rassberrynut_with_sprinkles),"donut3",getString(R.string.tastes_like_rasberries),5.00));
        donuts.add(new Product(getString(R.string.vanilla_donut_with_sprinkles),"donut4",getString(R.string.the_cheapest_but_very_good),1.50));
        donuts.add(new Product(getString(R.string.honeynut_with_icing),"donut5",getString(R.string.heaven_in_your_mouth),4.50));


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

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_speak: {
                tts.speak(getString(R.string.orderingActivityHint), TextToSpeech.QUEUE_FLUSH, null);
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
