package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.Session;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;


import java.util.Random;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;

public class SignupActivity extends AppCompatActivity {

    private EditText edt_signup_username;
    private EditText edt_signup_email;
    private EditText edt_signup_phone;
    private EditText edt_signup_password;
    private EditText edt_signup_repass;
    private Button btn_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        btn_signup=findViewById(R.id.btn_signup);
        btn_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                edt_signup_username=findViewById(R.id.edt_signup_username);
                edt_signup_email=findViewById(R.id.edt_signup_email);
                edt_signup_phone=findViewById(R.id.edt_signup_phone);
                edt_signup_password=findViewById(R.id.edt_signup_password);
                edt_signup_repass=findViewById(R.id.edt_signup_repass);


                String username = edt_signup_username.getText().toString();
                String email = edt_signup_email.getText().toString();
                String phone = edt_signup_phone.getText().toString();
                String password = edt_signup_password.getText().toString();
                String repassword = edt_signup_repass.getText().toString();

                if(!validateUserName() || !validateEmail() || !validatePhone() || !validatePassword() || !validateRePassword()){
                    return;
                }
                String code = generateRandomString(6);
                sendEmail(email, "Your Verification Code", "Your verification code is: " + code);

                Intent intent = new Intent(SignupActivity.this, VerifyEmailActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("otpCode", code);  // Gửi mã OTP vào màn hình xác thực
                intent.putExtra("username", username);
                intent.putExtra("phone", phone);
                intent.putExtra("password", password); // Gửi thông tin đăng ký vào màn hình xác thực
                startActivity(intent);

            }
        });

    }

    public void goToSignIn(View view){
        Intent intent = new Intent(SignupActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean validateUserName()
    {
        String username = edt_signup_username.getText().toString();
        if(username.isEmpty()){
            edt_signup_username.setError("Tên đăng nhập không được để trống!");
            return false;
        }
        else{
            edt_signup_username.setError(null);
            return true;
        }
    }

    public boolean validateEmail()
    {
        String email = edt_signup_email.getText().toString();
        if(email.isEmpty()){
            edt_signup_email.setError("Email không được để trống!");
            return false;
        }
        else if(!StringHelper.regexEmailValidationPattern(email)){
            edt_signup_email.setError("Email không hợp lệ!");
            return false;
        }
        else{
            edt_signup_email.setError(null);
            return true;
        }
    }

    public boolean validatePhone() {
        String phone = edt_signup_phone.getText().toString();
        if (phone.isEmpty()) {
            edt_signup_phone.setError("Số điện thoại không được để trống!");
            return false;
        } else {
            edt_signup_phone.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String password = edt_signup_password.getText().toString();
        if (password.isEmpty()) {
            edt_signup_password.setError("Mật khẩu không được để trống!");
            return false;
        } else if (password.length() < 6) {
            edt_signup_password.setError("Mật khẩu phải có ít nhất 6 ký tự!");
            return false;
        } else {
            edt_signup_password.setError(null);
            return true;
        }
    }

    public boolean validateRePassword() {
        String password = edt_signup_password.getText().toString();
        String repass = edt_signup_repass.getText().toString();

        if (repass.isEmpty()) {
            edt_signup_repass.setError("Vui lòng nhập lại mật khẩu!");
            return false;
        } else if (!repass.equals(password)) {
            edt_signup_repass.setError("Mật khẩu nhập lại không khớp!");
            return false;
        } else {
            edt_signup_repass.setError(null);
            return true;
        }
    }


    public static void sendEmail(String toEmail, String subject, String messageBody) {
        new Thread(() -> {
            String fromEmail = "nguyenthianhhuyen03@gmail.com";
            String password = "evxh dkbn nkgm whts";

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
