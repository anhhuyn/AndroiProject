package vn.iotstar.ecoveggieapp.models;

import java.util.List;

public class ProductModel {
    private int product_id;
    private String product_name;
    private String description;
    private double price;
    private int instock_quantity;
    private String category;
    private List<String> images; // Danh sách hình ảnh

    public ProductModel(int product_id, String product_name, String description, double price, int instock_quantity, String category, List<String> images) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.description = description;
        this.price = price;
        this.instock_quantity = instock_quantity;
        this.category = category;
        this.images = images;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public int getInstock_quantity() {
        return instock_quantity;
    }

    public String getCategory() {
        return category;
    }

    public List<String> getImages() {
        return images;
    }
}
