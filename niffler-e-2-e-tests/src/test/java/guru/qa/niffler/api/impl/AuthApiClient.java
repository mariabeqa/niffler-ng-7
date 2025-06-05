package guru.qa.niffler.api.impl;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.jupiter.extension.TestMethodContextExtension;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.ExtensionContext;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private final guru.qa.niffler.api.AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl(), true);
        this.authApi = create(AuthApi.class);
    }

    @Step("Initiate authorize request")
    public void preRequest(String codeChallenge) {
        final Response response;

        try {
            response = authApi.authorize(
                    "code",
                        "client",
                        "openid",
                        "http://127.0.0.1:3000/authorized",
                        codeChallenge,
                        "S256"
            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
    }

    @Step("Log request with {username}/{password}")
    public void login(String username, String password) {
        final String code;

        try {
            code = authApi.login(
                            username,
                            password,
                            ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
                    ).execute()
                    .raw()
                    .request()
                    .url()
                    .queryParameter("code");
        } catch (IOException e) {
            throw new AssertionError(e);
        }

        TestMethodContextExtension.context()
                .getStore(ExtensionContext.Namespace.create(AuthApiClient.class))
                .put(
                        "code",
                        code
                );
    }

    @Nonnull
    @Step("Get 'id_token'")
    public String token(String code, String codeVerifier) {
        final Response<JsonNode> response;

        try {
            response = authApi.token(
                    "client",
                    "http://127.0.0.1:3000/authorized",
                    "authorization_code",
                    code,
                    codeVerifier

            ).execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return Objects.requireNonNull(response.body()).get("id_token").asText();
    }
}
