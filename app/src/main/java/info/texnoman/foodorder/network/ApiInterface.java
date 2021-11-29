package info.texnoman.foodorder.network;


import java.util.Map;
import info.texnoman.foodorder.forretrofit.GetToken;
import info.texnoman.foodorder.forretrofit.UserResponse;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @POST("/api/customer/auth/register")
    Call<UserResponse> getUserResponse(@Query("phone") String phone);

    @POST("/api/customer/auth/confirm")
    Call<GetToken> getToken(@QueryMap Map<String,String> params);
}
