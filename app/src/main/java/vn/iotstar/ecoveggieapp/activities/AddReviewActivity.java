package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.ImageAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.FileUtils;
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.ReviewModel;

public class AddReviewActivity extends AppCompatActivity {

    private ImageView imgProduct, btnBack;
    private TextView txtProductName, txtPrice;
    private String productName, productImage;
    private int productId;
    private double productPrice;
    private ImageView imgStar1, imgStar2, imgStar3, imgStar4, imgStar5;
    private int rating = 0;
    private EditText txtComment;
    private ImageView imgCamera;
    private static final int PICK_IMAGE_REQUEST = 1;
    private List<Uri> mediaUriList = new ArrayList<>();
    private ImageAdapter imageAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_review);

        // Nhận dữ liệu từ Intent
        productId = getIntent().getIntExtra("product_id", -1);
        productName = getIntent().getStringExtra("product_name");
        productImage = getIntent().getStringExtra("product_image");
        productPrice = getIntent().getDoubleExtra("product_price", 0.0);

        SharedPrefManager prefManager = SharedPrefManager.getInstance(this);

        int userId = prefManager.getUserId();

        initView();
        RecyclerView recyclerView = findViewById(R.id.recyclerImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        imageAdapter = new ImageAdapter(this, mediaUriList);
        recyclerView.setAdapter(imageAdapter);

        txtPrice.setText(String.format("đ%,.0f", productPrice));

        if (productImage != null && !productImage.isEmpty()) {
            Glide.with(this)
                    .load(productImage)
                    .placeholder(R.drawable.placeholder_image)
                    .into(imgProduct);
        }

        if (productName != null) {
            txtProductName.setText(productName);
        }

        findViewById(R.id.txtSend).setOnClickListener(v -> {
            if (rating == 0) {
                Toast.makeText(AddReviewActivity.this, "Vui lòng chọn đánh giá sao", Toast.LENGTH_SHORT).show();
                return;
            }

            String comment = txtComment.getText().toString().trim();
            if (comment.isEmpty()) {
                Toast.makeText(AddReviewActivity.this, "Vui lòng nhập nhận xét", Toast.LENGTH_SHORT).show();
                return;
            }
            addReview(userId, productId, rating, comment);

        });
        btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

    }

    private void addReview(int userId, int productId, int rating, String comment) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService reviewApi = retrofit.create(ApiService.class);

        Call<ReviewModel> call = reviewApi.addReview(userId, productId, rating, comment);
        call.enqueue(new Callback<ReviewModel>() {
            @Override
            public void onResponse(Call<ReviewModel> call, Response<ReviewModel> response) {
                if (response.isSuccessful()) {
                    ReviewModel review = response.body();
                    int reviewId = review.getId();

                    addReviewMedia(reviewId);
                    Toast.makeText(AddReviewActivity.this, "Đánh giá thành công", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddReviewActivity.this, "Đánh giá không thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReviewModel> call, Throwable t) {
                Toast.makeText(AddReviewActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addReviewMedia(int reviewId) {
        if (mediaUriList.isEmpty()) return;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        for (Uri uri : mediaUriList) {
            File file = FileUtils.getFileFromUri(this, uri);
            if (file == null) {
                Log.e("FileUtils", "Không thể tạo file từ URI");
                continue;
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("media", file.getName(), requestFile);

            RequestBody reviewIdPart = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(reviewId));
            RequestBody mediaTypePart = RequestBody.create(MediaType.parse("text/plain"), "image");

            Call<Void> call = apiService.uploadReviewImage(reviewIdPart, mediaTypePart, body);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (!response.isSuccessful()) {
                        Log.e("UploadMedia", "Không thành công: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Log.e("UploadMedia", "Lỗi kết nối", t);
                }
            });
        }
    }



    private void setStarListeners() {
        ImageView[] stars = {imgStar1, imgStar2, imgStar3, imgStar4, imgStar5};

        for (int i = 0; i < stars.length; i++) {
            final int index = i;
            stars[i].setOnClickListener(v -> {
                updateStarRating(index);
            });
        }
    }

    private void updateStarRating(int clickedIndex) {
        ImageView[] stars = {imgStar1, imgStar2, imgStar3, imgStar4, imgStar5};

        for (int i = 0; i < stars.length; i++) {
            if (i <= clickedIndex) {
                stars[i].setImageResource(R.drawable.ic_star);
                resizeStar(stars[i], 32);
            } else {
                stars[i].setImageResource(R.drawable.ic_star_empty);
                resizeStar(stars[i], 35);
            }
        }

        rating = clickedIndex + 1;
    }


    private void resizeStar(ImageView star, int sizeDp) {
        int sizePx = (int) (sizeDp * getResources().getDisplayMetrics().density);
        star.getLayoutParams().width = sizePx;
        star.getLayoutParams().height = sizePx;
        star.requestLayout();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn hình ảnh"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            Uri selectedUri = data.getData();
            if (requestCode == PICK_IMAGE_REQUEST) {
                mediaUriList.add(selectedUri);
            }
            imageAdapter.notifyItemInserted(mediaUriList.size() - 1);
        }
    }


    private void initView() {
        imgProduct = findViewById(R.id.imgProduct);
        txtProductName = findViewById(R.id.txtProductName);
        txtPrice = findViewById(R.id.txtPrice);
        btnBack = findViewById(R.id.btnBack);

        imgStar1 = findViewById(R.id.imgStart1);
        imgStar2 = findViewById(R.id.imgStart2);
        imgStar3 = findViewById(R.id.imgStart3);
        imgStar4 = findViewById(R.id.imgStart4);
        imgStar5 = findViewById(R.id.imgStart5);
        txtComment = findViewById(R.id.txtComment);
        imgCamera = findViewById(R.id.imgCamera);
        imgCamera.setOnClickListener(v -> openImagePicker());
        setStarListeners();
    }
}
