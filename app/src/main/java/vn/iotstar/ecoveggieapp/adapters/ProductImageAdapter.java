package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import vn.iotstar.ecoveggieapp.R;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductImageAdapter extends RecyclerView.Adapter<ProductImageAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;

    public ProductImageAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate item layout from XML
        View view = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        String imageUrl = imageUrls.get(position);

        // Load image using Glide
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)  // Placeholder image
                .error(R.drawable.ic_error)                 // Error image if something goes wrong
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            // Find the ImageView by ID from the inflated layout
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
