package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import vn.iotstar.ecoveggieapp.R;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;
    private String email, verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_password);

        // Nhận dữ liệu từ Intent
        email = getIntent().getStringExtra("email");
        verificationCode = getIntent().getStringExtra("verification_code");

        Log.d("ResetPassword", "Email: " + email);
        Log.d("ResetPassword", "Verification Code: " + verificationCode);

        // Khởi tạo các thành phần giao diện
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // Xử lý sự kiện khi nhấn nút "Đặt lại mật khẩu"
        btnResetPassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra đầu vào
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            Toast.makeText(this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gửi yêu cầu cập nhật mật khẩu đến API
        sendResetPasswordRequest(newPassword);
    }

    private void sendResetPasswordRequest(String newPassword) {
        String url = "http://192.168.1.20:9080/api/v1/user/resetpassword";

        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("newpassword", newPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    Toast.makeText(this, "Mật khẩu đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Lỗi kết nối, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                    Log.e("ResetPassword", "Lỗi: " + error.toString());
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");  // Thêm JSON header
                return headers;
            }
        };

        queue.add(request);
    }

}
