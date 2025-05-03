package vn.iotstar.ecoveggieapp.activities;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.ReviewAdapter;
import vn.iotstar.ecoveggieapp.helpers.ApiService;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.ReviewModel;

public class ReviewProductActivity extends AppCompatActivity {

    private RecyclerView recyclerReview;
    private LinearLayout layoutEmpty;
    private ImageView btnBack;
    private ReviewAdapter reviewAdapter;
    private ApiService apiService;
    private TextView txtCountReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_review_product);

        initView();
        initRetrofit();
        loadReviews();

        btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        recyclerReview = findViewById(R.id.recyclerReview);
        btnBack = findViewById(R.id.btnBack);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        txtCountReview = findViewById(R.id.txtCountReview);
        btnBack = findViewById(R.id.btnBack);

    }

    private void initRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + StringHelper.SERVER_IP + ":9080/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    private void loadReviews() {
        int productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) return;

        apiService.getReviewsByProductId(productId).enqueue(new Callback<List<ReviewModel>>() {
            @Override
            public void onResponse(Call<List<ReviewModel>> call, Response<List<ReviewModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ReviewModel> reviewList = response.body();

                    boolean isEmpty = reviewList.isEmpty();
                    layoutEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
                    recyclerReview.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

                    if (!isEmpty) {
                        recyclerReview.setLayoutManager(new LinearLayoutManager(ReviewProductActivity.this));
                        reviewAdapter = new ReviewAdapter(ReviewProductActivity.this, reviewList);
                        recyclerReview.setAdapter(reviewAdapter);

                        // 👉 Cập nhật số lượng đánh giá
                        txtCountReview.setText("Đánh giá (" + reviewList.size() + ")");
                    } else {
                        txtCountReview.setText("Đánh giá (0)");
                    }

                }
            }

            @Override
            public void onFailure(Call<List<ReviewModel>> call, Throwable t) {
                layoutEmpty.setVisibility(View.VISIBLE);
                recyclerReview.setVisibility(View.GONE);
            }
        });
    }
}
