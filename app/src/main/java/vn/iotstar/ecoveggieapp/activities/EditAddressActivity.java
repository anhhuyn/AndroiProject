package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.AddressAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.AddressModel;

public class EditAddressActivity extends AppCompatActivity {


    private EditText edtName, edtPhone, edtDetail;
    private TextView txtProvince, btnComplete, btnDelete;
    private Switch switchDefault;
    private MapView mapView;
    private ImageView btnZoomIn, btnZoomOut, btnBack;
    private LinearLayout layout_select_address;
    private Retrofit retrofit;
    private ApiService addressApi;
    private String wards, district, province; // Khai báo biến toàn cục
    private int addressId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_address);

        // Khởi tạo Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // URL cơ sở của API của bạn
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        addressApi = retrofit.create(ApiService.class);

        // Nhận dữ liệu từ Intent
        addressId = getIntent().getIntExtra("addressId", -1);
        String userName = getIntent().getStringExtra("userName");
        String phone = getIntent().getStringExtra("phone");
        String detail = getIntent().getStringExtra("detail");
        wards = getIntent().getStringExtra("wards"); // Lưu giá trị vào biến toàn cục
        district = getIntent().getStringExtra("district"); // Lưu giá trị vào biến toàn cục
        province = getIntent().getStringExtra("province"); // Lưu giá trị vào biến toàn cục
        boolean isDefault = getIntent().getBooleanExtra("isDefault", false);

        initView();


        // Debounce cập nhật map khi sửa edtDetail
        edtDetail.addTextChangedListener(new TextWatcher() {
            private final Handler handler = new Handler();
            private Runnable updateMapRunnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (updateMapRunnable != null) {
                    handler.removeCallbacks(updateMapRunnable);
                }

                updateMapRunnable = () -> {
                    // Lấy địa chỉ đầy đủ mới
                    String fullAddress = edtDetail.getText().toString() + ", " + txtProvince.getText().toString().replace("\n", ", ");
                    showLocationOnMap(fullAddress);
                };

                handler.postDelayed(updateMapRunnable, 2000); // Delay 1 giây sau khi người dùng ngừng gõ
            }
        });


        edtName.setText(userName);
        edtPhone.setText(phone);
        edtDetail.setText(detail);
        txtProvince.setText(province + "\n" + district + "\n" + wards);
        switchDefault.setChecked(isDefault);

        mapView = findViewById(R.id.map);
        mapView.setBuiltInZoomControls(false); // Tắt nút zoom mặc định
        Configuration.getInstance().load(getApplicationContext(), getSharedPreferences("osmdroid", MODE_PRIVATE));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

