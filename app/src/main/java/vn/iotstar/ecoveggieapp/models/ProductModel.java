package vn.iotstar.ecoveggieapp.models;

import java.util.List;

public class ProductModel {
    private int product_id;
    private String product_name;
    private String description;
    private double price;
    private int instock_quantity;
    private CategoryModel category;
    private List<String> images; // Danh sách hình ảnh
    private String unit; // Thêm trường unit

    public ProductModel(int product_id, String product_name, String description, double price, int instock_quantity, CategoryModel category, List<String> images)
    {
        this.product_id = product_id;
        this.product_name = product_name;
        this.description = description;
        this.price = price;
        this.instock_quantity = instock_quantity;
        this.category = category;
        this.images = images;
    }

    public ProductModel(int product_id, String product_name, String description, double price, int instock_quantity, CategoryModel category, List<String> images, String unit)
    {
        this.product_id = product_id;
        this.product_name = product_name;
        this.description = description;
        this.price = price;
        this.instock_quantity = instock_quantity;
        this.category = category;
        this.images = images;
        this.unit = unit; // Khởi tạo unit
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

    public CategoryModel getCategory() {
        return category;
    }


    public List<String> getImages() {
        return images;
    }

    public String getUnit() {
        return unit; // Getter cho unit
    }

    public void setUnit(String unit) {
        this.unit = unit; // Setter cho unit
    }
}
