package vn.iotstar.ecoveggieapp.models;

import java.util.List;

public class CartItemModel {
    private int id;
    private int productId;
    private String productName;
    private double price;
    private int quantity;
    private List<String> images;
    private String unit;

    // Constructor
    public CartItemModel(int id, int productId, String productName, double price, int quantity, List<String> images, String unit) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.images = images;
        this.unit = unit;
    }

    // Getters và setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }
    public String getUnit() { // Getter for unit
        return unit;
    }

    public void setUnit(String unit) { // Setter for unit
        this.unit = unit;
    }
}
