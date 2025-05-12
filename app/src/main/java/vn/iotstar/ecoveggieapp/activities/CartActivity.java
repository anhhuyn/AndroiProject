package vn.iotstar.ecoveggieapp.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.graphics.Canvas;
import androidx.core.view.ViewCompat;

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

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.CartAdapter;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.CartItemModel;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<CartItemModel> cartItemList;
    private static final String API_URL = "http://" + StringHelper.SERVER_IP +":9080/api/v1/cart/user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);

        int userId = SharedPrefManager.getInstance(this).getUserId();
        getCartItems(userId);
    }


    private void getCartItems(int userId) {
        String url = API_URL + "?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        cartItemList = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject cartItemJson = response.getJSONObject(i);
                                JSONObject productJson = cartItemJson.getJSONObject("product");

                                int id = cartItemJson.getInt("id");
                                int productId = productJson.getInt("product_id");
                                String productName = productJson.getString("product_name");
                                double price = productJson.getDouble("price");
                                int quantity = cartItemJson.getInt("quantity");
                                String unit = productJson.getString("unit");

                                List<String> images = new ArrayList<>();
                                if (productJson.has("images")) {
                                    JSONArray imagesArray = productJson.getJSONArray("images");
                                    for (int j = 0; j < imagesArray.length(); j++) {
                                        images.add(imagesArray.getString(j));
                                    }
                                }

                                CartItemModel cartItem = new CartItemModel(id, productId, productName, price, quantity, images, unit);
                                cartItemList.add(cartItem);
                            }

                            setupRecyclerView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CartActivity.this, "Lỗi phân tích dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(CartActivity.this, "Lỗi tải giỏ hàng!", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonArrayRequest);
    }

    private void setupRecyclerView() {
        cartAdapter = new CartAdapter(cartItemList);
        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                int cartItemId = cartItemList.get(position).getId(); // Lấy đúng ID cart item

                String deleteUrl = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/cart/item/" + cartItemId;

                RequestQueue queue = Volley.newRequestQueue(CartActivity.this);
                com.android.volley.toolbox.StringRequest deleteRequest = new com.android.volley.toolbox.StringRequest(
                        Request.Method.DELETE,
                        deleteUrl,
                        response -> {
                            cartAdapter.deleteItem(position); // Xóa item trong UI
                            Toast.makeText(CartActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        },
                        error -> {
                            Toast.makeText(CartActivity.this, "Lỗi xóa sản phẩm", Toast.LENGTH_SHORT).show();
                            cartAdapter.notifyItemChanged(position); // Khôi phục nếu lỗi
                        }
                );

                queue.add(deleteRequest);
            }



            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View foregroundView = viewHolder.itemView.findViewById(R.id.cardItemCart);
                ViewCompat.setTranslationX(foregroundView, dX);
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerViewCart);

    }

}
