package Model;

public class Product {
    private String title;
    private String picture;
    private String description;
    private Double price;
    private int number;

    public Product() {
    }

    public Product(String title, String picture, String description, Double price) {
        this.title = title;
        this.picture = picture;
        this.description = description;
        this.price = price;
    }

    public Product(String title, String picture, String description, Double price, int number) {
        this.title = title;
        this.picture = picture;
        this.description = description;
        this.price = price;
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
