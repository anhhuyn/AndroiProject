package vn.iotstar.ecoveggieapp.adapters;

import vn.iotstar.ecoveggieapp.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.MediaModel;
import vn.iotstar.ecoveggieapp.models.ReviewModel;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Context context;
    private List<ReviewModel> reviewList;

    public ReviewAdapter(Context context, List<ReviewModel> reviewList) {
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_review_product, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        ReviewModel review = reviewList.get(position);

        holder.txtUsername.setText(review.getUser().getUsername());
        holder.txtComment.setText(review.getComment());
        holder.txtDate.setText(formatDateTime(review.getCreatedAt()));

        // Hiển thị số sao
        for (int i = 0; i < 5; i++) {
            holder.stars[i].setImageResource(
                    i < review.getRating() ? R.drawable.ic_star : R.drawable.ic_star_empty
            );
        }

        // Avatar
        String avatar = review.getUser().getAvatar();
        if (avatar != null && !avatar.isEmpty()) {
            String avatarUrl = "http://" + StringHelper.SERVER_IP + ":9080/uploads/" + avatar;
            Glide.with(context)
                    .load(avatarUrl)
                    .circleCrop()
                    .placeholder(R.drawable.ic_avatar)
                    .into(holder.imgAvatar);
        } else {
            holder.imgAvatar.setImageResource(R.drawable.ic_avatar);
        }

        List<String> imageUrls = new ArrayList<>();
        if (review.getMediaList() != null && !review.getMediaList().isEmpty()) {
            for (MediaModel media : review.getMediaList()) {
                if (media.getMediaType().equalsIgnoreCase("image")) {
                    // Thêm prefix đường dẫn nếu cần
                    String imageUrl = "http://" + StringHelper.SERVER_IP + ":9080/uploads/review_media/" + media.getMediaUrl();
                    imageUrls.add(imageUrl);
                }
            }
        }

        if (!imageUrls.isEmpty()) {
            holder.recyclerImages.setVisibility(View.VISIBLE);
            holder.recyclerImages.setLayoutManager(
                    new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.recyclerImages.setAdapter(new ImageReviewAdapter(context, imageUrls));
        } else {
            holder.recyclerImages.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsername, txtComment, txtDate;
        ImageView[] stars = new ImageView[5];
        RecyclerView recyclerImages;
        ImageView imgAvatar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsername = itemView.findViewById(R.id.txtUserName);
            txtComment = itemView.findViewById(R.id.txtComment);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            recyclerImages = itemView.findViewById(R.id.recyclerReviewImages);

            stars[0] = itemView.findViewById(R.id.star1);
            stars[1] = itemView.findViewById(R.id.star2);
            stars[2] = itemView.findViewById(R.id.star3);
            stars[3] = itemView.findViewById(R.id.star4);
            stars[4] = itemView.findViewById(R.id.star5);
        }
    }

    private String formatDateTime(String isoDateTime) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
            Date date = inputFormat.parse(isoDateTime);

            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDateTime; // fallback
        }
    }

}