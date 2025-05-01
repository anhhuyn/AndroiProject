package vn.iotstar.ecoveggieapp.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.osmdroid.views.MapView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import android.os.Looper;



public class SelectAddressActivity extends AppCompatActivity {

    private TextView txtTitle, txtProvince, txtDistrict, txtWards, txtDone;
    private ImageView imgProvince, imgDistrict, imgWards;
    private ListView listProvinces;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private enum Level { PROVINCE, DISTRICT, WARD }
    private Level currentLevel = Level.PROVINCE;
    private String selectedProvinceId = "";
    private String selectedDistrictId = "";
    private LinearLayout layoutCheckin;
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_address);

        String province = getIntent().getStringExtra("province");
        String district = getIntent().getStringExtra("district");
        String wards = getIntent().getStringExtra("wards");

        initView();

        txtProvince.setOnClickListener(v -> {
            currentLevel = Level.PROVINCE;
            loadProvincesFromAPI();
        });

        txtDistrict.setOnClickListener(v -> {
            if (!selectedProvinceId.isEmpty()) {
                currentLevel = Level.DISTRICT;
                loadDistrictsFromAPI(selectedProvinceId);
            }
        });

        txtWards.setOnClickListener(v -> {
            if (!selectedDistrictId.isEmpty()) {
                currentLevel = Level.WARD;
                loadWardsFromAPI(selectedDistrictId);
            }
        });

        layoutCheckin.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                return;
            }

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(1000);
            locationRequest.setFastestInterval(500);
            locationRequest.setNumUpdates(1);

            fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null || locationResult.getLastLocation() == null) {
                        Toast.makeText(SelectAddressActivity.this, "Không thể lấy vị trí. Thử lại sau.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double lat = locationResult.getLastLocation().getLatitude();
                    double lng = locationResult.getLastLocation().getLongitude();

                    Geocoder geocoder = new Geocoder(SelectAddressActivity.this);
                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                        if (!addresses.isEmpty()) {
                            Address address = addresses.get(0);
                            txtProvince.setText(address.getAdminArea());     // Tỉnh
                            txtDistrict.setText(address.getSubAdminArea());  // Quận/Huyện
                            txtWards.setText(address.getSubLocality());      // Phường/Xã
                        } else {
                            Toast.makeText(SelectAddressActivity.this, "Không tìm thấy địa chỉ từ tọa độ.", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(SelectAddressActivity.this, "Lỗi khi lấy địa chỉ.", Toast.LENGTH_SHORT).show();
                    }
                }
            }, Looper.getMainLooper());
        });

        txtProvince.setText(province);
        txtDistrict.setText(district);
        txtWards.setText(wards);

        loadProvincesFromAPI();

        listProvinces.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = names.get(position);
            String selectedId = ids.get(position);

            switch (currentLevel) {
                case PROVINCE:
                    txtProvince.setText(selectedName);
                    selectedProvinceId = selectedId;
                    currentLevel = Level.DISTRICT;
                    loadDistrictsFromAPI(selectedProvinceId);
                    break;
                case DISTRICT:
                    txtDistrict.setText(selectedName);
                    selectedDistrictId = selectedId;
                    currentLevel = Level.WARD;
                    loadWardsFromAPI(selectedDistrictId);
                    break;
                case WARD:
                    txtWards.setText(selectedName);
                    // Sau khi chọn phường có thể xử lý thêm nếu cần
                    break;
            }
        });

        txtDone.setOnClickListener(v -> {
            String provinceString = txtProvince.getText().toString();
            String districtString = txtDistrict.getText().toString();
            String wardsString = txtWards.getText().toString();

            android.content.Intent resultIntent = new android.content.Intent();
            resultIntent.putExtra("province", provinceString);
            resultIntent.putExtra("district", districtString);
            resultIntent.putExtra("wards", wardsString);

            setResult(RESULT_OK, resultIntent);
            finish();
        });



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                layoutCheckin.performClick(); // gọi lại để lấy vị trí
            } else {
                Toast.makeText(this, "Vui lòng cấp quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void loadProvincesFromAPI() {
        txtTitle.setText("Tỉnh/Thành phố");
        currentLevel = Level.PROVINCE;
        updateSelectionUI();
        names.clear();
        ids.clear();

        String url = "https://esgoo.net/api-tinhthanh/1/0.htm";
        RequestQueue queue = Volley.newRequestQueue(this);

        com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest =
                new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                org.json.JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject obj = dataArray.getJSONObject(i);
                                    names.add(obj.getString("name"));
                                    ids.add(obj.getString("id"));
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> error.printStackTrace()
                );

        queue.add(jsonObjectRequest);
    }

    private void loadDistrictsFromAPI(String provinceId) {
        txtTitle.setText("Quận/Huyện");
        currentLevel = Level.DISTRICT;
        updateSelectionUI();
        names.clear();
        ids.clear();

        String url = "https://esgoo.net/api-tinhthanh/2/" + provinceId + ".htm";
        RequestQueue queue = Volley.newRequestQueue(this);

        com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest =
                new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                org.json.JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject obj = dataArray.getJSONObject(i);
                                    names.add(obj.getString("name"));
                                    ids.add(obj.getString("id"));
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> error.printStackTrace()
                );

        queue.add(jsonObjectRequest);
    }

    private void loadWardsFromAPI(String districtId) {
        txtTitle.setText("Phường/Xã");
        currentLevel = Level.WARD;
        updateSelectionUI();
        names.clear();
        ids.clear();

        String url = "https://esgoo.net/api-tinhthanh/3/" + districtId + ".htm";
        RequestQueue queue = Volley.newRequestQueue(this);

        com.android.volley.toolbox.JsonObjectRequest jsonObjectRequest =
                new com.android.volley.toolbox.JsonObjectRequest(Request.Method.GET, url, null,
                        response -> {
                            try {
                                org.json.JSONArray dataArray = response.getJSONArray("data");
                                for (int i = 0; i < dataArray.length(); i++) {
                                    JSONObject obj = dataArray.getJSONObject(i);
                                    names.add(obj.getString("name"));
                                    ids.add(obj.getString("id"));
                                }
                                adapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },
                        error -> error.printStackTrace()
                );

        queue.add(jsonObjectRequest);
    }

    private void updateSelectionUI() {
        int green = getResources().getColor(R.color.green_main);
        int gray = getResources().getColor(android.R.color.darker_gray);

        txtProvince.setTextColor(currentLevel == Level.PROVINCE ? green : gray);
        imgProvince.setImageResource(currentLevel == Level.PROVINCE ? R.drawable.ic_radio_select : R.drawable.ic_radio);

        txtDistrict.setTextColor(currentLevel == Level.DISTRICT ? green : gray);
        imgDistrict.setImageResource(currentLevel == Level.DISTRICT ? R.drawable.ic_radio_select : R.drawable.ic_radio);

        txtWards.setTextColor(currentLevel == Level.WARD ? green : gray);
        imgWards.setImageResource(currentLevel == Level.WARD ? R.drawable.ic_radio_select : R.drawable.ic_radio);
    }



    private void initView() {
        txtProvince = findViewById(R.id.txtProvince);
        txtDistrict = findViewById(R.id.txtDistrict);
        txtWards = findViewById(R.id.txtWards);

        listProvinces = findViewById(R.id.list_provinces);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listProvinces.setAdapter(adapter);
        txtTitle = findViewById(R.id.txtTitle);
        imgProvince = findViewById(R.id.imgProvince);
        imgDistrict = findViewById(R.id.imgDistrict);
        imgWards = findViewById(R.id.imgWards);
        txtDone = findViewById(R.id.txtDone);
        layoutCheckin = findViewById(R.id.layout_checkin);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    }
}
