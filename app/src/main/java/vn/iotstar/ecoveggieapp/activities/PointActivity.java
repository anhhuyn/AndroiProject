package vn.iotstar.ecoveggieapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PointActivity extends AppCompatActivity {

    private TextView txtTotalPoint, btnReceive;
    private List<LinearLayout> dayLayouts;
    private ImageView btnBack;
    private int countDay, newPoint = 0;
    private ImageView imgPoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accumulate);

        int userId = SharedPrefManager.getInstance(this).getUserId();

        // Anh xa
        txtTotalPoint = findViewById(R.id.txtTotalPoint);
        btnReceive = findViewById(R.id.btnReceive);
        btnBack = findViewById(R.id.btnBack);
        imgPoint = findViewById(R.id.imgPoint);


        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại activity trước đó
        });


        // Danh sách layout 7 ngày
        dayLayouts = Arrays.asList(
                findViewById(R.id.layout_1),
                findViewById(R.id.layout_2),
                findViewById(R.id.layout_3),
                findViewById(R.id.layout_4),
                findViewById(R.id.layout_5),
                findViewById(R.id.layout_6),
                findViewById(R.id.layout_7)
        );

        fetchPointData(userId);

        btnReceive.setOnClickListener(v -> {
            updateUserPoint(); // Gọi hàm xử lý cập nhật điểm
        });


    }

    private void updateUserPoint() {
        int userId = SharedPrefManager.getInstance(this).getUserId();
        int currentPoint = Integer.parseInt(txtTotalPoint.getText().toString());


        if (countDay >= 0 && countDay <= 2 || countDay ==6) {
            newPoint = currentPoint + 100;
        } else if (countDay == 3 || countDay == 4) {
            newPoint = currentPoint + 200;
        } else if (countDay == 5) {
            newPoint = currentPoint + 500;
        } else {
            newPoint = 0;
        }


        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/points/update";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {

                },
                error -> {
                    Toast.makeText(this, "Lỗi khi nhận điểm", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("total_points", String.valueOf(newPoint));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        fetchPointData(userId);
        Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.scale_up);
        imgPoint.startAnimation(scaleUp);
    }


    private void fetchPointData(int userId) {
        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/points/"+userId; // thay URL thật

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject obj = response.getJSONObject(

                                    0);
                            int total = obj.getInt("totalPoints");
                            countDay = obj.getInt("countDay");

                            txtTotalPoint.setText(String.valueOf(total));

                            for (int i = 0; i < 7; i++) {
                                if (i == countDay) {
                                    dayLayouts.get(i).setBackgroundColor(Color.parseColor("#FFCDD2"));
                                } else {
                                    dayLayouts.get(i).setBackgroundColor(getResources().getColor(R.color.gray));
                                }
                            }

                            String updatedAt = obj.optString("updatedAt", obj.optString("createdAt")); // lấy updatedAt nếu có, không thì lấy createdAt
                            if (updatedAt != null && !updatedAt.equals("null")) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                String today = sdf.format(new Date());
                                String updatedDate = updatedAt.substring(0, 10); // cắt phần yyyy-MM-dd

                                if (today.equals(updatedDate)) {
                                    btnReceive.setEnabled(false);
                                    btnReceive.setText("Bạn đã nhận điểm hôm nay");
                                    btnReceive.setBackgroundColor(Color.GRAY);
                                }
                            }

                        } else {
                            Toast.makeText(this, "Không có dữ liệu điểm", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi phân tích JSON", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi kết nối API", Toast.LENGTH_SHORT).show());

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
