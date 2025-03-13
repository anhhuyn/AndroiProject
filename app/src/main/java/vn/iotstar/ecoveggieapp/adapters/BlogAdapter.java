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
import java.util.List;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.models.BlogModel;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private Context context;
    private List<BlogModel> blogs;

    public BlogAdapter(Context context, List<BlogModel> blogs) {
        this.context = context;
        this.blogs = blogs;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_blog, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogModel blog = blogs.get(position);
        holder.txtTitle.setText(blog.getTitle());
        holder.txtDescription.setText(blog.getDescription());

        // Dùng Glide để load ảnh
        Glide.with(context)
                .load(blog.getImageUrl())
                .placeholder(R.drawable.placeholder_image) // Ảnh tạm trong khi tải
                .into(holder.imgBlog);
    }

    @Override
    public int getItemCount() {
        return blogs.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription;
        ImageView imgBlog;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtBlogTitle);
            txtDescription = itemView.findViewById(R.id.txtBlogDescription);
            imgBlog = itemView.findViewById(R.id.imgBlog);
        }
    }
}
