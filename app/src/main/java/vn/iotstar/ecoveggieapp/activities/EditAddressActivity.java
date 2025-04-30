package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.AddressAdapter;
import vn.iotstar.ecoveggieapp.models.AddressModel;

public class EditAddressActivity extends AppCompatActivity {


    private EditText edtName, edtPhone, edtDetail;
    private TextView txtProvince;
    private Switch switchDefault;
    private MapView mapView;
    private ImageView btnZoomIn, btnZoomOut;
    private LinearLayout layout_select_address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_address);



        // Nhận dữ liệu từ Intent
        int addressId = getIntent().getIntExtra("addressId", -1);
        String userName = getIntent().getStringExtra("userName");
        String phone = getIntent().getStringExtra("phone");
        String detail = getIntent().getStringExtra("detail");
        String wards = getIntent().getStringExtra("wards");
        String district = getIntent().getStringExtra("district");
        String province = getIntent().getStringExtra("province");
        boolean isDefault = getIntent().getBooleanExtra("isDefault", false);

        initView();
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

        layout_select_address.setOnClickListener(v -> {
            Intent intent = new Intent(EditAddressActivity.this, SelectAddressActivity.class);
            intent.putExtra("province", province);
            intent.putExtra("district", district);
            intent.putExtra("wards", wards);
            startActivity(intent);
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
    }
}
