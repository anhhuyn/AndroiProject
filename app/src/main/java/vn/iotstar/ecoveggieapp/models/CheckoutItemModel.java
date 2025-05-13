package vn.iotstar.ecoveggieapp.models;

import java.io.Serializable;

public class CheckoutItemModel implements Serializable {
    private int productId;  // Thêm trường productId
    private String productName;
    private String unit;
    private double price;
    private int quantity;
    private String imageUrl;

    public CheckoutItemModel(int productId, String productName, String unit, double price, int quantity, String imageUrl) {
        this.productId = productId;
        this.productName = productName;
        this.unit = unit;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public CheckoutItemModel() {}

    // Getter và Setter cho productId
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    // Các getter đã có
    public String getProductName() {
        return productName;
    }

    public String getUnit() {
        return unit;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
