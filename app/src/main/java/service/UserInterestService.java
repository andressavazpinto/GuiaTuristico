package service;

import model.UserInterest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.POST;
import retrofit2.http.Path;
import util.Url;

/**
 * Created by Andressa on 31/05/2018.
 */

public interface UserInterestService {
    public static final String BASE_URL = Url.BASE_URL;

    //@Headers({"Accept: application/json"})
    @POST("users/interests")
    Call<Integer> insert(@Body UserInterest userInterest);

    @DELETE("users/interests/{id}")
    Call<UserInterest> delete(@Path("id") int id);
}