package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import vn.iotstar.ecoveggieapp.ConnectSQLServer;
import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;

public class VerifyEmailActivity extends AppCompatActivity {

    private EditText[] otpBoxes;
    private String verificationCode;
    private CountDownTimer countDownTimer;
    private TextView otpTimerText;
    private boolean isOtpValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);

        setContentView(R.layout.activity_verifyemail);

        otpTimerText = findViewById(R.id.otpTimerText); // Gán TextView từ XML

        startOtpTimer();

        verificationCode = getIntent().getStringExtra("otpCode");
        Log.d("OTP", "OTP từ email: " + verificationCode);configureOtpBoxes();
        configureOtpBoxes();

        Button btn_verifyEmail = findViewById(R.id.btn_verifyemail_verify);
        btn_verifyEmail.setOnClickListener(v -> {
            if (!isOtpValid) { // Nếu OTP đã hết hạn
                Toast.makeText(VerifyEmailActivity.this, "Mã OTP đã hết hạn, vui lòng yêu cầu mã mới!", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder otpCode = new StringBuilder();
            for (EditText otpBox : otpBoxes) {
                otpCode.append(otpBox.getText().toString());
            }
            String otp = otpCode.toString();

            if (otp.equals(verificationCode)) {
                // Mã OTP đúng, thực hiện đăng ký
                String username = getIntent().getStringExtra("username");
                String email = getIntent().getStringExtra("email");
                String phone = getIntent().getStringExtra("phone");
                String password = getIntent().getStringExtra("password");

                RequestQueue queue = Volley.newRequestQueue(VerifyEmailActivity.this);
                String url = "http://" + StringHelper.SERVER_IP +":9080/api/v1/user/register";

                // String Request Object;
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equalsIgnoreCase("success"))
                        {
                            Toast.makeText(VerifyEmailActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(VerifyEmailActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(VerifyEmailActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();

                    }
                })
                {
                    @Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username",username);
                        params.put("email",email);
                        params.put("phone",phone);
                        params.put("password",password);
                        return params;
                    }
                };
                queue.add(stringRequest);
            } else {
                Toast.makeText(VerifyEmailActivity.this, "Mã xác nhận không đúng, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startOtpTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) { // 120 giây, cập nhật mỗi giây
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                otpTimerText.setText("Mã OTP hết hạn sau: " + secondsRemaining + " giây");
            }

            public void onFinish() {
                otpTimerText.setText("Mã OTP đã hết hạn!");
                isOtpValid = false;
                // Tùy chọn: Vô hiệu hóa nút xác thực OTP nếu cần
                // btnVerify.setEnabled(false);
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Hủy timer khi thoát activity
        }
    }

    public void configureOtpBoxes() {
        otpBoxes = new EditText[6];
        otpBoxes[0] = findViewById(R.id.otp_box1);
        otpBoxes[1] = findViewById(R.id.otp_box2);
        otpBoxes[2] = findViewById(R.id.otp_box3);
        otpBoxes[3] = findViewById(R.id.otp_box4);
        otpBoxes[4] = findViewById(R.id.otp_box5);
        otpBoxes[5] = findViewById(R.id.otp_box6);

        // Thêm logic tự động chuyển focus giữa các ô
        for (int i = 0; i < otpBoxes.length; i++) {
            int finalI = i;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!s.toString().isEmpty() && finalI < otpBoxes.length - 1) {
                        otpBoxes[finalI + 1].requestFocus(); // Chuyển sang ô tiếp theo
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
        }
    }
}
