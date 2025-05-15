package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.adapters.CategoryAdapter;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import vn.iotstar.ecoveggieapp.models.CategoryModel;

public class CategoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CategoryAdapter categoryAdapter;
    private List<CategoryModel> categoryList;
    private EditText edtSearch;
    private static final String BASE_URL = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/categories";
    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        initViews();
        setupRecyclerView();
        setupListeners();

        requestQueue = Volley.newRequestQueue(this);
        fetchCategories(BASE_URL + "/all");
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerViewCategory);
        edtSearch = findViewById(R.id.edtSearchBar);
        ImageView btnBack = findViewById(R.id.btnBack);


        btnBack.setOnClickListener(view -> navigateToHome());
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(categoryAdapter);
    }

    private void setupListeners() {
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchCategories(BASE_URL + "/search?name=" + s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchCategories(String url) {
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> handleResponse(response),
                error -> handleError(error));

        requestQueue.add(jsonArrayRequest);
    }

    private void handleResponse(JSONArray response) {
        categoryList.clear();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject categoryJson = response.getJSONObject(i);
                categoryList.add(new CategoryModel(
                        categoryJson.getInt("category_id"),
                        categoryJson.getString("category_name"),
                        categoryJson.getString("thumbnail"),
                        categoryJson.getInt("productCount") // Nhận số lượng sản phẩm
                ));
            }
            categoryAdapter.notifyDataSetChanged();
        } catch (JSONException e) {
            showToast("Lỗi xử lý dữ liệu!");
        }
    }


    private void handleError(VolleyError error) {
        showToast("Lỗi tải danh mục!");
    }

    private void showToast(String message) {
        Toast.makeText(CategoryActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToHome() {
        Intent intent = new Intent(CategoryActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
