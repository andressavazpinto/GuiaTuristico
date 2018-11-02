package service;

import model.Chat;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import to.ChatConnectTO;
import util.Url;

public interface ChatService {

    String BASE_URL = Url.BASE_URL;

    @GET("chat/{idUser}")
    Call<Chat> read(@Path("idUser") int id);

    @POST("chat")
    Call<Integer> register(@Body Chat chat);

    @PUT("chat")
    Call<Void> update(@Body Chat chat);

    @POST("chat/left")
    Call<Void> leftSession(@Body ChatConnectTO to);
}
