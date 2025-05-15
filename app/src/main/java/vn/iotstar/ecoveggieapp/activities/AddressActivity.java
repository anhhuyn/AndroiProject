package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.AddressAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.AddressModel;

import java.util.ArrayList;
import java.util.List;

public class AddressActivity extends AppCompatActivity {

    private RecyclerView rvAddresses;
    private AddressAdapter addressAdapter;
    private List<AddressModel> addressList = new ArrayList<>();
    private LinearLayout layoutAdd;
    private ImageView btnBack;
    private int source = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_address);
        initViews();

        source = getIntent().getIntExtra("source", -1);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Kết thúc activity hiện tại, quay về màn hình trước
        });
        int userId = SharedPrefManager.getInstance(this).getUserId();
        fetchAddresses(userId);


        setupRecyclerView();

        layoutAdd.setOnClickListener(v -> {
            Intent intent = new Intent(AddressActivity.this, EditAddressActivity.class);
            SharedPrefManager prefManager = SharedPrefManager.getInstance(this);
            String username = prefManager.getUsername();

            intent.putExtra("userName", username);
            startActivityForResult(intent, 100);
        });



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            // Gọi lại API để lấy danh sách địa chỉ mới
            Log.d("AddressActivity", "onActivityResult gọi: requestCode=" + requestCode + ", resultCode=" + resultCode);
            int userId = SharedPrefManager.getInstance(this).getUserId();
            fetchAddresses(userId);
        }
    }


    private void initViews() {
        rvAddresses = findViewById(R.id.rvAddresses);
        layoutAdd = findViewById(R.id.btnAddNewAddress);
    }

    private void setupRecyclerView() {
        rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        addressAdapter = new AddressAdapter(this, addressList); // <-- truyền context
        rvAddresses.setAdapter(addressAdapter);

        if (source == 1) {
            addressAdapter.setOnAddressClickListener(address -> {
                // Trả về CheckoutActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedAddressId", address.getId());
                Log.d("CheckoutActivity", "Địa chỉ được chọn ID1: " + address.getId());
                setResult(RESULT_OK, resultIntent);
                finish();
            });
        }
    }


    private void fetchAddresses(int userId) {
        ApiService apiService = getApiService();
        apiService.getAllAddresses(userId).enqueue(new Callback<List<AddressModel>>() {
            @Override
            public void onResponse(Call<List<AddressModel>> call, Response<List<AddressModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addressAdapter.updateAddressList(response.body());// Cập nhật danh sách địa chỉ trong adapter
                } else {
                    showToast("Không thể lấy danh sách địa chỉ.");
                }
            }

            @Override
            public void onFailure(Call<List<AddressModel>> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private ApiService getApiService() {
        return new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
