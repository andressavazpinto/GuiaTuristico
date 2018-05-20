package service;

import model.Usuario;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Andressa on 19/05/2018.
 */

public interface UsuarioService {

    //entre http:// e /Guia, tem que colocar o ipV4 da rede que o pc está conectado (verificar pelo cmd, comando ipconfig)
    //também é necessário que a API esteja ativa na rede
    public static final String BASE_URL = "http://192.168.0.102:8089/GuiaWSMaven/rest/";

    @GET("usuarios/{id}")
    Call<Usuario> consultar(@Path("id") int id);

    //@Headers({"Accept: application/json"})
    @POST("usuarios")
    Call<Integer> cadastrar(@Body Usuario usuario);
}