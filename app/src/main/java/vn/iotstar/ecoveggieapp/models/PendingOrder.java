package vn.iotstar.ecoveggieapp.models;

public class PendingOrder {
    private int orderId;
    private double totalAmount;
    private String paymentMethod;
    private String note;
    private String createdAt;
    private String status;

    private String productName;
    private String unit;
    private double price;
    private int firstProductQuantity;
    private String productImage;

    private int totalQuantityInOrder;
    private int totalItemsInOrder;

    // Getters & Setters

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFirstProductQuantity() {
        return firstProductQuantity;
    }

    public void setFirstProductQuantity(int firstProductQuantity) {
        this.firstProductQuantity = firstProductQuantity;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getTotalQuantityInOrder() {
        return totalQuantityInOrder;
    }

    public void setTotalQuantityInOrder(int totalQuantityInOrder) {
        this.totalQuantityInOrder = totalQuantityInOrder;
    }

    public int getTotalItemsInOrder() {
        return totalItemsInOrder;
    }

    public void setTotalItemsInOrder(int totalItemsInOrder) {
        this.totalItemsInOrder = totalItemsInOrder;
    }
}
