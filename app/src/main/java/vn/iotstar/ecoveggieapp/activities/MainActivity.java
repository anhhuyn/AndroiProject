package vn.iotstar.ecoveggieapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import vn.iotstar.ecoveggieapp.R;
import vn.iotstar.ecoveggieapp.helpers.StringHelper;


public class MainActivity extends AppCompatActivity {

    //Connection conn;
    //String str;

    private TextView txt_signup, txt_forgotpass;
    private Button btn_login;
    private EditText edtEmail, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        txt_signup = findViewById(R.id.txt_login_signup);
        txt_forgotpass = findViewById(R.id.txt_login_forgotPass);
        edtEmail = findViewById(R.id.edt_login_email);
        edtPassword = findViewById(R.id.edt_login_password);
        btn_login = findViewById(R.id.btn_login);

        txt_signup.setOnClickListener(this::goToSignUp);
        txt_forgotpass.setOnClickListener(this::goToForgotPass);

        // Set login button on click:
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                authenticateUser();
            }
        });


    }

    public void authenticateUser(){
        if(!validateEmail() || !validatePassword() ){
            return;
        }
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        // The URL Posting TO:
        String url = "http://192.168.1.19:9080/api/v1/user/login";

        // Set Parameters:
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("email", edtEmail.getText().toString());
        params.put("password", edtPassword.getText().toString());

        // Set Request Object:
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try{
                            String username = (String) response.get("username");
                            String email = (String) response.get("email");
                            String phone = (String) response.get("phone");

                            Intent goToHome = new Intent(MainActivity.this, HomeActivity.class);
                            goToHome.putExtra("username", username);
                            goToHome.putExtra("email", email);
                            goToHome.putExtra("phone", phone);
                            startActivity(goToHome);
                            finish();

                        }catch (JSONException e){
                            e.printStackTrace();
                            System.out.println(e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                System.out.println("Lỗi:"+ error.getMessage());
                Toast.makeText(MainActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
            }
        });//
        queue.add(jsonObjectRequest);
    }

    public boolean validateEmail()
    {
        String email = edtEmail.getText().toString();
        if(email.isEmpty()){
            edtEmail.setError("Email không được để trống!");
            return false;
        }
        else if(!StringHelper.regexEmailValidationPattern(email)){
            edtEmail.setError("Email không hợp lệ!");
            return false;
        }
        else{
            edtEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String password = edtPassword.getText().toString();
        if (password.isEmpty()) {
            edtPassword.setError("Mật khẩu không được để trống!");
            return false;
        }
        else {
            edtPassword.setError(null);
            return true;
        }
    }

    public void goToSignUp(View view){
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
        finish();
    }

    public void goToForgotPass(View view){
        Intent intent = new Intent(MainActivity.this, ForgotPassActivity.class);
        startActivity(intent);
        finish();
    }







        /*ConnectSQLServer connectSQLServer = new ConnectSQLServer();
        conn = connectSQLServer.CONN();
        //connect();

        txt_forgotpass = findViewById(R.id.txt_login_forgotPass);
        txt_forgotpass.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, ForgotPassActivity.class);
            startActivity(intent);
        });

        txt_signup = findViewById(R.id.txt_login_signup);
        txt_signup.setOnClickListener(v ->
        {
            Intent intent = new Intent(MainActivity.this, SignupActivity.class);
            startActivity(intent);
        });

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> {
            EditText edtEmail = findViewById(R.id.edt_login_email);
            EditText edtPassword = findViewById(R.id.edt_login_password);

            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                ExecutorService executorService = Executors.newSingleThreadExecutor();
                executorService.execute(() -> {
                    boolean isLoginSuccessful = connectSQLServer.kiemTraDangNhap(email, password);
                    runOnUiThread(() -> {
                        if (isLoginSuccessful) {
                            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                            // Chuyển sang màn hình chính
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(this, "Email hoặc mật khẩu không đúng!", Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

    }
    public void connect(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{
            try {
                if(conn == null){
                    str = "Error";
                }else {
                    str = "Connected with SQL server";
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                }catch (InterruptedException e){
                    throw new RuntimeException(e);
                }
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

            });
        });
    }*/


}