// Ghép địa chỉ đầy đủ
        String fullAddress = detail + ", " + wards + ", " + district + ", " + province;
        showLocationOnMap(fullAddress);

        btnZoomIn.setOnClickListener(v -> {
            if (mapView != null) {
                mapView.getController().zoomIn();
            }
        });

        btnZoomOut.setOnClickListener(v -> {
            if (mapView != null) {
                mapView.getController().zoomOut();
            }
        });

        btnBack.setOnClickListener(v -> {
            finish(); // Kết thúc activity hiện tại, quay về màn hình trước
        });

        layout_select_address.setOnClickListener(v -> {
            Intent intent = new Intent(EditAddressActivity.this, SelectAddressActivity.class);
            intent.putExtra("province", province);
            intent.putExtra("district", district);
            intent.putExtra("wards", wards);
            startActivityForResult(intent, 100);
        });

        findViewById(R.id.btnComplete).setOnClickListener(v -> {
            AddressModel addressModel = new AddressModel();
            addressModel.setPhone(edtPhone.getText().toString());
            addressModel.setDetail(edtDetail.getText().toString());
            addressModel.setWards(wards);
            addressModel.setDistrict(district);
            addressModel.setProvince(province);
            addressModel.setDefault(switchDefault.isChecked());

            int userId = SharedPrefManager.getInstance(this).getUserId();
            AddressModel.User user = new AddressModel.User();
            user.setUserId(userId);
            addressModel.setUser(user);


            if (addressId == -1) {
                // Thêm mới địa chỉ
                addAddress(addressModel);
            } else {
                // Cập nhật địa chỉ
                addressModel.setId(addressId);
                updateAddress(addressId, addressModel);
            }
        });

        btnDelete.setOnClickListener(v -> {
            if (addressId != -1) {
                deleteAddress(addressId);
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.onPause();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            province = data.getStringExtra("province"); // Cập nhật lại province
            district = data.getStringExtra("district"); // Cập nhật lại district
            wards = data.getStringExtra("wards"); // Cập nhật lại wards

            // Cập nhật TextView với giá trị mới
            txtProvince.setText(province + "\n" + district + "\n" + wards);

            // Cập nhật lại địa chỉ trên bản đồ
            String fullAddress = edtDetail.getText().toString() + ", " + wards + ", " + district + ", " + province;
            showLocationOnMap(fullAddress);
        }
    }

    private void addAddress(AddressModel newAddress) {
        Call<AddressModel> call = addressApi.createAddress(newAddress); // Gọi API POST

        call.enqueue(new Callback<AddressModel>() {
            @Override
            public void onResponse(Call<AddressModel> call, Response<AddressModel> response) {
                if (response.isSuccessful()) {
                    Log.d("AddAddress", "Thêm địa chỉ thành công!");
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Log.e("AddAddress", "Thêm địa chỉ thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AddressModel> call, Throwable t) {
                Log.e("AddAddress", "Lỗi khi thêm địa chỉ: " + t.getMessage());
            }
        });
    }

    private void deleteAddress(int id) {
        Call<Void> call = addressApi.deleteAddress(id);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.d("DeleteAddress", "Xóa địa chỉ thành công!");
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    finish(); // quay lại activity trước
                } else {
                    Log.e("DeleteAddress", "Xóa thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("DeleteAddress", "Lỗi kết nối khi xóa: " + t.getMessage());
            }
        });
    }



    // Gửi yêu cầu PUT tới API để cập nhật địa chỉ
    private void updateAddress(int id, AddressModel updatedAddress) {
        Call<AddressModel> call = addressApi.updateAddress(id, updatedAddress);

        call.enqueue(new Callback<AddressModel>() {
            @Override
            public void onResponse(Call<AddressModel> call, Response<AddressModel> response) {
                if (response.isSuccessful()) {
                    // Thành công, gửi kết quả về AddressActivity
                    Log.d("UpdateAddress", "Cập nhật địa chỉ thành công!");

                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK, resultIntent);
                    Log.d("EditAddressActivity", "Gọi setResult(RESULT_OK)");
                    finish();
                } else {
                    // Xử lý lỗi
                    Log.e("UpdateAddress", "Cập nhật địa chỉ thất bại: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<AddressModel> call, Throwable t) {
                // Xử lý lỗi khi kết nối không thành công
                Log.e("UpdateAddress", "Lỗi khi kết nối: " + t.getMessage());
            }
        });
    }




    private void showLocationOnMap(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address location = addresses.get(0);
                GeoPoint point = new GeoPoint(location.getLatitude(), location.getLongitude());
                mapView.getController().setZoom(20.0);
                mapView.getController().setCenter(point);

                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setTitle("Địa chỉ");
                Drawable drawable = getResources().getDrawable(R.drawable.ic_marker, null);
                Bitmap originalBitmap = ((BitmapDrawable) drawable).getBitmap();

// Resize ảnh (ví dụ: 60x60 pixels)
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 30, 30, false);

// Đặt icon mới
                marker.setIcon(new BitmapDrawable(getResources(), scaledBitmap));


                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                mapView.getOverlays().clear();
                mapView.getOverlays().add(marker);
            }
        } catch (IOException e) {
            Log.e("MapError", "Lỗi khi lấy tọa độ từ địa chỉ", e);
        }
    }


    private void initView()
    {
        edtName = findViewById(R.id.edtName);
        edtPhone = findViewById(R.id.edtPhone);
        edtDetail = findViewById(R.id.edtDetail);
        txtProvince = findViewById(R.id.tvProvince);
        switchDefault = findViewById(R.id.switchDefault);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        layout_select_address = findViewById(R.id.layout_select_address);
        btnDelete = findViewById(R.id.btnDelete);
        btnBack = findViewById(R.id.btnBack);
    }
}
