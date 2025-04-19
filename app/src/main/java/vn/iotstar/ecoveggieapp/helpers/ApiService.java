package vn.iotstar.ecoveggieapp.helpers;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.PUT;
import retrofit2.http.Part;

public interface ApiService {

    @Multipart
    @PUT("user/update")
    Call<String> updateUserInfoWithAvatar(
            @Part("user_id") RequestBody userId,
            @Part("username") RequestBody username,
            @Part("gender") RequestBody gender,
            @Part("birthday") RequestBody birthday,
            @Part MultipartBody.Part avatar
    );
}
