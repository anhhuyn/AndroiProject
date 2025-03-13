package vn.iotstar.ecoveggieapp.models;

public class CategoryModel {
    private int category_id;
    private String category_name;
    private String thumbnail;
    private int productCount; // Thêm số lượng sản phẩm

    public CategoryModel(int category_id, String category_name, String thumbnail, int productCount) {
        this.category_id = category_id;
        this.category_name = category_name;
        this.thumbnail = thumbnail;
        this.productCount = productCount;
    }

    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public int getProductCount() {
        return productCount;
    }
}

