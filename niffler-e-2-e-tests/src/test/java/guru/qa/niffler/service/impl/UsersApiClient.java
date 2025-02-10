package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApiClient;
import guru.qa.niffler.api.UserdataApiClient;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

  private final AuthApiClient authApiClient = new AuthApiClient();
  private final UserdataApiClient userdataApiClient = new UserdataApiClient();

  @NotNull
  @Override
  public UserJson createUser(String username, String password) {
    authApiClient.register(username, password);

    return Objects.requireNonNull(
            userdataApiClient.currentUser(username)
    );
  }

  @Override
  public void addIncomeInvitation(UserJson targetUser, int count) {

  }

  @Override
  public void addOutcomeInvitation(UserJson targetUser, int count) {

  }

  @Override
  public void addFriend(UserJson targetUser, int count) {

  }
}
