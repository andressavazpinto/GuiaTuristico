package service;

import model.Search;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import util.Url;

public interface ConnectGuidesService {
    public static final String BASE_URL = Url.BASE_URL;

    @PUT("connectguides")
    Call<Void> update(@Body Search search);

    @POST("connectguides")
    Call<Boolean> searchRandomly(@Body Search search);
}
