package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import vn.iotstar.ecoveggieapp.R;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout layoutAccount, layoutHome;
    private ImageView editAccount, imgAvatar;
    private TextView txtUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        initViews();
        setListeners();

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

        txtUsername.setText(username);
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

    private void initViews() {
        layoutHome = findViewById(R.id.layout_home);
        layoutAccount = findViewById(R.id.account);
        editAccount = findViewById(R.id.edit_avatar);
        txtUsername = findViewById(R.id.edtUsername);
        imgAvatar = findViewById(R.id.ImgAvatar);
    }

    private void setListeners() {
        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        layoutAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });
    }

    private void goToHome() {
        Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
        startActivity(intent);
    }

    private void goToEditProfile() {
        // Lấy dữ liệu từ ProfileActivity để gửi sang EditProfileActivity
        Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
        intent.putExtra("user_id", getIntent().getIntExtra("user_id", -1));
        intent.putExtra("username", getIntent().getStringExtra("username"));
        intent.putExtra("email", getIntent().getStringExtra("email"));
        intent.putExtra("phone", getIntent().getStringExtra("phone"));
        intent.putExtra("password", getIntent().getStringExtra("password"));
        intent.putExtra("avatar", getIntent().getStringExtra("avatar"));
        intent.putExtra("gender", getIntent().getStringExtra("gender"));
        intent.putExtra("birthday", getIntent().getStringExtra("birthday"));

        startActivity(intent);
    }
}
