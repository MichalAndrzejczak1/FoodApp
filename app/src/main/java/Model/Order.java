package Model;

import com.google.firebase.Timestamp;

public class Order {
    private String title;
    private String description;
    private String userId;
    private int count;
    private double tPrice;
    private int categoryNumber;
    private int productNumber;
    private Timestamp timeAdded;

    public Order() {      //For firestore
    }

    public Order(String title, String description, String userId, int count, double tPrice, int categoryNumber, int productNumber, Timestamp timeAdded) {
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.count = count;
        this.tPrice = tPrice;
        this.categoryNumber = categoryNumber;
        this.productNumber = productNumber;
        this.timeAdded = timeAdded;
    }

    public int getCategoryNumber() {
        return categoryNumber;
    }

    public void setCategoryNumber(int categoryNumber) {
        this.categoryNumber = categoryNumber;
    }

    public int getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(int productNumber) {
        this.productNumber = productNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double gettPrice() {
        return tPrice;
    }

    public void settPrice(double tPrice) {
        this.tPrice = tPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userID_) {
        this.userId = userID_;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
