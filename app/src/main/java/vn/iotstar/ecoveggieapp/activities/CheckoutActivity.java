package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {

    private TextView txtTotalAmount, txtVoucher, txtTotalPayment, txtSaveMoney, txtUsername, txtAddress, btnOrder;
    private RecyclerView recyclerView;
    private ImageView btnBack;
    private List<CheckoutItemModel> itemList = new ArrayList<>();
    private int addressId;
    private LinearLayout layoutCOD, layoutBank;
    private String selectedPaymentMethod = "";
    private String userNote = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        initViews();

        int userId = SharedPrefManager.getInstance(this).getUserId();
        getAddressInfo(userId);

        getIntentData();
        setupRecyclerView();
        displayAmountInfo();
        layoutCOD.setOnClickListener(v -> selectPaymentMethod("COD"));
        layoutBank.setOnClickListener(v -> selectPaymentMethod("BANK"));
        setupListeners(userId);
    }

    private void initViews() {
        recyclerView = findViewById(R.id.rvDanhSachSanPham);
        txtTotalAmount = findViewById(R.id.txtTotalAmount);
        txtVoucher = findViewById(R.id.txtVoucher);
        txtTotalPayment = findViewById(R.id.txtTotalPayment);
        txtSaveMoney = findViewById(R.id.txtSaveMoney);
        txtUsername = findViewById(R.id.txtUserName);
        txtAddress = findViewById(R.id.txtAddress);
        btnOrder = findViewById(R.id.btnOrder);
        findViewById(R.id.layoutNote).setOnClickListener(v -> showNoteDialog());
        btnBack = findViewById(R.id.btnBack);
        layoutCOD = findViewById(R.id.layoutCOD);
        layoutBank = findViewById(R.id.layoutBank);

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

    private void displayAmountInfo() {
        double totalAmount = 0;
        for (CheckoutItemModel item : itemList) {
            totalAmount += item.getQuantity() * item.getPrice();
        }

        double discount = totalAmount > 0 ? totalAmount * 0.10 : 0;
        double totalPayment = totalAmount - discount;

        txtTotalAmount.setText(String.format("%,.0f₫", totalAmount));
        txtVoucher.setText(String.format("- %,.0f₫", discount));
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
            createOrder(userId, total - (total * 0.10), selectedPaymentMethod, "Pending", userNote, addressId);
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
                            // Có thể chuyển sang OrderSuccessActivity tại đây nếu muốn
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
                                        showToast("Chi tiết đơn hàng đã được thêm thành công.");
                                        Intent intent = new Intent(CheckoutActivity.this, ProfileActivity.class);
                                        startActivity(intent); // Chuyển hướng sang ProfileActivity
                                        finish(); // Đóng CheckoutActivity
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
}
