package vn.iotstar.ecoveggieapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.sql.Connection;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtNewPassword, edtConfirmPassword;
    private Button btnResetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_reset_password);

        // Lấy dữ liệu từ Intent
        String email = getIntent().getStringExtra("email");
        String verificationCode = getIntent().getStringExtra("verification_code");

        Log.d("ResetPassword", "Email: " + email);  // Kiểm tra email đã truyền vào Intent
        Log.d("ResetPassword", "Verification Code: " + verificationCode);  // Kiểm tra mã OTP

        // Khởi tạo các View
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnResetPassword = findViewById(R.id.btn_reset_password);

        // Đặt sự kiện cho nút "Đặt lại mật khẩu"
        btnResetPassword.setOnClickListener(v -> {
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp không
            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ mật khẩu!", Toast.LENGTH_SHORT).show();
                return;
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(ResetPasswordActivity.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                return;
            } else {

                // Nếu mật khẩu hợp lệ, bạn có thể cập nhật mật khẩu trong cơ sở dữ liệu hoặc SharedPreferences
                // Tạo đối tượng ConnectSQLServer để kết nối cơ sở dữ liệu
                ConnectSQLServer db = new ConnectSQLServer();
                Connection conn = db.CONN();

                // Cập nhật mật khẩu trong cơ sở dữ liệu
                boolean isUpdated = db.updatePassword(email, newPassword);

                if (isUpdated) {
                    Toast.makeText(ResetPasswordActivity.this, "Mật khẩu đã được thay đổi thành công!", Toast.LENGTH_SHORT).show();

                    // Chuyển đến màn hình đăng nhập sau khi cập nhật mật khẩu
                    Intent intent = new Intent(ResetPasswordActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();  // Đóng activity hiện tại
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Cập nhật mật khẩu thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
