package com.example.foodapp;

import android.app.Application;

public class FoodApi extends Application {
    private String username;
    private String userId;
    private static FoodApi instance;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public FoodApi(){}

    public static FoodApi getInstance(){
        if(instance == null)
            instance = new FoodApi();

        return instance;
    };
}
