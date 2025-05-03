package vn.iotstar.ecoveggieapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.activities.AddReviewActivity;
import vn.iotstar.ecoveggieapp.models.ProductModel;

public class ProductReviewAdapter extends RecyclerView.Adapter<ProductReviewAdapter.ProductViewHolder> {

    private final List<ProductModel> productList;
    private final Activity activity;

    public ProductReviewAdapter(List<ProductModel> productList, Activity activity) {
        this.productList = productList;
        this.activity = activity;
    }



    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_unreview, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductModel product = productList.get(position);

        holder.tvProductName.setText(product.getProduct_name());
        holder.tvPrice.setText(String.format("đ%,.0f / %s", product.getPrice(), product.getUnit()));

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            Glide.with(activity)
                    .load(product.getImages().get(0))
                    .placeholder(R.drawable.placeholder_image)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_image);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(activity, AddReviewActivity.class);
            intent.putExtra("product_id", product.getProduct_id());
            intent.putExtra("product_name", product.getProduct_name());
            intent.putExtra("product_price", product.getPrice());
            String imageUrl = (product.getImages() != null && !product.getImages().isEmpty()) ?
                    product.getImages().get(0) : "";
            intent.putExtra("product_image", imageUrl);

            Log.d("ReviewAdapter", "Clicked item: " + product.getProduct_name());
            activity.startActivityForResult(intent, 1); // REQUEST_CODE = 1
        });
    }

        @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct;
        TextView tvProductName, tvPrice, tvReviewReward;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_product);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvReviewReward = itemView.findViewById(R.id.tv_review_reward);

        }
    }
}
