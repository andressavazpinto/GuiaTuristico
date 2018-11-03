package service;

import model.Localization;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import util.Url;

public interface LocalizationService {
    String BASE_URL = Url.BASE_URL;

    @GET("localization/{id}")
    Call<Localization> read(@Path("id") int id);

    @POST("localization")
    Call<Integer> register(@Body Localization localization);

    @PUT("localization")
    Call<Void> update(@Body Localization localization);
}
