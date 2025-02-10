package guru.qa.niffler.api;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class AuthApiClient extends RestClient {

    private final AuthApi authApi;

    public AuthApiClient() {
        super(CFG.authUrl());
        this.authApi = create(AuthApi.class);
    }

    public void register(@Nonnull String username, @Nonnull String password) {
        try {
            authApi.getRegisterForm().execute();
            authApi.register(
                    username,
                    password,
                    password,
                    ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
            ).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
