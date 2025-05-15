package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;

public class ProfileActivity extends AppCompatActivity {

    private LinearLayout layoutAccount, layoutHome, layoutLogout, layoutPoint, layoutAddress, layoutReview, layoutConfirm, layoutDelivery, layoutShipping, order_title_section, order_section, my_utilities_section, account, address;
    private ImageView editAccount, imgAvatar;
    private TextView txtUsername, txtOrder, txtLogin;
    private TextView confirmBadge, pickupBadge, deliveryBadge;
    private int userId = -1;


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
        loadOrderCounts();

        // Nhận dữ liệu từ Intent
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        userId = prefManager.getUserId();
        String username = prefManager.getUsername();

        String email = prefManager.getEmail();
        String phone = prefManager.getPhone();
        String password = prefManager.getPassword();
        String avatar = prefManager.getAvatar();
        String gender = prefManager.getGender();
        String birthday = prefManager.getBirthday();

        Log.d("UserInfo", "userId = " + userId);

        if(userId == -1)
        {
            txtLogin.setVisibility(View.VISIBLE);
            txtUsername.setVisibility(View.GONE);
            layoutLogout.setVisibility(View.GONE);
            editAccount.setClickable(false);
            order_title_section.setClickable(false);
            order_section.setClickable(false);
            my_utilities_section.setClickable(false);
            account.setClickable(false);
            address.setClickable(false);
            layoutConfirm.setClickable(false);
            layoutDelivery.setClickable(false);
            layoutShipping.setClickable(false);
            layoutReview.setClickable(false);
            txtOrder.setClickable(false);
            layoutPoint.setClickable(false);

        }


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
        txtOrder = findViewById(R.id.order_history);
        confirmBadge = findViewById(R.id.confirm_badge);
        pickupBadge = findViewById(R.id.pickup_badge);
        deliveryBadge = findViewById(R.id.delivery_badge);
        layoutConfirm = findViewById(R.id.layout_confirm);
        layoutDelivery = findViewById(R.id.layout_delivery);
        layoutShipping = findViewById(R.id.layout_shipping);
        LinearLayout layoutHome = findViewById(R.id.layout_home);
        LinearLayout layoutCategory = findViewById(R.id.layout_category);
        LinearLayout layoutStore = findViewById(R.id.layout_store);
        txtLogin = findViewById(R.id.txtLogin);
        order_title_section = findViewById(R.id.order_title_section);
        order_section = findViewById(R.id.order_section);
        my_utilities_section = findViewById(R.id.my_utilities_section);
        account = findViewById(R.id.account);
        address = findViewById(R.id.address);

        layoutHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        layoutStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadOrderCounts() {
        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
        int userId = prefManager.getUserId();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP +":9080/api/v1/") // sửa URL backend phù hợp
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService orderApi = retrofit.create(ApiService.class);

        loadCountForStatus(orderApi, userId, "Pending Confirm", confirmBadge);
        loadCountForStatus(orderApi, userId, "Pending Delivery", pickupBadge);
        loadCountForStatus(orderApi, userId, "Pending Ship", deliveryBadge);
    }

    private void loadCountForStatus(ApiService orderApi, int userId, String status, TextView badge) {
        orderApi.countOrdersByStatus(userId, status).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int count = response.body();
                    if (count > 0) {
                        badge.setText(String.valueOf(count));
                        badge.setVisibility(View.VISIBLE);
                    } else {
                        badge.setVisibility(View.GONE);
                    }
                } else {
                    badge.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                badge.setVisibility(View.GONE);
            }
        });
    }


    private void setListeners() {

        layoutConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrder("Pending Confirm");
            }
        });

        layoutDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrder("Pending Delivery");
            }
        });

        layoutShipping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrder("Pending Ship");
            }
        });

        txtOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOrder();
            }
        });

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
                SharedPrefManager.getInstance(ProfileActivity.this).setRemembered(false);

                // Chuyển về màn hình đăng nhập (MainActivity hoặc LoginActivity tùy bạn)
                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Xóa stack
                startActivity(intent);
                finish(); // Kết thúc ProfileActivity
            }
        });

    }

    // Thêm overload có trạng thái
    private void goToOrder(String status) {
        Intent intent = new Intent(ProfileActivity.this, OrderActivity.class);
        intent.putExtra("order_status", status);
        startActivity(intent);
    }

    private void goToOrder() {
        Intent intent = new Intent(ProfileActivity.this, OrderActivity.class);
        startActivity(intent);
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
