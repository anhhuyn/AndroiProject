package vn.iotstar.ecoveggieapp.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.CartAdapter;
import vn.iotstar.ecoveggieapp.models.CartItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCart;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private TextView tvTotalPrice, tvTotal;
    private CheckBox cbSelectAll;
    private Button btnBuy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvTotalPrice = findViewById(R.id.tvTotalPrice);
        tvTotal = findViewById(R.id.tvTotal);
        cbSelectAll = findViewById(R.id.cbSelectAll);

        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartItemList);

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);

        // Sample data
        cartItemList.add(new CartItem("Táo đỏ nhập khẩu", "Đơn vị tính: kg", "₫950.000", "", 1, false));
        cartItemList.add(new CartItem("Cà chua bi", "Đơn vị tính: kg", "₫150.000", "", 1, false));

        cartAdapter.notifyDataSetChanged();

        // Handle "Select All" functionality
        cbSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItemList) {
                item.setSelected(isChecked);
            }
            cartAdapter.notifyDataSetChanged();
            updateTotalPrice();
        });

        // Calculate total price
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double total = 0;
        for (CartItem item : cartItemList) {
            if (item.isSelected()) {
                total += 950000; // Replace with dynamic price calculation
            }
        }
        tvTotalPrice.setText("₫" + total);
    }
}
