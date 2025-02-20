package vn.iotstar.ecoveggieapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView txtHello, txtLogout, txtPopular, txtNewest, txtBestselling, txtPrice;
    private TextView selectedCategory;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<ProductModel> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        LinearLayout layoutCategory = findViewById(R.id.layout_category); // Lấy LinearLayout

        txtHello = findViewById(R.id.txtHello);
        txtLogout = findViewById(R.id.txtLogout);
        txtNewest = findViewById(R.id.txtNewest);
        txtPopular = findViewById(R.id.txtPopular);
        txtBestselling = findViewById(R.id.txtBestselling);
        txtPrice = findViewById(R.id.txtPrice);
        recyclerView = findViewById(R.id.recyclerView);

        setCategoryClickListener(txtPopular);
        setCategoryClickListener(txtNewest);
        setCategoryClickListener(txtBestselling);
        setCategoryClickListener(txtPrice);

        // Đặt mặc định là "Phổ biến"
        selectedCategory = txtPopular;
        setSelectedCategory(txtPopular);

        String username = getIntent().getStringExtra("username");
        if (username == null || username.isEmpty()) {
            username = "User";
        }
        txtHello.setText("Hello, " + username + "!");

        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUserOut();
            }
        });

        // Xử lý khi nhấp vào "Danh mục"
        layoutCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        // Khởi tạo danh sách sản phẩm và adapter
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList);

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(productAdapter);

        fetchAllProducts();
        txtNewest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedCategory(txtNewest);
                fetchProductsNewest();
            }
        });

        txtPopular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedCategory(txtPopular);
                fetchAllProducts();
            }
        });

    }

    private void setCategoryClickListener(final TextView textView) {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedCategory(textView);
            }
        });
    }

    private void setSelectedCategory(TextView textView) {
        // Đặt màu lại cho category trước đó
        if (selectedCategory != null) {
            selectedCategory.setTextColor(Color.parseColor("#696969")); // Màu mặc định
        }

        // Cập nhật trạng thái
        selectedCategory = textView;
        selectedCategory.setTextColor(getResources().getColor(R.color.green_main));
    }


    private void fetchProducts(String url) {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        productList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject productJson = response.getJSONObject(i);

                                int id = productJson.getInt("product_id");
                                String name = productJson.getString("product_name");
                                String description = productJson.getString("description");
                                double price = productJson.getDouble("price");
                                int stock = productJson.getInt("instock_quantity");

                                String category = "Unknown";
                                if (productJson.has("category") && !productJson.isNull("category")) {
                                    JSONObject categoryJson = productJson.getJSONObject("category");
                                    if (categoryJson.has("category_name")) {
                                        category = categoryJson.getString("category_name");
                                    }
                                }

                                List<String> images = new ArrayList<>();
                                if (productJson.has("productImages") && !productJson.isNull("productImages")) {
                                    JSONArray imagesArray = productJson.getJSONArray("productImages");
                                    for (int j = 0; j < imagesArray.length(); j++) {
                                        JSONObject imageJson = imagesArray.getJSONObject(j);
                                        String imageUrl = imageJson.getString("product_image");
                                        images.add(imageUrl);
                                    }
                                }

                                ProductModel product = new ProductModel(id, name, description, price, stock, category, images);
                                productList.add(product);
                            }
                            productAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(HomeActivity.this, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(HomeActivity.this, "Lỗi tải sản phẩm!", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(jsonArrayRequest);
    }

    // Gọi phương thức này với URL tương ứng
    private void fetchAllProducts() {
        fetchProducts("http://192.168.1.19:9080/api/v1/products/all");
    }

    private void fetchProductsNewest() {
        fetchProducts("http://192.168.1.19:9080/api/v1/products/newest");
    }


    public void signUserOut() {
        txtHello.setText("Hello!");
        Intent goToSignup = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(goToSignup);
        finish();
    }
}
