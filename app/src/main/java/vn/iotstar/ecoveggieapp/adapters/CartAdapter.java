package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartItem> cartItemList;

    public CartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItemList.get(position);

        holder.productName.setText(cartItem.getProductName());
        holder.productVariant.setText(cartItem.getProductVariant());
        holder.productPrice.setText(cartItem.getProductPrice());
        holder.productImage.setImageResource(R.drawable.placeholder_image); // Replace with image loading library like Glide or Picasso
        holder.quantity.setText(String.valueOf(cartItem.getQuantity()));

        holder.selectItem.setChecked(cartItem.isSelected());

        holder.selectItem.setOnCheckedChangeListener((buttonView, isChecked) -> cartItem.setSelected(isChecked));

        holder.minusButton.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            if (currentQuantity > 1) {
                cartItem.setQuantity(currentQuantity - 1);
                holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
            }
        });

        holder.plusButton.setOnClickListener(v -> {
            int currentQuantity = cartItem.getQuantity();
            cartItem.setQuantity(currentQuantity + 1);
            holder.quantity.setText(String.valueOf(cartItem.getQuantity()));
        });
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox selectItem;
        ImageView productImage;
        TextView productName, productVariant, productPrice, quantity;
        TextView minusButton, plusButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            selectItem = itemView.findViewById(R.id.cbSelectItem);
            productImage = itemView.findViewById(R.id.imgProduct);
            productName = itemView.findViewById(R.id.tvProductName);
            productVariant = itemView.findViewById(R.id.tvProductVariant);
            productPrice = itemView.findViewById(R.id.tvProductPrice);
            quantity = itemView.findViewById(R.id.txtQuantity);
            minusButton = itemView.findViewById(R.id.btnMinus);
            plusButton = itemView.findViewById(R.id.btnPlus);
        }
    }
}
