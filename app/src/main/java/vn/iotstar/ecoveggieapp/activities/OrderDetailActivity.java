package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.CheckoutItemAdapter;
import vn.iotstar.ecoveggieapp.adapters.PendingOrderAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.CheckoutItemModel;
import vn.iotstar.ecoveggieapp.models.PendingOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class OrderDetailActivity extends AppCompatActivity {

    private TextView orderStatus, orderTime, addressDetail, totalAmount;
    private RecyclerView recyclerViewProducts;
    private CheckoutItemAdapter adapter;
    private List<CheckoutItemModel> productList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        // Ánh xạ View
        orderStatus = findViewById(R.id.order_status);
        orderTime = findViewById(R.id.order_time);
        addressDetail = findViewById(R.id.address_detail);
        totalAmount = findViewById(R.id.total_amount);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);

        int orderId = getIntent().getIntExtra("order_id", -1);
        adapter = new CheckoutItemAdapter(this, productList, item -> {
            Log.d("DEBUG_PRODUCT_ID", "Clicked productId: " + item.getProductId());
            Intent intent = new Intent(OrderDetailActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", item.getProductId());
            startActivity(intent);
        });
        recyclerViewProducts.setAdapter(adapter);


        recyclerViewProducts.setAdapter(adapter);
        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));


        getOrderDetails(orderId);

    }
    private void getOrderDetails(int orderId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // Đổi nếu chạy thật hoặc IP khác
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Map<String, Object>>> call = apiService.getOrderDetails(orderId);

        call.enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Map<String, Object>> data = response.body();

                    if (!data.isEmpty()) {
                        Map<String, Object> orderInfo = data.get(0);

                        String rawStatus = String.valueOf(orderInfo.get("status"));
                        String statusVN;

                        if (rawStatus == null || rawStatus.equalsIgnoreCase("null")) {
                            statusVN = "Không xác định";
                        } else {
                            switch (rawStatus) {
                                case "Pending Delivery":
                                    statusVN = "Đang giao hàng";
                                    break;
                                case "Pending Ship":
                                    statusVN = "Đang chờ vận chuyển";
                                    break;
                                case "Pending":
                                    statusVN = "Chờ xử lý";
                                    break;
                                case "Delivered":
                                    statusVN = "Đã giao hàng";
                                    break;
                                case "Cancel":
                                    statusVN = "Đã hủy";
                                    break;
                                default:
                                    statusVN = rawStatus;
                                    break;
                            }
                        }
                        orderStatus.setText("Trạng thái: " + statusVN);
                        String rawDate = String.valueOf(orderInfo.get("createdAt"));
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        try {
                            Date date = inputFormat.parse(rawDate);
                            // Nếu muốn hiển thị theo giờ Việt Nam
                            outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
                            String formattedDate = outputFormat.format(date);
                            orderTime.setText("Thời gian đặt hàng: " + formattedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                            orderTime.setText("Thời gian đặt hàng: không xác định");
                        }
                        String address = String.format("%s\n%s, %s, %s, %s",
                                orderInfo.get("receiverPhone"),
                                orderInfo.get("addressLine"),
                                orderInfo.get("ward"),
                                orderInfo.get("district"),
                                orderInfo.get("province"));
                        addressDetail.setText(address);
                        totalAmount.setText("Tổng tiền: ₫" + orderInfo.get("totalAmount"));

                        productList.clear();
                        for (Map<String, Object> item : data) {
                            CheckoutItemModel model = new CheckoutItemModel();
                            model.setProductId((int) Double.parseDouble(item.get("productId").toString()));
                            model.setProductName(String.valueOf(item.get("productName")));
                            model.setUnit(String.valueOf(item.get("unit")));
                            model.setPrice((int) Double.parseDouble(item.get("price").toString()));
                            model.setQuantity((int) Double.parseDouble(item.get("quantity").toString()));
                            model.setImageUrl(String.valueOf(item.get("productImage")));

                            Log.d("DEBUG", "Thêm sản phẩm: " + model.getProductName());

                            productList.add(model);
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Không có dữ liệu đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Lỗi khi tải đơn hàng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}


