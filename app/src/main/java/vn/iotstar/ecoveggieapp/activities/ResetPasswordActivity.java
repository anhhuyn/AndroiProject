package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;
import com.android.volley.toolbox.StringRequest;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private String email, verificationCode;
    private String actionType;  // lưu giá trị action_type

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_password);

        // Nhận dữ liệu từ Intent
        email = getIntent().getStringExtra("email");
        verificationCode = getIntent().getStringExtra("verification_code");
        actionType = getIntent().getStringExtra("action_type");

        Log.d("ResetPassword", "Email: " + email);
        Log.d("ResetPassword", "Verification Code: " + verificationCode);
        Log.d("ResetPassword", "Action Type: " + actionType);

        // Ánh xạ giao diện
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // Bắt sự kiện nút đặt lại mật khẩu
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        sendResetPasswordRequest(newPassword);
    }

    private void sendResetPasswordRequest(String newPassword) {
        String url = "http://" + StringHelper.SERVER_IP + ":9080/api/v1/user/resetpassword";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    Intent intent;

                    // Kiểm tra actionType để quyết định chuyển trang
                    if ("reset".equals(actionType)) {
                        // Quay về MainActivity khi quên mật khẩu
                        intent = new Intent(ResetPasswordActivity.this, EditProfileActivity.class);
                    } else {
                        // Quay về EditProfileActivity khi reset mật khẩu
                        intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    }

                    startActivity(intent);
                    finish();
                },
                error -> handleVolleyError(error)
        ) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("email", email);
                    jsonBody.put("newPassword", newPassword);
                    return jsonBody.toString().getBytes(StandardCharsets.UTF_8);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void handleVolleyError(VolleyError error) {
        if (error.networkResponse != null && error.networkResponse.data != null) {
            String responseBody = new String(error.networkResponse.data);
            Log.e("ResetPassword", "Lỗi từ server: " + responseBody);
            Toast.makeText(this, "Lỗi từ server: " + responseBody, Toast.LENGTH_LONG).show();
        } else {
            Log.e("ResetPassword", "Lỗi kết nối: " + error.toString());
            Toast.makeText(this, "Lỗi kết nối, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
        }
    }
}
