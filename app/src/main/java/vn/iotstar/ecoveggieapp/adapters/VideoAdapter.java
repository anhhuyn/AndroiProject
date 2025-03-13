package vn.iotstar.ecoveggieapp.adapters;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import vn.iotstar.ecoveggieapp.models.VideoModel;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoModel> videos;

    public VideoAdapter(Context context, List<VideoModel> videos) {
        this.context = context;
        this.videos = videos;
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModel video = videos.get(position);
        holder.txtTitle.setText(video.getTitle());

        // Dùng Glide để tải ảnh thumbnail
        Glide.with(context)
                .load(video.getThumbnailUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.imgThumbnail);

        // Mở video khi click vào ảnh
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video.getVideoUrl()));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle;
        ImageView imgThumbnail;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtVideoTitle);
            imgThumbnail = itemView.findViewById(R.id.imgVideoThumbnail);
        }
    }
}
