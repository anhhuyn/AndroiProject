package vn.iotstar.ecoveggieapp.activities;

import android.os.Bundle;
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
    private static final String URL = "http://" + StringHelper.SERVER_IP +":9080/api/v1/categories/all"; // Địa chỉ API danh mục

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.recyclerViewCategory);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(this, categoryList);
        recyclerView.setAdapter(categoryAdapter);

        fetchCategories();
    }

    private void fetchCategories() {
        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        categoryList.clear();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject categoryJson = response.getJSONObject(i);
                                int id = categoryJson.getInt("category_id");
                                String name = categoryJson.getString("category_name");
                                String thumbnail = categoryJson.getString("thumbnail");

                                categoryList.add(new CategoryModel(id, name, thumbnail));
                            }
                            categoryAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(CategoryActivity.this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(CategoryActivity.this, "Lỗi tải danh mục!", Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(jsonArrayRequest);
    }
}
