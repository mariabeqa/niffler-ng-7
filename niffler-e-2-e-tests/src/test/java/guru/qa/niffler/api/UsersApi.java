package guru.qa.niffler.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface UsersApi {

    @POST("register")
    @FormUrlEncoded
    Call<Void> createUser(
            @Field("username") String username,
            @Field("password") String password,
            @Field("passwordSubmit") String passwordSubmit,
            @Field("_csrf") String csrf);

    @GET("register")
    Call<ResponseBody> getCsrfToken();

    @POST("internal/invitations/send")
    Call<ResponseBody> sendInvitation(@Query("username") String username,
                              @Query("targetUsername") String targetUsername);

    @POST("internal/invitations/accept")
    Call<ResponseBody> addFriend(@Query("username") String username,
                         @Query("targetUsername") String targetUsername);

}
