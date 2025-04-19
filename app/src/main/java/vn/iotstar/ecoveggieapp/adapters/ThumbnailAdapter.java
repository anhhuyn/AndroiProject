package vn.iotstar.ecoveggieapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import vn.iotstar.ecoveggieapp.R;

public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private OnThumbnailClickListener listener;

    public interface OnThumbnailClickListener {
        void onThumbnailClick(int position);
    }

    public ThumbnailAdapter(Context context, List<String> imageUrls, OnThumbnailClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ThumbnailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbnailAdapter.ViewHolder holder, int position) {
        Glide.with(context)
                .load(imageUrls.get(position))
                .into(holder.thumbnailImage);

        holder.thumbnailImage.setOnClickListener(v -> listener.onThumbnailClick(position));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImage;

        public ViewHolder(View itemView) {
            super(itemView);
            thumbnailImage = itemView.findViewById(R.id.thumbnailImage);
        }
    }
}
