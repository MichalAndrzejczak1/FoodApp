package Model;

import com.google.firebase.Timestamp;

public class User {

    private String email;
    private String money;
    private String username;
    private String password;
    private String imageURL;
    private Timestamp timeAdded;

    public User() {         //For firestore
    }

    public User(String email, String money, String username, String password) {
        this.email = email;
        this.money = money;
        this.username = username;
        this.password = password;
    }

    public User(String email, String money, String username, String password, String imageURL) {
        this.email = email;
        this.money = money;
        this.username = username;
        this.password = password;
        this.imageURL = imageURL;
    }

    public Timestamp getTimeAdded() {
        return timeAdded;
    }

    public void setTimeAdded(Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
