package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.CheckoutItemAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.AddressModel;
import vn.iotstar.ecoveggieapp.models.CheckoutItemModel;
import vn.iotstar.ecoveggieapp.models.OrderModel;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private TextView txtTotalAmount, txtVoucher, txtTotalPayment, txtSaveMoney, txtUsername, txtAddress, btnOrder, txtPoint;
    private RecyclerView recyclerView;
    private ImageView btnBack;
    private List<CheckoutItemModel> itemList = new ArrayList<>();
    private int addressId, total;
    private LinearLayout layoutCOD, layoutBank, layoutAddress;
    private String selectedPaymentMethod = "";
    private String userNote = "";
    private Switch switchPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);
        initViews();

        int userId = SharedPrefManager.getInstance(this).getUserId();
        getAddressInfo(userId);
        fetchPointData(userId);

        getIntentData();
        setupRecyclerView();
        displayAmountInfo();
        initListeners();
        layoutCOD.setOnClickListener(v -> selectPaymentMethod("COD"));
        layoutBank.setOnClickListener(v -> selectPaymentMethod("BANK"));
        setupListeners(userId);

        layoutAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CheckoutActivity.this, AddressActivity.class);
            intent.putExtra("source", 1); // Truyền số 1 vào với key "source"
            startActivityForResult(intent, 1);
        });

    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvDanhSachSanPham);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtVoucher = findViewById(R.id.txtVoucher);
        txtTotalPayment = findViewById(R.id.txtTotalPayment);
        txtSaveMoney = findViewById(R.id.txtSaveMoney);
        txtUsername = findViewById(R.id.txtUserName);
        txtAddress = findViewById(R.id.txtAddress);
        btnOrder = findViewById(R.id.btnReceive);
        findViewById(R.id.layoutNote).setOnClickListener(v -> showNoteDialog());
        btnBack = findViewById(R.id.btnBack);
        layoutCOD = findViewById(R.id.layoutCOD);
        layoutBank = findViewById(R.id.layoutBank);
        txtPoint = findViewById(R.id.txtPoint);
        switchPoint = findViewById(R.id.switchPoint);
        layoutAddress = findViewById(R.id.layoutAddress);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        List<CheckoutItemModel> items = (ArrayList<CheckoutItemModel>) intent.getSerializableExtra("checkout_items");
        if (items != null) itemList = items;
    }

    private void selectPaymentMethod(String method) {
        selectedPaymentMethod = method;

        if (method.equals("COD")) {
            layoutCOD.setBackgroundResource(R.drawable.bg_payment_selected);
            layoutBank.setBackgroundResource(R.drawable.bg_payment_unselected);
        } else {
            layoutBank.setBackgroundResource(R.drawable.bg_payment_selected);
            layoutCOD.setBackgroundResource(R.drawable.bg_payment_unselected);
        }
    }


    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheckoutItemAdapter adapter = new CheckoutItemAdapter(this, itemList);
        recyclerView.setAdapter(adapter);
    }

    private void initListeners() {
        // Lắng nghe sự thay đổi của Switch
        switchPoint.setOnCheckedChangeListener((buttonView, isChecked) -> {
            displayAmountInfo();  // Gọi lại hàm tính toán lại số tiền sau khi thay đổi
        });
    }

    private void displayAmountInfo() {
        double totalAmount = 0;
        for (CheckoutItemModel item : itemList) {
            totalAmount += item.getQuantity() * item.getPrice();
        }

        double discount = totalAmount > 0 ? totalAmount * 0.10 : 0;
        double totalPayment = totalAmount - discount;

        txtVoucher.setText(String.format("%,.0f₫", discount));


        if (switchPoint.isChecked()) {
            double totalDouble = (double) total;
            discount += totalDouble;
            totalPayment -= totalDouble;
        }

        txtTotalAmount.setText(String.format("%,.0f₫", totalAmount));
        txtTotalPayment.setText(String.format("%,.0f₫", totalPayment));
        txtSaveMoney.setText(String.format("Tiết kiệm %,.0f₫", discount));
    }

    private void setupListeners(int userId) {
        btnOrder.setOnClickListener(v -> {
            if (selectedPaymentMethod.isEmpty()) {
                showToast("Vui lòng chọn phương thức thanh toán");
                return;
            }
            double total = getTotalAmount();
            createOrder(userId, total - (total * 0.10), selectedPaymentMethod, "Pending Confirm", userNote, addressId);
        });
        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại activity trước đó
        });
    }

    private double getTotalAmount() {
        double total = 0;
        for (CheckoutItemModel item : itemList) {
            total += item.getQuantity() * item.getPrice();
        }
        return total;
    }

    private void getAddressInfo(int userId) {
        ApiService apiService = getApiService();
        apiService.getDefaultAddress(userId).enqueue(new Callback<AddressModel>() {
            @Override
            public void onResponse(Call<AddressModel> call, Response<AddressModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddressModel address = response.body();
                    addressId = address.getId();

                    String username = address.getUser().getUsername();
                    String phone = address.getPhone();
                    String fullAddress = address.getDetail() + ", " + address.getWards() + ", " +
                            address.getDistrict() + ", " + address.getProvince();

                    txtUsername.setText(username + " (+84) " + phone.substring(1));
                    txtAddress.setText(fullAddress);
                } else {
                    showToast("Không thể lấy thông tin địa chỉ");
                }
            }

            @Override
            public void onFailure(Call<AddressModel> call, Throwable t) {
                showToast("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void createOrder(int customerId, double totalAmount, String paymentMethod, String status, String note, int addressId) {
        ApiService apiService = getApiService();
        apiService.createOrder(customerId, totalAmount, paymentMethod, status, note, addressId)
                .enqueue(new Callback<OrderModel>() {
                    @Override
                    public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            OrderModel order = response.body();
                            int orderId = order.getId(); // Lấy order_id từ phản hồi
                            createOrderDetails(orderId);
                            // đặt lại point = 0
                            if (switchPoint.isChecked()) {
                                deductUsedPoints(customerId, total);
                            }

                        } else {
                            showToast("Đặt hàng thất bại");
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderModel> call, Throwable t) {
                        showToast("Lỗi: " + t.getMessage());
                    }
                });
    }

    private void createOrderDetails(int orderId) {
        ApiService apiService = getApiService();

        for (CheckoutItemModel item : itemList) {
            int productId = item.getProductId();
            int quantity = item.getQuantity();
            double price = item.getPrice();

            Log.d("CheckoutItem", "Product ID: " + productId +
                    ", Quantity: " + quantity +
                    ", Price: " + price);

            apiService.createOrderDetail(orderId, productId, quantity, price)
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                try {
                                    String message = response.body().string().trim();
                                    Log.d("API_RESPONSE", message);
                                    if (message.contains("success")) {
                                        updateProductQuantity(productId, quantity);
                                    } else {
                                        showToast("Thêm chi tiết đơn hàng thất bại: " + message);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    showToast("Lỗi đọc dữ liệu từ phản hồi.");
                                }
                            } else {
                                showToast("Phản hồi không hợp lệ từ server.");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            showToast("Lỗi khi thêm chi tiết đơn hàng: " + t.getMessage());
                        }
                    });
        }
        Intent intent = new Intent(CheckoutActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateProductQuantity(int productId, int quantityUsed) {
        ApiService apiService = getApiService();

        apiService.updateProductQuantity(productId, quantityUsed)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Log.d("PRODUCT_UPDATE", "Số lượng sản phẩm " + productId + " đã được cập nhật.");
                        } else {
                            Log.e("PRODUCT_UPDATE", "Cập nhật thất bại cho sản phẩm " + productId);
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("PRODUCT_UPDATE", "Lỗi khi cập nhật sản phẩm: " + t.getMessage());
                    }
                });
    }



    private void showNoteDialog() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_bottom_note);

        EditText edtMessage = dialog.findViewById(R.id.edtMessage);
        ImageView btnClose = dialog.findViewById(R.id.btnClose);
        TextView btnConfirm = dialog.findViewById(R.id.btnConfirm);

        edtMessage.setText(userNote);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        btnConfirm.setOnClickListener(v -> {
            userNote = edtMessage.getText().toString().trim();
            showToast("Lưu lời nhắn: " + userNote);
            dialog.dismiss();
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            int selectedAddressId = data.getIntExtra("selectedAddressId", -1);
            if (selectedAddressId != -1) {
                getAddressById(selectedAddressId);
                Log.d("CheckoutActivity", "Địa chỉ được chọn ID: " + selectedAddressId);
            }
        }
    }

    private void getAddressById(int id) {
        ApiService apiService = getApiService();
        apiService.getAddressById(id).enqueue(new Callback<AddressModel>() {
            @Override
            public void onResponse(Call<AddressModel> call, Response<AddressModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AddressModel address = response.body();
                    addressId = address.getId(); // cập nhật addressId

                    String username = address.getUser().getUsername();
                    String phone = address.getPhone();
                    String fullAddress = address.getDetail() + ", " + address.getWards() + ", " +
                            address.getDistrict() + ", " + address.getProvince();

                    txtUsername.setText(username + " (+84) " + phone.substring(1));
                    txtAddress.setText(fullAddress);
                } else {
                    showToast("Không thể lấy địa chỉ đã chọn");
                }
            }

            @Override
            public void onFailure(Call<AddressModel> call, Throwable t) {
                showToast("Lỗi khi lấy địa chỉ mới: " + t.getMessage());
            }
        });
    }



    private void fetchPointData(int userId) {
        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/points/"+userId; // thay URL thật

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.length() > 0) {
                            JSONObject obj = response.getJSONObject(

                                    0);
                            total = obj.getInt("totalPoints");
                            txtPoint.setText("Dùng " + total + " điểm tích lũy");
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


    private ApiService getApiService() {
        return new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService.class);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void deductUsedPoints(int userId, int usedPoint) {
        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/points/update";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> Log.d("POINT", "Điểm đã được trừ sau khi đặt hàng"),
                error -> Log.e("POINT", "Lỗi trừ điểm: " + error.getMessage())
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                int newPoint = total - usedPoint;
                params.put("user_id", String.valueOf(userId));
                params.put("total_points", String.valueOf(newPoint));
                return params;
            }
        };
        Volley.newRequestQueue(this).add(request);
    }

}
