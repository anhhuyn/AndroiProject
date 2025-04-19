package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.activities.ProductDetailActivity;
import vn.iotstar.ecoveggieapp.models.ProductModel;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Context context;
    private List<ProductModel> productList;

    public ProductAdapter(Context context, List<ProductModel> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);
        holder.txtProductName.setText(product.getProduct_name());
        holder.txtPrice.setText("Giá: $" + product.getPrice());
        holder.txtStock.setText("Đã bán " + product.getInstock_quantity());

        // Load hình ảnh đầu tiên từ danh sách hình ảnh
        if (product.getImages() != null && !product.getImages().isEmpty()) {
            String imageUrl = product.getImages().get(0); // Lấy ảnh đầu tiên
            Glide.with(context)
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.placeholder_image) // Ảnh mặc định nếu chưa tải xong
                    .error(R.drawable.placeholder_image) // Ảnh hiển thị nếu lỗi
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_image); // Ảnh mặc định nếu không có ảnh
        }

        // Set click listener cho itemView để mở chi tiết sản phẩm
        holder.itemView.setOnClickListener(v -> {
            // Truyền product_id sang ProductDetailActivity
            Intent intent = new Intent(context, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getProduct_id());// Truyền product_id
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView txtProductName, txtPrice, txtStock;
        ImageView imgProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtPrice = itemView.findViewById(R.id.txtProductPrice);
            txtStock = itemView.findViewById(R.id.txtSoldCount);
            imgProduct = itemView.findViewById(R.id.imgProduct);
        }
    }
}
