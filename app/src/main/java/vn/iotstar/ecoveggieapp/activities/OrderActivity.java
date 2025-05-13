package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.PendingOrderAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.PendingOrder;

public class OrderActivity extends AppCompatActivity {
    private List<TextView> tabList = new ArrayList<>();
    private TextView tvEmptyMessage;

    private TextView tabPendingConfirmation, tabWaitingPickup, tabShipping, tabDelivered, tabCanceled;
    private RecyclerView recyclerView;
    private PendingOrderAdapter orderAdapter; // Adapter riêng
    private List<PendingOrder> orderList = new ArrayList<>();

    private final int customerId = 17;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initView();
        initTabClickListeners();
        loadPendingOrders();

    }
    private void initView() {
        tabPendingConfirmation = findViewById(R.id.tabPendingConfirmation);
        tabWaitingPickup = findViewById(R.id.tabWaitingPickup);
        tabShipping = findViewById(R.id.tabShipping);
        tabDelivered = findViewById(R.id.tabDelivered);
        tabCanceled = findViewById(R.id.tabCanceled);
        tvEmptyMessage = findViewById(R.id.tvEmptyMessage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        orderAdapter = new PendingOrderAdapter(orderList, order -> {
            Log.d("OrderActivity", "Clicked orderId: " + order.getOrderId());
            Intent intent = new Intent(OrderActivity.this, OrderDetailActivity.class);
            intent.putExtra("order_id", order.getOrderId());
            startActivity(intent);
        });
        recyclerView.setAdapter(orderAdapter);

        tabList.add(tabPendingConfirmation);
        tabList.add(tabWaitingPickup);
        tabList.add(tabShipping);
        tabList.add(tabDelivered);
        tabList.add(tabCanceled);
    }

    private void initTabClickListeners() {
        tabPendingConfirmation.setOnClickListener(v -> {
            selectTab(tabPendingConfirmation);
            loadPendingOrders();  // Tải đơn hàng "Chờ xác nhận"
        });

        tabWaitingPickup.setOnClickListener(v -> {
            selectTab(tabWaitingPickup);
            loadWaitingDeliveryOrders();  // Tải đơn hàng "Chờ giao hàng"
        });

        tabShipping.setOnClickListener(v -> {
            selectTab(tabShipping);
            loadPendingShipOrders();  // Tải đơn hàng "Chờ giao hàng"
        });

        tabDelivered.setOnClickListener(v -> {
            selectTab(tabDelivered);
            loadDeliveredOrders();  // Tải đơn hàng "Đã giao"
        });

        tabCanceled.setOnClickListener(v -> {
            selectTab(tabCanceled);
            loadCanceledOrders();  // Tải đơn hàng "Đã hủy"
        });
    }


    private void selectTab(TextView selectedTab) {
        for (TextView tab : tabList) {
            tab.setTextAppearance(this, R.style.TabItem);
        }
        selectedTab.setTextAppearance(this, R.style.TabItemSelected);
    }

    private void loadPendingOrders() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // Localhost cho Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getPendingOrders(customerId).enqueue(new Callback<List<PendingOrder>>() {
            @Override
            public void onResponse(Call<List<PendingOrder>> call, Response<List<PendingOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();

                    tvEmptyMessage.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(OrderActivity.this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PendingOrder>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderActivity", "API ERROR", t);
            }
        });
    }

    // Phương thức mới cho API "Chờ giao hàng"
    private void loadWaitingDeliveryOrders() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // Localhost cho Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getWaitingDeliveryOrders(customerId).enqueue(new Callback<List<PendingOrder>>() {
            @Override
            public void onResponse(Call<List<PendingOrder>> call, Response<List<PendingOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();

                    tvEmptyMessage.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(OrderActivity.this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PendingOrder>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderActivity", "API ERROR", t);
            }
        });
    }

    // Tải đơn hàng "Chờ giao hàng" (Pending Ship)
    private void loadPendingShipOrders() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // Localhost cho Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getShippingOrders(customerId).enqueue(new Callback<List<PendingOrder>>() {
            @Override
            public void onResponse(Call<List<PendingOrder>> call, Response<List<PendingOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();

                    tvEmptyMessage.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(OrderActivity.this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PendingOrder>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderActivity", "API ERROR", t);
            }
        });
    }

    // Tải đơn hàng "Đã giao"
    private void loadDeliveredOrders() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // Localhost cho Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getDeliveredOrders(customerId).enqueue(new Callback<List<PendingOrder>>() {
            @Override
            public void onResponse(Call<List<PendingOrder>> call, Response<List<PendingOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();

                    tvEmptyMessage.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(OrderActivity.this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PendingOrder>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderActivity", "API ERROR", t);
            }
        });
    }

    // Tải đơn hàng "Đã hủy"
    private void loadCanceledOrders() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/") // Localhost cho Android Emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);
        apiService.getCanceledOrders(customerId).enqueue(new Callback<List<PendingOrder>>() {
            @Override
            public void onResponse(Call<List<PendingOrder>> call, Response<List<PendingOrder>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    orderList.clear();
                    orderList.addAll(response.body());
                    orderAdapter.notifyDataSetChanged();

                    tvEmptyMessage.setVisibility(orderList.isEmpty() ? View.VISIBLE : View.GONE);
                } else {
                    Toast.makeText(OrderActivity.this, "Không thể tải đơn hàng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<PendingOrder>> call, Throwable t) {
                Toast.makeText(OrderActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("OrderActivity", "API ERROR", t);
            }
        });
    }
}
