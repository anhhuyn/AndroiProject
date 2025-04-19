package vn.iotstar.ecoveggieapp.helpers;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import vn.iotstar.ecoveggieapp.models.ProductModel;

public interface ProductApi {
    @GET("products/{id}")
    Call<ProductModel> getProductById(@Path("id") int productId);
}

