package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.AddressAdapter;
import vn.iotstar.ecoveggieapp.adapters.ProductReviewAdapter;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.AddressModel;
import vn.iotstar.ecoveggieapp.models.ProductModel;

public class ListUserReviewActivity extends AppCompatActivity {

    private ImageView imgAvatar, btnBack;
    RecyclerView recyclerView;
    private TextView txtCountReview;
    ProductReviewAdapter adapter;
    List<ProductModel> productList = new ArrayList<>();
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review);

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        int userId = prefManager.getUserId();
        String username = prefManager.getUsername();
        String avatar = prefManager.getAvatar();

        initView();

        if (avatar != null && !avatar.isEmpty()) {
            Glide.with(this)
                    .load("http://"+ StringHelper.SERVER_IP+":9080/uploads/" + avatar)
                    .placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar)
                    .circleCrop()
                    .into(imgAvatar);
        } else {
            imgAvatar.setImageResource(R.drawable.ic_avatar);
        }

        fetchUnreviewedProducts(userId);
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Gọi lại API để refresh danh sách
            int userId = SharedPrefManager.getInstance(this).getUserId();
            fetchUnreviewedProducts(userId);
        }
    }


    private void fetchUnreviewedProducts(int userId) {
        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/reviews/unreviewed?userId=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    productList.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            ProductModel product = new ProductModel();
                            product.setProductId(obj.getInt("product_id"));
                            product.setProductName(obj.getString("product_name"));
                            product.setDescription(obj.getString("description"));
                            product.setPrice(obj.getDouble("price"));
                            product.setUnit(obj.getString("unit"));

                            JSONArray imageArray = obj.getJSONArray("images");
                            // Thêm đoạn này vào sau khi lấy imageArray
                            List<String> images = new ArrayList<>();
                            for (int j = 0; j < imageArray.length(); j++) {
                                images.add(imageArray.getString(j));
                            }
                            product.setImages(images);  // thêm dòng này

                            productList.add(product);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    txtCountReview.setText(String.valueOf(productList.size()));
                    adapter.notifyDataSetChanged();
                },
                error -> {
                    Toast.makeText(ListUserReviewActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                });

        queue.add(jsonArrayRequest);
    }


    private void initView()
    {
        imgAvatar = findViewById(R.id.imgAvatar);
        recyclerView = findViewById(R.id.recyclerReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductReviewAdapter(productList, this);
        recyclerView.setAdapter(adapter);
        txtCountReview = findViewById(R.id.txtCountReview);
        btnBack = findViewById(R.id.btnBack);
    }
}