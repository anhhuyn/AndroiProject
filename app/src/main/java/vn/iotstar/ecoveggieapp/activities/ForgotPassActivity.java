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
    private CountDownTimer countDownTimer;
    private TextView otpTimerText;
    private boolean isOtpValid = true;

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

        otpTimerText = findViewById(R.id.otpTimerText);
        startOtpTimer(); // Bắt đầu đếm ngược

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
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1 && nextIndex < otpBoxes.length) {
                        otpBoxes[nextIndex].requestFocus();
                    }
                }
            });
        }

        btn_verifyOtp = findViewById(R.id.btn_verifyemail_verify);
        btn_verifyOtp.setOnClickListener(v -> {
            if (!isOtpValid) {
                Toast.makeText(ForgotPassActivity.this, "Mã OTP đã hết hạn, vui lòng gửi lại!", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuilder otpInput = new StringBuilder();
            for (EditText otpBox : otpBoxes) {
                otpInput.append(otpBox.getText().toString());
            }

            if (otpInput.toString().equals(code)) {
                Toast.makeText(ForgotPassActivity.this, "OTP đúng!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ForgotPassActivity.this, ResetPasswordActivity.class);
                intent.putExtra("email", edt_email.getText().toString());
                intent.putExtra("verification_code", code);
                startActivity(intent);
            } else {
                Toast.makeText(ForgotPassActivity.this, "Mã OTP không chính xác", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startOtpTimer() {
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                long secondsRemaining = millisUntilFinished / 1000;
                otpTimerText.setText("Mã OTP hết hạn sau: " + secondsRemaining + " giây");
            }

            public void onFinish() {
                otpTimerText.setText("Mã OTP đã hết hạn!");
                isOtpValid = false;
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
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
