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

    // Inner class User
    public static class User {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("username")
        private String username;

        public int getUserId() {
            return userId;
        }

        public String getUsername() {
            return username;
        }
    }
}
