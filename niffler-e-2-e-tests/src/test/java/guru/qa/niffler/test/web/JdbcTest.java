package guru.qa.niffler.test.web;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcTest {

  @Test
  void txTest() {
    SpendDbClient spendDbClient = new SpendDbClient();

    SpendJson spend = spendDbClient.createSpend(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                "cat-name-tx-3",
                "duck",
                false
            ),
            CurrencyValues.RUB,
            1000.0,
            "spend-name-tx-3",
            "duck"
        )
    );

    System.out.println(spend);
  }

  @Test
  void springJdbcTest() {
    UsersDbClient usersDbClient = new UsersDbClient();
    UserJson user = usersDbClient.createUser(
        new UserJson(
            null,
            "valentin-6",
            null,
            null,
            null,
            CurrencyValues.RUB,
            null,
            null,
            null
        )
    );
    System.out.println(user);
  }

  @Test
  void findUserWithFriendshipByIdWithJoinRequestTest() {
    UsersDbClient usersDbClient = new UsersDbClient();

    Optional<UserEntity> userByID =
            usersDbClient.findUserByID(UUID.fromString("c7f0e3b7-a0f7-4d6e-a819-57289217cd0b"));

    if (userByID.isPresent()) {
      UserEntity user = userByID.get();
      System.out.println(user);
      System.out.println("Friendship requests: ");
      user.getFriendshipRequests().forEach(System.out::println);
      System.out.println("Friendship addressees: ");
      user.getFriendshipAddressees().forEach(System.out::println);
    }
  }

  @Test
  void addFriendInvitationTest() {
    UsersDbClient usersDbClient = new UsersDbClient();

    UUID requesterUUID = UUID.fromString("598c9c39-e3d5-4f4a-b803-6044da6f5c1e");
    UUID addresseeUUID = UUID.fromString("68adfcea-54c1-4991-84f8-e68156de5d3b");

    usersDbClient.addIncomeInvitation(requesterUUID, addresseeUUID);

    List<FriendshipEntity> requests = usersDbClient.getFriendshipRequestsByUserID(requesterUUID, addresseeUUID);

    assertEquals("PENDING", requests.getFirst().getStatus().name());
  }


  @Test
  void addFriendTest() {
    UsersDbClient usersDbClient = new UsersDbClient();

    UUID requesterUUID = UUID.fromString("36b16a70-62e6-4727-912a-f6dfe66cdbe5");
    UUID addresseeUUID = UUID.fromString("1f50791c-69a0-49e4-a81a-502a153174ca");

    usersDbClient.addFriend(requesterUUID, addresseeUUID);

    List<FriendshipEntity> requests = usersDbClient.getFriendshipRequestsByUserID(requesterUUID, addresseeUUID);

    assertEquals(2, requests.size());
    assertTrue(requests.stream().allMatch(f -> f.getStatus().name().equals("ACCEPTED")));
  }

}
