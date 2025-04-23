package vn.iotstar.ecoveggieapp.models;

import com.google.gson.annotations.SerializedName;

public class OrderModel {

    @SerializedName("id")
    private int id;

    @SerializedName("customer_id")
    private int customerId;

    @SerializedName("total_amount")
    private double totalAmount;

    @SerializedName("payment_method")
    private String paymentMethod;

    @SerializedName("status")
    private String status;

    @SerializedName("note")
    private String note;

    @SerializedName("address_id")
    private int addressId;

    @SerializedName("created_at")
    private String createdAt;

    // Constructors
    public OrderModel() {}

    public OrderModel(int id, int customerId, double totalAmount, String paymentMethod, String status, String note, int addressId, String createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.note = note;
        this.addressId = addressId;
        this.createdAt = createdAt;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public String getNote() {
        return note;
    }

    public int getAddressId() {
        return addressId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
