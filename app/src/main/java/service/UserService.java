package service;

import model.User;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import util.Url;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Andressa on 19/05/2018.
 */

public interface UserService {

    String BASE_URL = Url.BASE_URL;

    @GET("users/{id}")
    Call<User> read(@Path("id") int id);

    @POST("users")
    Call<Integer> register(@Body User user);

    @POST("users/login")
    Call<User> login(@Body User user);

    @PUT("users")
    Call<User> update(@Body User user);

    @GET("users/email/{email}")
    Call<Boolean> checkEmail(@Path("email") String email);

    @GET("users/pass/{email}")
    Call<String> getPass(@Path("email") String email);
}