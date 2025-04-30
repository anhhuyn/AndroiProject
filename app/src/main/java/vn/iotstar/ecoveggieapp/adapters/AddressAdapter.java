package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.activities.EditAddressActivity;
import vn.iotstar.ecoveggieapp.models.AddressModel;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private Context context;
    private List<AddressModel> addressList;

    public AddressAdapter(Context context, List<AddressModel> addressList) {
        this.context = context;
        this.addressList = addressList;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        AddressModel address = addressList.get(position);

        holder.tvName.setText(address.getUser().getUsername());
        holder.tvPhone.setText("| " + address.getPhone());
        holder.tvDetail.setText(address.getDetail());
        holder.tvWards.setText(address.getWards() + ", " + address.getDistrict() + ", " + address.getProvince());

        if (address.isDefault()) {
            holder.tvDefault.setVisibility(View.VISIBLE);
        } else {
            holder.tvDefault.setVisibility(View.GONE);
        }

        // Set sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditAddressActivity.class);
            intent.putExtra("addressId", address.getId());
            intent.putExtra("userName", address.getUser().getUsername());
            intent.putExtra("phone", address.getPhone());
            intent.putExtra("detail", address.getDetail());
            intent.putExtra("wards", address.getWards());
            intent.putExtra("district", address.getDistrict());
            intent.putExtra("province", address.getProvince());
            intent.putExtra("isDefault", address.isDefault());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void updateAddressList(List<AddressModel> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }

    public static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone, tvDetail, tvWards, tvDefault;

        public AddressViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            tvDetail = itemView.findViewById(R.id.tvAddressLine1);
            tvWards = itemView.findViewById(R.id.tvAddressLine2);
            tvDefault = itemView.findViewById(R.id.tvDefault);
        }
    }
}
