package vn.iotstar.ecoveggieapp.models;

import com.google.gson.annotations.SerializedName;

public class AddressModel {

    @SerializedName("id")
    private int id;

    @SerializedName("user")
    private User user;

    @SerializedName("phone")
    private String phone;

    @SerializedName("detail")
    private String detail;

    @SerializedName("wards")
    private String wards;

    @SerializedName("district")
    private String district;

    @SerializedName("province")
    private String province;

    @SerializedName("is_default")  // Thêm trường is_default
    private boolean isDefault;

    // Getter cho ID
    public int getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getPhone() {
        return phone;
    }

    public String getDetail() {
        return detail;
    }

    public String getWards() {
        return wards;
    }

    public String getDistrict() {
        return district;
    }

    public String getProvince() {
        return province;
    }

    public boolean isDefault() {   // Getter cho is_default
        return isDefault;
    }

    // Setters cho các trường
    public void setId(int id) {
        this.id = id;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setWards(String wards) {
        this.wards = wards;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }

    // Inner class User
    public static class User {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("username")
        private String username;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
