package Model;

import com.google.firebase.Timestamp;

public class Order {
    private String title;
    private String description;
    private String imageURL;
    private String userId;
    private Timestamp timeAdded;
    private String username;

    public Order() {      //For firestore
    }

    public Order(String title, String description, String imageURL, String userId, Timestamp timeAdded, String username) {
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
