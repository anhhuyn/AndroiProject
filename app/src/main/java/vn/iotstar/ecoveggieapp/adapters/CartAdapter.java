package vn.iotstar.ecoveggieapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.models.CartItemModel;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItemModel> cartItemList;

    public CartAdapter(List<CartItemModel> cartItemList) {
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItemModel cartItem = cartItemList.get(position);

        // Hiển thị thông tin sản phẩm
        holder.tvProductName.setText(cartItem.getProductName());
        holder.tvProductPrice.setText("₫" + String.format("%.2f", cartItem.getPrice()));
        holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
        holder.tvProductVariant.setText("Đơn vị tính: " + cartItem.getUnit());  // Assuming getUnit() is a method in CartItemModel


        // Hiển thị hình ảnh sản phẩm
        if (cartItem.getImages() != null && !cartItem.getImages().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(cartItem.getImages().get(0))
                    .into(holder.imgProduct);
        }


        holder.cbSelectItem.setChecked(false);
        // Thêm sự kiện thay đổi số lượng
        holder.btnPlus.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            cartItem.setQuantity(quantity + 1);
            holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
        });

        holder.btnMinus.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            if (quantity > 1) {
                cartItem.setQuantity(quantity - 1);
                holder.tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
            } else {
                Toast.makeText(holder.itemView.getContext(), "Số lượng không thể nhỏ hơn 1", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelectItem;
        ImageView imgProduct;
        TextView tvProductName, tvProductPrice, tvProductQuantity, tvProductVariant;
        TextView btnMinus, btnPlus;

        public CartViewHolder(View itemView) {
            super(itemView);

            cbSelectItem = itemView.findViewById(R.id.cbSelectItem);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.txtQuantity);
            tvProductVariant = itemView.findViewById(R.id.tvProductVariant);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }
    }
}
