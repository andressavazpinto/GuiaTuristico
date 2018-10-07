package service;

import com.google.gson.JsonObject;

import model.Translate;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TranslationService {

    String BASE_URL = "https://translation.googleapis.com/";

    @POST("language/translate/v2")
    Call<JsonObject> translate(//@Header("Authorization") String auth,
                                @Body Translate translate,
                                @Query("key") String API_KEY);

    @POST("language/translate/v2/languages")
    Call<JsonObject> listLanguages(//@Header("Authorization") String auth,
                                   @Body Translate translate,
                                   @Query("key") String API_KEY);

    @POST("language/translate/v2/detect")
    Call<JsonObject> detect(@Body Translate translate,
                            @Query("key") String API_KEY);
}
