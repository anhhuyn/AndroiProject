package vn.iotstar.ecoveggieapp.models;

public class CartItem {
    private String productName;
    private String productVariant;
    private String productPrice;
    private String productImage;
    private int quantity;
    private boolean isSelected;

    public CartItem(String productName, String productVariant, String productPrice, String productImage, int quantity, boolean isSelected) {
        this.productName = productName;
        this.productVariant = productVariant;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.quantity = quantity;
        this.isSelected = isSelected;
    }

    // Getter and Setter methods
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductVariant() {
        return productVariant;
    }

    public void setProductVariant(String productVariant) {
        this.productVariant = productVariant;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
