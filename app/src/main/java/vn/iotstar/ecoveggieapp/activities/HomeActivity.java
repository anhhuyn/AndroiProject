package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.ProductAdapter;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.ProductModel;

public class HomeActivity extends AppCompatActivity {

    TextView txtHello, txtLogout, txtPopular, txtNewest, txtBestselling, txtPrice;
    private TextView selectedCategory;
    RecyclerView recyclerView;
    ProductAdapter productAdapter;
    List<ProductModel> productList;
    // Biến kiểm tra trạng thái sắp xếp giá
    private boolean isPriceDescending = true;
    private ImageView iconSort;
    private EditText edtSearchbar;
    private TextView txtEmptyMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        LinearLayout layoutCategory = findViewById(R.id.layout_category); // Lấy LinearLayout
        LinearLayout layoutProfile = findViewById(R.id.layout_profile);
        layoutProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        txtHello = findViewById(R.id.txtHello);
        txtLogout = findViewById(R.id.txtLogout);
        txtNewest = findViewById(R.id.txtNewest);
        txtPopular = findViewById(R.id.txtPopular);
        txtBestselling = findViewById(R.id.txtBestselling);
        txtPrice = findViewById(R.id.txtPrice);
        recyclerView = findViewById(R.id.recyclerView);
        iconSort = findViewById(R.id.icon_sort);
        edtSearchbar = findViewById(R.id.edtSearchbar);
        edtSearchbar.setInputType(InputType.TYPE_CLASS_TEXT);
        txtEmptyMessage = findViewById(R.id.txtEmptyMessage);


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

        txtBestselling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedCategory(txtBestselling);
                fetchProductsBySoldQuantityDesc();
            }
        });

        // Tìm LinearLayout theo ID
        LinearLayout layoutStore = findViewById(R.id.layout_store);

        // Bắt sự kiện nhấp vào LinearLayout
        layoutStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StoreActivity.class);
                startActivity(intent);
            }
        });

        edtSearchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });



        txtPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedCategory(txtPrice);

                if (isPriceDescending) {
                    fetchProductsByPriceDesc();
                    iconSort.setImageResource(R.drawable.ic_arrow_down);
                } else {
                    fetchProductsByPriceAsc();
                    iconSort.setImageResource(R.drawable.ic_arrow_up);
                }

                // Đảo trạng thái
                isPriceDescending = !isPriceDescending;
            }
        });

        int categoryId = getIntent().getIntExtra("category_id", -1);
        String categoryName = getIntent().getStringExtra("category_name");

        if (categoryId != -1) {
            fetchProductsByCategory(categoryId);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Cập nhật Intent mới

        int categoryId = intent.getIntExtra("category_id", -1);
        if (categoryId != -1) {
            setSelectedCategory(txtPopular); // Đặt lại UI
            fetchProductsByCategory(categoryId);
        } else {
            fetchAllProducts();
        }
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

        // Nếu chọn danh mục khác ngoài "Giá", reset icon về mặc định
        if (selectedCategory != txtPrice) {
            iconSort.setImageResource(R.drawable.ic_sort);
        }
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
                                int stock = productJson.getInt("sold_quantity");

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
                            // Kiểm tra danh sách trống
                            if (productList.isEmpty()) {
                                txtEmptyMessage.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                txtEmptyMessage.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                            }
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

    private void searchProducts(String query) {
        if (query.isEmpty()) {
            fetchAllProducts();
            return;
        }

        String searchUrl = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/products/search?name=" + query;
        fetchProducts(searchUrl);
    }


    private void fetchProductsByCategory(int categoryId) {
        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/products/category/" + categoryId;
        fetchProducts(url);
    }


    private void fetchProductsByPriceAsc() {
        fetchProducts("http://" + StringHelper.SERVER_IP +":9080/api/v1/products/price/asc");
    }

    // Gọi API sắp xếp theo giá giảm dần
    private void fetchProductsByPriceDesc() {
        fetchProducts("http://" + StringHelper.SERVER_IP +":9080/api/v1/products/price/desc");
    }


    // Gọi phương thức này với URL tương ứng
    private void fetchAllProducts() {
        fetchProducts("http://" + StringHelper.SERVER_IP +":9080/api/v1/products/all");
    }

    private void fetchProductsNewest() {
        fetchProducts("http://" + StringHelper.SERVER_IP +":9080/api/v1/products/newest");
    }

    private void fetchProductsBySoldQuantityDesc() {
        fetchProducts("http://" + StringHelper.SERVER_IP +":9080/api/v1/products/sold/desc");
    }



    public void signUserOut() {
        txtHello.setText("Hello!");
        Intent goToSignup = new Intent(HomeActivity.this, MainActivity.class);
        startActivity(goToSignup);
        finish();
    }
}
