package vn.iotstar.ecoveggieapp.activities;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;
import org.osmdroid.views.MapView;

import java.util.ArrayList;

import vn.iotstar.ecoveggieapp.R;

public class SelectAddressActivity extends AppCompatActivity {

    private TextView txtTitle, txtProvince, txtDistrict, txtWards;
    private ListView listProvinces;
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private enum Level { PROVINCE, DISTRICT, WARD }
    private Level currentLevel = Level.PROVINCE;
    private String selectedProvinceId = "";
    private String selectedDistrictId = "";


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

    }

    private void loadProvincesFromAPI() {
        txtTitle.setText("Tỉnh/Thành phố");
        currentLevel = Level.PROVINCE;
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


    private void initView() {
        txtProvince = findViewById(R.id.txtProvince);
        txtDistrict = findViewById(R.id.txtDistrict);
        txtWards = findViewById(R.id.txtWards);

        listProvinces = findViewById(R.id.list_provinces);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        listProvinces.setAdapter(adapter);
        txtTitle = findViewById(R.id.txtTitle);

    }
}
