package com.example.foodapp;

import android.app.Application;

public class OrderApi extends Application {
    private String username;
    private String userId;
    private static OrderApi instance;

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


    public OrderApi() {
    }

    public static OrderApi getInstance() {
        if (instance == null)
            instance = new OrderApi();

        return instance;
    }

    ;
}
