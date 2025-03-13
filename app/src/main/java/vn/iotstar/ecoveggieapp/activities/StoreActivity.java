package vn.iotstar.ecoveggieapp.activities;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.BlogAdapter;
import vn.iotstar.ecoveggieapp.adapters.VideoAdapter;
import vn.iotstar.ecoveggieapp.models.BlogModel;
import vn.iotstar.ecoveggieapp.models.VideoModel;

public class StoreActivity extends AppCompatActivity {
    private RecyclerView recyclerBlog, recyclerVideo;
    private BlogAdapter blogAdapter;
    private VideoAdapter videoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        recyclerBlog = findViewById(R.id.recyclerBlog);
        recyclerVideo = findViewById(R.id.recyclerVideo);

        // Cấu hình danh sách blog
        recyclerBlog.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerBlog.setHasFixedSize(true);
        blogAdapter = new BlogAdapter(this, getSampleBlogs());
        recyclerBlog.setAdapter(blogAdapter);

        // Cấu hình danh sách video
        recyclerVideo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerVideo.setHasFixedSize(true);
        videoAdapter = new VideoAdapter(this, getSampleVideos());
        recyclerVideo.setAdapter(videoAdapter);
    }

    private List<BlogModel> getSampleBlogs() {
        List<BlogModel> blogs = new ArrayList<>();
        blogs.add(new BlogModel(
                "Lợi ích của nông sản sạch",
                "Nông sản sạch giúp bảo vệ sức khỏe và môi trường.",
                "https://cdn.pixabay.com/photo/2016/11/18/15/45/agriculture-1836170_960_720.jpg",
                "2025-03-13"
        ));
        blogs.add(new BlogModel(
                "Cách chọn rau củ an toàn",
                "Hướng dẫn chọn rau không hóa chất và đảm bảo dinh dưỡng.",
                "https://cdn.pixabay.com/photo/2018/07/12/08/57/vegetables-3539263_960_720.jpg",
                "2025-03-13"
        ));
        return blogs;
    }

    private List<VideoModel> getSampleVideos() {
        List<VideoModel> videos = new ArrayList<>();
        videos.add(new VideoModel(
                "Hướng dẫn trồng rau sạch tại nhà",
                "https://img.youtube.com/vi/abc123/maxresdefault.jpg",
                "https://www.youtube.com/watch?v=abc123"
        ));
        videos.add(new VideoModel(
                "Cách nhận biết thực phẩm hữu cơ",
                "https://img.youtube.com/vi/def456/maxresdefault.jpg",
                "https://www.youtube.com/watch?v=def456"
        ));
        return videos;
    }
}
