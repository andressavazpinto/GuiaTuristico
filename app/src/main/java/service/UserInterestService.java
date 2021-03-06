package service;

import java.util.List;

import model.UserInterest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import util.Url;

/**
 * Created by Andressa on 31/05/2018.
 */

public interface UserInterestService {
    String BASE_URL = Url.BASE_URL;

    @POST("users/interests")
    Call<Integer> insert(@Body List<UserInterest> userInterests);

    @DELETE("users/interests/{id}")
    Call<UserInterest> delete(@Path("id") int id);

    @GET("users/interests/{idUser}")
    Call<List<UserInterest>> listByUser(@Path("idUser") int idUser);

    @PUT("users/interests/{idUser}")
    Call<Void> update(@Path("idUser") int idUser, @Body List<UserInterest> userInterests);
}
