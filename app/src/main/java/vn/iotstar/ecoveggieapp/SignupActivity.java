package vn.iotstar.ecoveggieapp;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignupActivity extends AppCompatActivity {

    private EditText edt_signup_username;
    private EditText edt_signup_email;
    private EditText edt_signup_phone;
    private EditText edt_signup_password;
    private EditText edt_signup_repass;
    private Button btn_signup;
    Connection conn;
    ConnectSQLServer connectSQLServer = new ConnectSQLServer();
    //int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        btn_signup = findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(v -> {
            edt_signup_username = findViewById(R.id.edt_signup_username);
            edt_signup_email = findViewById(R.id.edt_signup_email);
            edt_signup_phone = findViewById(R.id.edt_signup_phone);
            edt_signup_password = findViewById(R.id.edt_signup_password);
            edt_signup_repass = findViewById(R.id.edt_signup_repass);

            String username = edt_signup_username.getText().toString();
            String email = edt_signup_email.getText().toString();
            String phone = edt_signup_phone.getText().toString();
            String password = edt_signup_password.getText().toString();
            String repassword = edt_signup_repass.getText().toString();

            if(!password.equals(repassword))
            {
                Toast.makeText(this, "Mật khẩu không trùng khớp!", Toast.LENGTH_SHORT).show();
            }
            else if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || repassword.isEmpty())
            {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                String code = generateRandomString(6);
                sendEmail(email, "Your Verification Code", "Your verification code is: " + code);

                Intent intent = new Intent(SignupActivity.this, VerifyEmailActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otpCode", code);  // Gửi mã OTP vào màn hình xác thực
                intent.putExtra("username", username);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password); // Gửi thông tin đăng ký vào màn hình xác thực
                startActivity(intent);
                /*String code = generateRandomString(6);
                sendEmail(email, "Your Verification Code", "Your verification code is: " + code);

                Intent intent = new Intent(SignupActivity.this, VerifyEmailActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otpCode", code);
                startActivity(intent);

                conn = connectSQLServer.CONN();
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    boolean isSignupSuccessful = connectSQLServer.dangKy(username, email,phone, password);
                    runOnUiThread(() -> {
                        if (isSignupSuccessful) {
                            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(SignupActivity.this, MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }
                    });
                });*/
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
