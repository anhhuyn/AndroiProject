package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vn.iotstar.ecoveggieapp.R;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView btnBack, imgAvatar;
    private EditText edtUsername, edtGender, edtBirthDate;
    private TextView txtPhone, txtEmail;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Ánh xạ các view
        initViews();

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        int userId = intent.getIntExtra("user_id", -1);
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String password = intent.getStringExtra("password");
        String avatar = intent.getStringExtra("avatar");
        String gender = intent.getStringExtra("gender");
        String birthday = intent.getStringExtra("birthday");

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
                    .load(avatar)  // Avatar là URL hoặc path đến hình ảnh
                    .placeholder(R.drawable.ic_avatar) // Avatar mặc định trong khi tải
                    .error(R.drawable.ic_avatar) // Avatar mặc định nếu có lỗi khi tải
                    .circleCrop()  // Crop ảnh thành hình tròn
                    .into(imgAvatar);  // Đặt vào ImageView
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar); // Avatar mặc định nếu không có
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
