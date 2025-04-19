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
import vn.iotstar.ecoveggieapp.helpers.SharedPrefManager;
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
        String url = "http://" + StringHelper.SERVER_IP +":9080/api/v1/user/login";

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
                            int userId = response.getInt("user_id");
                            String username = response.getString("username");
                            String email = response.getString("email");
                            String phone = response.getString("phone");
                            String password = response.getString("password");
                            String avatar = response.isNull("avatar") ? "" : response.getString("avatar");
                            String gender = response.isNull("gender") ? "Không xác định" : response.getString("gender");
                            String birthday = response.isNull("birthday") ? "" : response.getString("birthday");

                            SharedPrefManager.getInstance(MainActivity.this).saveUser(
                                    userId,
                                    username,
                                    email,
                                    phone,
                                    password,
                                    avatar,
                                    gender,
                                    birthday
                            );

                            Intent goToHome = new Intent(MainActivity.this, HomeActivity.class);
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
}