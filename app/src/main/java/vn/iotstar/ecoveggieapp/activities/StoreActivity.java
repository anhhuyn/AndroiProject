package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import java.util.Arrays;
import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.ImageSliderAdapter;

public class StoreActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable runnable;
    private List<Integer> imageList = Arrays.asList(
            R.drawable.logo_green,
            R.drawable.nongsan1,
            R.drawable.nongsan2,
            R.drawable.nongsan3
    );

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_store);

        // Thiết lập icon và text màu cho "Store"
        ImageView iconStore = findViewById(R.id.icon_store);
        TextView textStore = findViewById(R.id.text_store);
        iconStore.setImageResource(R.drawable.ic_store_green);
        textStore.setTextColor(getResources().getColor(R.color.green_main));

        // Thiết lập ViewPager2
        viewPager = findViewById(R.id.viewPager);
        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);

        // Khởi tạo runnable chỉ một lần
        runnable = new Runnable() {
            @Override
            public void run() {
                int nextItem = (viewPager.getCurrentItem() + 1) % imageList.size();
                viewPager.setCurrentItem(nextItem, true);
                handler.postDelayed(this, 3000); // cuộn sau 3 giây
            }
        };

        // Thiết lập VideoView
        VideoView videoView = findViewById(R.id.videoView);
        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.store_intro;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);

        videoView.start();

        MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);
        mediaController.setAnchorView(videoView);

        videoView.setOnCompletionListener(mp -> videoView.start());

        /// Video thứ nhất
        VideoView videoView1 = findViewById(R.id.videoView1);
        String videoPath1 = "android.resource://" + getPackageName() + "/" + R.raw.video1;
        Uri uri1 = Uri.parse(videoPath1);
        videoView1.setVideoURI(uri1);
        MediaController mediaController1 = new MediaController(this);
        videoView1.setMediaController(mediaController1);
        mediaController1.setAnchorView(videoView1);
        videoView1.start(); // <--- Thêm dòng này
        videoView1.setOnCompletionListener(mp -> videoView1.start());

// Video thứ hai
        VideoView videoView2 = findViewById(R.id.videoView2);
        String videoPath2 = "android.resource://" + getPackageName() + "/" + R.raw.video2;
        Uri uri2 = Uri.parse(videoPath2);
        videoView2.setVideoURI(uri2);
        MediaController mediaController2 = new MediaController(this);
        videoView2.setMediaController(mediaController2);
        mediaController2.setAnchorView(videoView2);
        videoView2.start(); // <--- Thêm dòng này
        videoView2.setOnCompletionListener(mp -> videoView2.start());


        // Navigation giữa các tab
        ImageView iconHome = findViewById(R.id.icon_home);
        TextView txtHome = findViewById(R.id.txtHome);
        iconHome.setImageResource(R.drawable.ic_home);

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

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 3000);
    }
}
