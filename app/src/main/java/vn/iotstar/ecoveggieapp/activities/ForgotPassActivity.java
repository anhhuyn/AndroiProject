package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import vn.iotstar.ecoveggieapp.R;

public class ForgotPassActivity extends AppCompatActivity {
    private TextView txt_backLogin;
    private EditText edt_email;
    private Button btn_sendEmail;
    private EditText[] otpBoxes;
    private Button btn_verifyOtp;
    private String code;
    VerifyEmailActivity verifyEmailActivity = new VerifyEmailActivity();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgotpassword);

        txt_backLogin = findViewById(R.id.txt_backLogin);
        txt_backLogin.setOnClickListener(v ->
        {
            Intent intent = new Intent(ForgotPassActivity.this, MainActivity.class);
            startActivity(intent);
        });

        edt_email = findViewById(R.id.edt_forgotpass_email);


        btn_sendEmail = findViewById(R.id.btn_forgotpassword_sendEmail);
        btn_sendEmail.setOnClickListener(v ->
        {
            String email = edt_email.getText().toString().trim();
            Log.d("Email", "email: " + email);
            if (email.isEmpty()) {
                Toast.makeText(ForgotPassActivity.this, "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
                return;
            }

            code = generateRandomString(6);
            sendEmail(email, "Your Verification Code", "Your verification code is: " + code);

            showOtpInterface();
        });
    }

    private void showOtpInterface() {

        setContentView(R.layout.activity_verifyemail);

        otpBoxes = new EditText[6];
        otpBoxes[0] = findViewById(R.id.otp_box1);
        otpBoxes[1] = findViewById(R.id.otp_box2);
        otpBoxes[2] = findViewById(R.id.otp_box3);
        otpBoxes[3] = findViewById(R.id.otp_box4);
        otpBoxes[4] = findViewById(R.id.otp_box5);
        otpBoxes[5] = findViewById(R.id.otp_box6);


        for (int i = 0; i < otpBoxes.length; i++) {
            int nextIndex = i + 1;
            otpBoxes[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable.length() == 1 && nextIndex < otpBoxes.length) {
                        otpBoxes[nextIndex].requestFocus();  // Chuyển con trỏ sang ô tiếp theo
                    }
                }
            });
        }

        btn_verifyOtp = findViewById(R.id.btn_verifyemail_verify);
        btn_verifyOtp.setOnClickListener(v -> {
            StringBuilder otpInput = new StringBuilder();
            for (EditText otpBox : otpBoxes) {
                otpInput.append(otpBox.getText().toString());
            }

            // Kiểm tra mã OTP người dùng nhập
            if (otpInput.toString().equals(code)) {
                Toast.makeText(ForgotPassActivity.this, "OTP đúng!", Toast.LENGTH_SHORT).show();
                // Sau khi OTP đúng, có thể tiếp tục các bước khác (ví dụ, chuyển đến Reset Password)
                Intent intent = new Intent(ForgotPassActivity.this, ResetPasswordActivity.class);

                // Truyền dữ liệu qua Intent
                intent.putExtra("email", edt_email.getText().toString());  // Truyền email người dùng
                intent.putExtra("verification_code", code);  // Hiển thị giao diện Reset Password
                startActivity(intent);
            } else {
                Toast.makeText(ForgotPassActivity.this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void sendEmail(String toEmail, String subject, String messageBody) {
        new Thread(() -> {
            String fromEmail = "nguyenthianhhuyen03@gmail.com";
            String password = "wjbj vyvf gkgo wyzb";

            Properties properties = new Properties();
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(fromEmail, password);
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject(subject);
                message.setText(messageBody);

                Transport.send(message);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }).start();
    }



    public static String generateRandomString(int length) {
        String numbers = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(numbers.length());
            sb.append(numbers.charAt(index));
        }

        return sb.toString();
    }
}
