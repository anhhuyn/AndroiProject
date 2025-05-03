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
    import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
    import vn.iotstar.ecoveggieapp.helpers.StringHelper;

    public class ProfileActivity extends AppCompatActivity {

        private LinearLayout layoutAccount, layoutHome, layoutLogout, layoutPoint, layoutAddress, layoutReview;
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
            SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

            int userId = prefManager.getUserId();
            String username = prefManager.getUsername();

            String email = prefManager.getEmail();
            String phone = prefManager.getPhone();
            String password = prefManager.getPassword();
            String avatar = prefManager.getAvatar();
            String gender = prefManager.getGender();
            String birthday = prefManager.getBirthday();


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
            layoutLogout = findViewById(R.id.logout);
            editAccount = findViewById(R.id.edit_avatar);
            txtUsername = findViewById(R.id.edtUsername);
            imgAvatar = findViewById(R.id.ImgAvatar);
            layoutPoint = findViewById(R.id.layout_point);
            layoutAddress = findViewById(R.id.address);
            layoutReview = findViewById(R.id.layoutReview);
        }


        private void setListeners() {
            layoutHome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToHome();
                }
            });

            layoutReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToReview();
                }
            });

            layoutAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditProfile();
                }
            });

            layoutAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToAddress();
                }
            });


            layoutPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPoint();
                }
            });

            editAccount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditProfile();
                }
            });

            layoutLogout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPrefManager.getInstance(ProfileActivity.this).clear(); // Xóa dữ liệu người dùng

                    // Chuyển về màn hình đăng nhập (MainActivity hoặc LoginActivity tùy bạn)
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack
                    startActivity(intent);
                    finish(); // Kết thúc ProfileActivity
                }
            });

        }

        private void goToReview() {
            Intent intent = new Intent(ProfileActivity.this, ListUserReviewActivity.class);
            startActivity(intent);
        }

        private void goToPoint() {
            Intent intent = new Intent(ProfileActivity.this, PointActivity.class);
            startActivity(intent);
        }

        private void goToAddress() {
            Intent intent = new Intent(ProfileActivity.this, AddressActivity.class);
            startActivity(intent);
        }

        private void goToHome() {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            startActivity(intent);
        }

        private void goToEditProfile() {
            Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);
            startActivity(intent);
        }

        @Override
        protected void onResume() {
            super.onResume();

            // Lấy lại dữ liệu từ SharedPreferences và hiển thị lên UI
            SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
            String username = prefManager.getUsername();
            String avatar = prefManager.getAvatar();

            // Hiển thị tên người dùng
            txtUsername.setText(username);

            // Cập nhật ảnh đại diện nếu có
            if (avatar != null && !avatar.isEmpty()) {
                Glide.with(this)
                        .load("http://"+ StringHelper.SERVER_IP+":9080/uploads/" + avatar)
                        .placeholder(R.drawable.ic_avatar)
                        .error(R.drawable.ic_avatar)
                        .circleCrop()
                        .into(imgAvatar);
            } else {
                imgAvatar.setImageResource(R.drawable.ic_avatar); // Avatar mặc định nếu không có
            }
        }
    }
