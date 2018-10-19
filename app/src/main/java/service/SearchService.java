package service;

import java.util.List;

import model.Search;
import model.SearchByRegion;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import util.Url;

public interface SearchService {
    String BASE_URL = Url.BASE_URL;

    @GET("search/user/{id}")
    Call<Search> read(@Path("id") int id);

    @PUT("search")
    Call<Void> update(@Body Search search);

    @GET("search/regions/{id}")
    Call<List<SearchByRegion>> getRegions(@Path("id") int id);

    @GET("users/{city}/{id}")
    Call<List<SearchByRegion>> getUsersRegions(@Path("city") String city, @Path("id") int id);
}