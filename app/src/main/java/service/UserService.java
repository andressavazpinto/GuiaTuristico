package service;

import model.Grade;
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

    @POST("users/score/{id}")
    Call<Void> updateScore(@Path("id") int idUser, @Body Grade grade);

    @GET("users/check/{email}")
    Call<Boolean> checkEmail(@Path("email") String email);

    @GET("users/score/{id}")
    Call<Double> getScore(@Path("id") int idUser);
}