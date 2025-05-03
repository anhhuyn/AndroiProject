package vn.iotstar.ecoveggieapp.adapters;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.FullScreenImageDialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ImageReviewAdapter extends RecyclerView.Adapter<ImageReviewAdapter.ImageViewHolder> {

    private Context context;
    private List<String> imageUrls;

    public ImageReviewAdapter(Context context, List<String> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_image_preview, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        // Load the image using Glide
        String imageUrl = imageUrls.get(position);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgReview);

        // Set up click listener to open the image in full screen
        holder.imgReview.setOnClickListener(v -> {
            // Open full screen dialog with the clicked image URL
            FullScreenImageDialog dialog = new FullScreenImageDialog(context, imageUrl);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgReview;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgReview = itemView.findViewById(R.id.imgPreview);
        }
    }
}
