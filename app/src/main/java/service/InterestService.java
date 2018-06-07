package service;

import java.util.List;

import model.Interest;
import retrofit2.Call;
import retrofit2.http.GET;
import util.Url;

/**
 * Created by Andressa on 31/05/2018.
 */

public interface InterestService {
    public static final String BASE_URL = Url.BASE_URL;

    @GET("interests")
    Call<List<Interest>> list();

}
