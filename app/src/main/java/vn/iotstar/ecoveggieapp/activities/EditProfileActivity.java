package vn.iotstar.ecoveggieapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Layout;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.gson.GsonBuilder;
import vn.iotstar.ecoveggieapp.helpers.RealPathUtil;


import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView btnBack, imgAvatar, imgEditAvatar;
    private EditText edtUsername, edtGender, edtBirthDate;
    private TextView txtPhone, txtEmail, txtSave;
    private LinearLayout layoutPassword;
    private static final int PICK_IMAGE_REQUEST = 1;  // Mã yêu cầu cho việc chọn ảnh
    private static final int REQUEST_PERMISSION = 100; // Mã yêu cầu quyền

    private String avatar; // Biến để lưu ảnh khi chọn từ thư viện
    String fileName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ánh xạ các view
        initViews();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
        imgEditAvatar.setOnClickListener(v -> openImagePicker());

        // Lấy dữ liệu từ SharedPrefManager
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        int userId = prefManager.getUserId();
        String username = prefManager.getUsername();
        String email = prefManager.getEmail();
        String phone = prefManager.getPhone();
        avatar = prefManager.getAvatar();  // Lấy avatar từ SharedPreferences
        String gender = prefManager.getGender();
        String birthday = prefManager.getBirthday();

        // Hiển thị thông tin người dùng lên màn hình
        edtUsername.setText(username);
        txtEmail.setText(email);
        txtPhone.setText(phone);
        edtGender.setText(gender);
        if (birthday != null && !birthday.isEmpty()) {
            String formattedBirthday = formatDate(birthday);
            edtBirthDate.setText(formattedBirthday);
        }
        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(this)   // Sử dụng Glide để tải ảnh
                    .load("http://"+ StringHelper.SERVER_IP+":9080/uploads/" + avatar)  // Avatar là URL hoặc path đến hình ảnh
                    .placeholder(R.drawable.ic_avatar) // Avatar mặc định trong khi tải
                    .error(R.drawable.ic_avatar) // Avatar mặc định nếu có lỗi khi tải
                    .circleCrop()  // Crop ảnh thành hình tròn
                    .into(imgAvatar);  // Đặt vào ImageView
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar); // Avatar mặc định nếu không có
        }

        // Sự kiện nhấn nút cập nhật
        txtSave.setOnClickListener(v -> updateUserInfo(userId));

        layoutPassword.setOnClickListener(v -> {

            if (!email.isEmpty()) {
                Intent intent = new Intent(EditProfileActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", email); // Gửi email qua Intent
                intent.putExtra("action_type", "reset");
                startActivity(intent);
            } else {
                Toast.makeText(EditProfileActivity.this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void openImagePicker() {
        // Mở thư viện ảnh
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            // Nhận dữ liệu từ Intent (URI của ảnh)
            Uri imageUri = data.getData();

            // Hiển thị ảnh đã chọn trong ImageView
            Glide.with(this)
                    .load(imageUri)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .circleCrop()
                    .into(imgAvatar);

            // Lưu lại URI hoặc chuyển ảnh thành String (để lưu vào SharedPreferences hoặc gửi lên server)
            avatar = imageUri.toString(); // Cập nhật avatar nếu người dùng chọn ảnh mới
            SharedPrefManager.getInstance(this).updateAvatar(avatar); // Cập nhật avatar vào SharedPreferences
            Log.d("Avatar", "New Avatar: " + avatar);
        }
    }

    // Phương thức ánh xạ các view
    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);
        edtUsername = findViewById(R.id.edtUsername);
        edtGender = findViewById(R.id.edtGender);
        edtBirthDate = findViewById(R.id.edtBirthDate);
        txtPhone = findViewById(R.id.txtPhone);
        txtEmail = findViewById(R.id.txtEmail);
        txtSave = findViewById(R.id.txtSave);
        imgEditAvatar = findViewById(R.id.edit_avatar);
        layoutPassword = findViewById(R.id.layoutChangePass);
    }

    private void updateUserInfo(int userId) {
        String username = edtUsername.getText().toString().trim();
        String gender = edtGender.getText().toString().trim();
        String birthday = edtBirthDate.getText().toString().trim();


        if (username.isEmpty() || gender.isEmpty() || birthday.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedBirthday = convertToServerDateFormat(birthday);

        // Chuẩn bị avatar (nếu có)
        MultipartBody.Part avatarPart = null;
        if (avatar != null) {
            try {
                Uri imageUri = Uri.parse(avatar);
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    byte[] imageBytes = new byte[inputStream.available()];
                    inputStream.read(imageBytes);
                    inputStream.close();

                    RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);
                    fileName = "avatar_" + System.currentTimeMillis() + ".jpg"; // tên file giả định
                    avatarPart = MultipartBody.Part.createFormData("avatar", fileName, requestFile);
                    SharedPrefManager.getInstance(this).updateAvatar(fileName); // Cập nhật avatar vào SharedPreferences
                    Log.d("Avatar", "New Avatar: " + fileName);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(
                        new GsonBuilder().setLenient().create()
                ))
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        RequestBody userIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userId));
        RequestBody usernamePart = RequestBody.create(MediaType.parse("text/plain"), username);
        RequestBody genderPart = RequestBody.create(MediaType.parse("text/plain"), gender);
        RequestBody birthdayPart = RequestBody.create(MediaType.parse("text/plain"), formattedBirthday);

        Call<String> call = apiService.updateUserInfoWithAvatar(
                userIdPart,
                usernamePart,
                genderPart,
                birthdayPart,
                avatarPart
        );

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    if ("success".equals(response.body())) {
                        SharedPrefManager.getInstance(EditProfileActivity.this)
                                .updateUserInfo(userId, username, gender, formattedBirthday,fileName );

                        Toast.makeText(EditProfileActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(EditProfileActivity.this, "Cập nhật thất bại!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfileActivity.this, "Lỗi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(EditProfileActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private String getRealPathFromURI(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    // Phương thức chuyển đổi ngày tháng
    private String convertToServerDateFormat(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString;  // Nếu có lỗi, trả về ngày ban đầu
        }
    }

    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());  // Định dạng ngày ban đầu (nếu có dạng yyyy-MM-dd)
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Định dạng ngày mới
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return dateString;  // Nếu có lỗi, trả về ngày ban đầu
        }

    }


}
