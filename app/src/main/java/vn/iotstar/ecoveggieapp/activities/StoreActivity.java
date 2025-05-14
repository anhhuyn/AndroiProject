package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import vn.iotstar.ecoveggieapp.R;

public class StoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        // === Thiết lập icon và màu cho Bottom Navigation ===

        // 1. Cửa hàng (đang active)
        ImageView iconStore = findViewById(R.id.icon_store);
        TextView textStore = findViewById(R.id.text_store);
        iconStore.setImageResource(R.drawable.ic_store_green); // icon màu xanh
        textStore.setTextColor(getResources().getColor(R.color.green_main));

        // 2. Trang chủ (không active)
        ImageView iconHome = findViewById(R.id.icon_home);
        TextView txtHome = findViewById(R.id.txtHome);
        iconHome.setImageResource(R.drawable.ic_home); // icon màu xám

        // === Thiết lập VideoView ===
        VideoView videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.store_intro;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.start(); // Tự động phát video

        // Thêm MediaController cho điều khiển video
        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        // Khi video phát xong, tự động phát lại
        videoView.setOnCompletionListener(mp -> videoView.start());

        // === Xử lý sự kiện cho các mục trong thanh điều hướng ===
        findViewById(R.id.icon_home).setOnClickListener(v -> {
            startActivity(new Intent(StoreActivity.this, HomeActivity.class));
            finish();
        });

        findViewById(R.id.layout_category).setOnClickListener(v -> {
            startActivity(new Intent(StoreActivity.this, CategoryActivity.class));
            finish();
        });

        findViewById(R.id.layout_profile).setOnClickListener(v -> {
            startActivity(new Intent(StoreActivity.this, ProfileActivity.class));
            finish();
        });
    }
}
