package service;

import model.ConnectGuides;
import model.Search;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import util.Url;

public interface ConnectGuidesService {
    public static final String BASE_URL = Url.BASE_URL;

    @PUT("connectguides")
    Call<Void> update(@Body Search search);

    @POST("connectguides")
    Call<ConnectGuides> searchRandomly(@Body Search search);

    @GET("connectguides/{id}")
    Call<ConnectGuides> read(@Path("id") int id);

    @DELETE("connectguides/{id}")
    Call<Void> delete(@Path("id") int id);
}
