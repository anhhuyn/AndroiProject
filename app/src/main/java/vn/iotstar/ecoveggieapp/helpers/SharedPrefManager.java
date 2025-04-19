package vn.iotstar.ecoveggieapp.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefManager {
    private static final String PREF_NAME = "ecoveggie_user_data";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_AVATAR = "avatar";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_BIRTHDAY = "birthday";

    private static SharedPrefManager mInstance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;

    private SharedPrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    // Lưu thông tin người dùng vào SharedPreferences
    public void saveUser(int userId, String username, String email, String phone, String password, String avatar, String gender, String birthday) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_AVATAR, avatar);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_BIRTHDAY, birthday);
        editor.apply();
    }

    // Cập nhật thông tin người dùng vào SharedPreferences
    public void updateUserInfo(int userId, String username, String gender, String birthday, String avatar) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_BIRTHDAY, birthday);
        editor.putString(KEY_AVATAR, avatar);
        editor.apply();
    }

    // Cập nhật riêng avatar khi người dùng thay đổi avatar
    public void updateAvatar(String avatar) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_AVATAR, avatar);  // Lưu URL hoặc URI của ảnh
        editor.apply();
    }

    public int getUserId() { return sharedPreferences.getInt(KEY_USER_ID, -1); }
    public String getUsername() { return sharedPreferences.getString(KEY_USERNAME, ""); }
    public String getEmail() { return sharedPreferences.getString(KEY_EMAIL, ""); }
    public String getPhone() { return sharedPreferences.getString(KEY_PHONE, ""); }
    public String getPassword() { return sharedPreferences.getString(KEY_PASSWORD, ""); }
    public String getAvatar() { return sharedPreferences.getString(KEY_AVATAR, ""); }
    public String getGender() { return sharedPreferences.getString(KEY_GENDER, ""); }
    public String getBirthday() { return sharedPreferences.getString(KEY_BIRTHDAY, ""); }

    // Xóa tất cả dữ liệu trong SharedPreferences
    public void clear() {
        editor.clear();
        editor.apply();
    }
}
