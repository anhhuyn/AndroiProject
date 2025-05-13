package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.text.DecimalFormat;
import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.models.PendingOrder;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.OrderViewHolder> {

    private final List<PendingOrder> orderList;
    private final DecimalFormat priceFormat = new DecimalFormat("#,###₫");
    private final OnOrderClickListener listener;

    public PendingOrderAdapter(List<PendingOrder> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    public interface OnOrderClickListener {
        void onOrderClick(PendingOrder order);
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        PendingOrder order = orderList.get(position);
        Context context = holder.itemView.getContext();

        // Set thông tin sản phẩm đầu tiên trong đơn
        holder.txtProductName.setText(order.getProductName());
        holder.txtUnit.setText("Đơn vị tính: " + order.getUnit());
        holder.txtPrice.setText(priceFormat.format(order.getPrice()));
        holder.txtQuantity.setText("x" + order.getFirstProductQuantity());

        // Tổng tiền & số lượng sản phẩm
        holder.txtTotalPrice.setText("Tổng số tiền (" +
                order.getTotalItemsInOrder() + " sản phẩm): " +
                priceFormat.format(order.getTotalAmount()));

        // Load ảnh bằng Glide
        Glide.with(context)
                .load(order.getProductImage())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgProduct);

        // Xử lý nút "Chi tiết đơn hàng" nếu cần
        holder.btnReview.setOnClickListener(v -> {
            listener.onOrderClick(order);
        });
        holder.itemView.setOnClickListener(v -> {
            listener.onOrderClick(order);
        });

        // Ẩn "Xem thêm" nếu chỉ có 1 sản phẩm
        if (order.getTotalQuantityInOrder() <= 1) {
            holder.layoutViewMore.setVisibility(View.GONE);
        } else {
            holder.layoutViewMore.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, imgArrow;
        TextView txtProductName, txtUnit, txtPrice, txtQuantity, txtTotalPrice, txtViewMore, btnReview;
        View layoutViewMore;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.imgProduct);
            txtProductName = itemView.findViewById(R.id.txtProductName);
            txtUnit = itemView.findViewById(R.id.txtUnit);
            txtPrice = itemView.findViewById(R.id.txtPrice);
            txtQuantity = itemView.findViewById(R.id.txtQuantity);
            txtTotalPrice = itemView.findViewById(R.id.txtTotalPrice);
            txtViewMore = itemView.findViewById(R.id.txtViewMore);
            imgArrow = itemView.findViewById(R.id.imgArrow);
            layoutViewMore = itemView.findViewById(R.id.layoutViewMore);
            btnReview = itemView.findViewById(R.id.btnReview);
        }
    }
}
