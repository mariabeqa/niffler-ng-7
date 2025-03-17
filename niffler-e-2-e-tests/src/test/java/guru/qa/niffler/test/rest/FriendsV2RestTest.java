package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.impl.GatewayV2ApiClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RestTest
public class FriendsV2RestTest {

  @RegisterExtension
  private static final ApiLoginExtension apiLoginExtension = ApiLoginExtension.api();

  private final GatewayV2ApiClient gatewayApiClient = new GatewayV2ApiClient();

  @ApiLogin
  @User(friends = 1, incomeInvitations = 1)
  @Test
  void friendsAndIncomeInvitationsListShouldBeReturned(UserJson user, @Token String token) {
    final UserJson expectedFriend = user.testData().friends().getFirst();
    final UserJson expectedInvitation = user.testData().incomeInvitations().getFirst();

    final RestResponsePage<UserJson> response = gatewayApiClient.allFriends(token, 0, 2, null, null);

    assertEquals(2, response.getContent().size());

    final UserJson actualInvitation = response.getContent().getFirst();
    final UserJson actualFriend = response.getContent().getLast();

    assertEquals(expectedFriend.id(), actualFriend.id());
    assertEquals(expectedInvitation.id(), actualInvitation.id());
  }

  @ApiLogin
  @User(friends = 5)
  @Test
  void friendsAndIncomeInvitationsListShouldBeReturnedSorted(UserJson user, @Token String token) {
    final RestResponsePage<UserJson> response = gatewayApiClient.allFriends(
            token,
            0,
            5,
            "username,DESC",
            null);

    List<String> actualUsernames = response.getContent().stream()
            .map(UserJson::username)
            .toList();

    List<String> expectedUsernames = new ArrayList<>(actualUsernames);
    expectedUsernames.sort(Comparator.reverseOrder());

    assertEquals(expectedUsernames, actualUsernames);
  }
}
