package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import vn.iotstar.ecoveggieapp.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.iotstar.ecoveggieapp.models.CheckoutItemModel;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {

    private Context context;
    private List<CheckoutItemModel> itemList;

    public CheckoutItemAdapter(Context context, List<CheckoutItemModel> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CheckoutItemModel item = itemList.get(position);

        holder.txtProductName.setText(item.getProductName());
        holder.txtUnit.setText("Đơn vị tính: " + item.getUnit());
        holder.tvGia.setText("₫" + item.getPrice());
        holder.tvSoLuong.setText("x" + item.getQuantity());

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgSanPham);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgSanPham;
        TextView txtProductName, txtUnit, tvGia, tvSoLuong;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSanPham = itemView.findViewById(R.id.imgSanPham);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtUnit = itemView.findViewById(R.id.txtUnit);
            tvGia = itemView.findViewById(R.id.tvGia);
            tvSoLuong = itemView.findViewById(R.id.tvSoLuong);
        }
    }
}

