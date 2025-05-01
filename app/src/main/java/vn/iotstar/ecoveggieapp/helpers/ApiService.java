package vn.iotstar.ecoveggieapp.helpers;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.iotstar.ecoveggieapp.models.AddressModel;
import vn.iotstar.ecoveggieapp.models.CartItemModel;
import vn.iotstar.ecoveggieapp.models.OrderModel;

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


    @GET("address/default")
    Call<AddressModel> getDefaultAddress(@Query("user_id") int userId);

    @GET("address/all")
    Call<List<AddressModel>> getAllAddresses(@Query("user_id") int userId);

    @PUT("address/{id}")
    Call<AddressModel> updateAddress(@Path("id") int id, @Body AddressModel address);

    @GET("cart/user")
    Call<List<CartItemModel>> getCartItems(@Query("user_id") int userId);


    @FormUrlEncoded
    @POST("order/create")
    Call<OrderModel> createOrder(
            @Field("customer_id") int customerId,
            @Field("total_amount") double totalAmount,
            @Field("payment_method") String paymentMethod,
            @Field("status") String status,
            @Field("note") String note,
            @Field("address_id") int addressId
    );

    @FormUrlEncoded
    @POST("order/detail/create")
    Call<ResponseBody> createOrderDetail(
            @Field("order_id") int orderId,
            @Field("product_id") int productId,
            @Field("quantity") int quantity,
            @Field("price") double price
    );

    @POST("points/reset")
    Call<ResponseBody> resetTotalPoints(@Query("user_id") int userId);


}
