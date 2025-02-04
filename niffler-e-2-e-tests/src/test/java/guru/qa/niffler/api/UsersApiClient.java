package guru.qa.niffler.api;

import guru.qa.niffler.api.utils.MyCookieJar;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsersApiClient implements UsersClient {

    public static final String USER_PW = "12345";

    OkHttpClient client = new OkHttpClient.Builder()
            .cookieJar(new MyCookieJar())
            .build();

    private final Retrofit retrofitAuth = new Retrofit.Builder()
            .client(client)
            .baseUrl(Config.getInstance().authUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final Retrofit retrofitUD = new Retrofit.Builder()
            .client(client)
            .baseUrl(Config.getInstance().userdataUrl())
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UsersApi usersAuthApi = retrofitAuth.create(UsersApi.class);
    private final UsersApi usersUDApi = retrofitUD.create(UsersApi.class);

    @Override
    public UserJson createUser(String username, String password) {
        String csrfToken;
        Response<Void> response;

        try {
            csrfToken = usersAuthApi.getCsrfToken()
                    .execute()
                    .headers()
                    .get("X-XSRF-TOKEN");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try {
            response = usersAuthApi.createUser(username, password, password, csrfToken)
            .execute();

        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(201, response.code());

        return new UserJson(
                null,
                username,
                null,
                null,
                null,
                CurrencyValues.RUB,
                null,
                null,
                null,
                new TestData(password, null, null)
        );
    }

    @Override
    public void sendInvitation(UserJson targetUser, int count) {
        Response<ResponseBody> response = null;
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson randomUser = createRandomUser();
                try {
                    response = usersUDApi.sendInvitation(
                                    randomUser.username(),
                                    targetUser.username()
                            )
                            .execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            assertEquals(200, response.code());
        }
    }

    @Override
    public void sendInvitation(UserJson user, UserJson targetUser) {
        Response<ResponseBody> response;

        try {
            response = usersUDApi.sendInvitation(
                    user.username(),
                    targetUser.username()
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }

    @Override
    public void addFriend(UserJson targetUser, int count) {
        Response<ResponseBody> inviteResponse;
        Response<ResponseBody> acceptInviteresponse;

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                UserJson randomUser = createRandomUser();
                try {
                    inviteResponse = usersUDApi.sendInvitation(
                            randomUser.username(),
                            targetUser.username()
                    ).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, inviteResponse.code());

                try {
                    acceptInviteresponse = usersUDApi.addFriend(
                            targetUser.username(),
                            randomUser.username()
                            )
                            .execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                assertEquals(200, acceptInviteresponse.code());
            }
        }
    }

    private UserJson createRandomUser() {
        String username = randomUsername();
        return createUser(username, USER_PW);
    }
}
