package service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import util.Url;

public interface MailService {
    String BASE_URL = Url.BASE_URL;

    @GET("mail/generatepass/{email}")
    Call<Void> generatePass(@Path("email") String email);
}
