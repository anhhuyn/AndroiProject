package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.android.volley.Response;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import vn.iotstar.ecoveggieapp.R;

import vn.iotstar.ecoveggieapp.adapters.ProductImageAdapter;
import vn.iotstar.ecoveggieapp.adapters.ThumbnailAdapter;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.CheckoutItemModel;
import vn.iotstar.ecoveggieapp.models.ProductModel;

public class ProductDetailActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TextView productName, productPrice, productDescription, sellerRating, productSale, productCount, productUnit;
    private TextView btnAddToCart, btnBuyNow;
    private LinearLayout bottomNavigation, layoutReview;
    private ProductModel product;
    private RecyclerView recyclerViewThumbnails;
    private ImageView btnBack;
    private double price;
    private int stock;
    private String unit;
    private String imageUrl;
    private int productId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_product_detail);

        productId = getIntent().getIntExtra("product_id", -1);
        //Ánh xạ
        initView();

        // Lấy chi tiết sản phẩm từ API
        fetchProductDetail(productId);

        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại activity trước đó
        });

        btnBuyNow.setOnClickListener(v -> {
            showBuyNowBottomSheet();
        });
        btnAddToCart.setOnClickListener(v -> {
            showAddToCartBottomSheet();  // Gọi phương thức hiển thị BottomSheet
        });

        layoutReview.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailActivity.this, ReviewProductActivity.class);
            intent.putExtra("productId", productId);
            startActivity(intent);
        });

    }

    public void initView()
    {
        viewPager = findViewById(R.id.viewPagerImages);
        productName = findViewById(R.id.productName);
        productPrice = findViewById(R.id.productPrice);
        productSale = findViewById(R.id.productSales);
        productDescription = findViewById(R.id.productDescription);
        sellerRating = findViewById(R.id.sellerRating);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        btnBuyNow = findViewById(R.id.btnBuy);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        productCount = findViewById(R.id.productCount);
        productUnit = findViewById(R.id.productUnit);
        recyclerViewThumbnails = findViewById(R.id.recyclerViewThumbnails);
        btnBack = findViewById(R.id.btnBack);
        layoutReview = findViewById(R.id.layoutReview);
    }

    private void fetchProductDetail(int productId) {
        // Tạo URL API để lấy chi tiết sản phẩm
        String url = "http://"+ StringHelper.SERVER_IP+":9080/api/v1/products/" + productId;
        Log.d("ProductDetail", "Request URL: " + url);


        // Khởi tạo RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Tạo request với URL để lấy chi tiết sản phẩm
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Lấy thông tin từ JSON
                            String name = response.getString("product_name");
                            price = response.getDouble("price");
                            String description = response.isNull("description") ? "Chưa có mô tả sản phẩm này." : response.getString("description");
                            stock = response.getInt("instock_quantity");
                            int soldQuantity = response.getInt("sold_quantity");
                            unit = response.getString("unit");

                            // In log các giá trị lấy được
                            Log.d("ProductDetail", "Product Name: " + name);
                            Log.d("ProductDetail", "Price: " + price);
                            Log.d("ProductDetail", "Description: " + description);
                            Log.d("ProductDetail", "Stock: " + stock);
                            Log.d("ProductDetail", "Sold Quantity: " + soldQuantity);

                            // Cập nhật thông tin sản phẩm
                            productName.setText(name);
                            productPrice.setText("₫" + price);
                            productDescription.setText("Mô tả sản phẩm: \n"+description);
                            productSale.setText("Đã bán " + soldQuantity);
                            productCount.setText("Số lượng còn lại: "+ stock);
                            productUnit.setText("Đơn vị tính: "+unit);

                            // Lấy hình ảnh sản phẩm
                            JSONArray imagesArray = response.getJSONArray("productImages");
                            List<String> productImages = new ArrayList<>();
                            for (int i = 0; i < imagesArray.length(); i++) {
                                JSONObject imageJson = imagesArray.getJSONObject(i);
                                imageUrl = imageJson.getString("product_image");
                                productImages.add(imageUrl);
                                Log.d("ProductDetail", "Image URL: " + imageUrl);

                            }

                            ProductImageAdapter productImageAdapter = new ProductImageAdapter(ProductDetailActivity.this, productImages);
                            viewPager.setAdapter(productImageAdapter);

                            ThumbnailAdapter thumbnailAdapter = new ThumbnailAdapter(ProductDetailActivity.this, productImages, position -> {
                                viewPager.setCurrentItem(position); // Khi bấm thumbnail thì đổi hình lớn
                            });
                            recyclerViewThumbnails.setLayoutManager(new LinearLayoutManager(ProductDetailActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            recyclerViewThumbnails.setAdapter(thumbnailAdapter);


                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProductDetailActivity.this, "Error parsing product data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error.networkResponse != null) {
                            int statusCode = error.networkResponse.statusCode;
                            Log.e("ProductDetail", "HTTP Status Code: " + statusCode);
                        }
                        error.printStackTrace();
                        Toast.makeText(ProductDetailActivity.this, "Error fetching product data.", Toast.LENGTH_SHORT).show();
                    }

                });

        queue.add(jsonObjectRequest);
    }

    // Xử lý BottomSheet
    private void showBuyNowBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(R.layout.layout_bottom_buy_now, null);
        dialog.setContentView(view);

        ImageView imgProduct = view.findViewById(R.id.imgProduct);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        TextView tvStock = view.findViewById(R.id.tvStock);
        TextView txtQuantity = view.findViewById(R.id.txtQuantity);
        ImageView btnClose = view.findViewById(R.id.btnClose);
        TextView btnMinus = view.findViewById(R.id.btnMinus);
        TextView btnPlus = view.findViewById(R.id.btnPlus);
        TextView btnBuy = view.findViewById(R.id.btnBuy);

        tvPrice.setText("₫" + price);
        tvStock.setText("Kho: " + stock);

        Glide.with(this)
                .load(imageUrl)
                .into(imgProduct);

        btnClose.setOnClickListener(v -> dialog.dismiss());

        btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(txtQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(txtQuantity.getText().toString());
            if (quantity < stock) {
                quantity++;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        btnBuy.setOnClickListener(v -> {
            int quantity = Integer.parseInt(txtQuantity.getText().toString());

            ArrayList<CheckoutItemModel> itemList = new ArrayList<>();
            itemList.add(new CheckoutItemModel(
                    productId,
                    productName.getText().toString(),
                    unit,
                    price,
                    quantity,
                    imageUrl
            ));

            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("checkout_items", itemList);
            startActivity(intent);


            dialog.dismiss();
        });

        dialog.setCancelable(true);
        dialog.show();
    }

    private void showAddToCartBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);

        // Inflate layout bottom sheet
        View view = getLayoutInflater().inflate(R.layout.activity_product_to_cart, null);
        dialog.setContentView(view);

        // Initialize views
        ImageView imgProduct = view.findViewById(R.id.image_product);
        TextView tvPrice = view.findViewById(R.id.text_price);
        TextView tvStock = view.findViewById(R.id.text_stock);
        TextView txtQuantity = view.findViewById(R.id.txtQuantity);
        TextView btnMinus = view.findViewById(R.id.btnMinus);
        TextView btnPlus = view.findViewById(R.id.btnPlus);
        TextView btnAddToCart = view.findViewById(R.id.button_add_to_cart);

        // Set product details
        tvPrice.setText("₫" + price);
        tvStock.setText("Kho: " + stock);

        // Load product image using Glide
        Glide.with(this)
                .load(imageUrl)
                .into(imgProduct);

        // Handle quantity adjustments
        btnMinus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(txtQuantity.getText().toString());
            if (quantity > 1) {
                quantity--;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        btnPlus.setOnClickListener(v -> {
            int quantity = Integer.parseInt(txtQuantity.getText().toString());
            if (quantity < stock) {
                quantity++;
                txtQuantity.setText(String.valueOf(quantity));
            }
        });

        // Handle adding to cart and calling API
        btnAddToCart.setOnClickListener(v -> {
            int quantity = Integer.parseInt(txtQuantity.getText().toString());
            int userId = SharedPrefManager.getInstance(this).getUserId();

            // Create the API URL
            String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/cart/add?user_id=" + userId + "&product_id=" + productId + "&quantity=" + quantity;

            // Create the request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        // Handle success
                        Toast.makeText(ProductDetailActivity.this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    },
                    error -> {
                        // Handle error
                        Toast.makeText(ProductDetailActivity.this, "Lỗi khi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    });

            // Add the request to the Volley request queue
            RequestQueue queue = Volley.newRequestQueue(ProductDetailActivity.this);
            queue.add(stringRequest);
        });

        dialog.setCancelable(true);
        dialog.show();
    }

}

