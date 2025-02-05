package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class FriendsWebTest {

  private static final Config CFG = Config.getInstance();
  public static final String USER_PW = "12345";
  static UsersApiClient usersApiClient = new UsersApiClient();

  @Test
  void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), USER_PW)
        .checkThatPageLoaded()
        .friendsPage()
        .checkUserHasOneFriend();
  }

  @Test
  void friendsTableShouldBeEmptyForNewUser(@UserType(EMPTY) UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), USER_PW)
        .checkThatPageLoaded()
        .friendsPage()
        .checkNoExistingFriends();
  }

  @Test
  void incomeInvitationBePresentInFriendsTable(@UserType(WITH_INCOME_REQUEST) UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), USER_PW)
        .checkThatPageLoaded()
        .friendsPage()
        .checkUserHasOneIncomeInvitation();
  }

  @Test
  void outcomeInvitationBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin(user.username(), USER_PW)
        .checkThatPageLoaded()
        .allPeoplesPage()
        .checkUserHasOneOutcomeInvitation();
  }

  @Test
  void searchForFriendsInTable() {
    UserJson user = usersApiClient.createUser(
            randomUsername(),
            USER_PW
    );

    UserJson targetUser = usersApiClient.createUser(
            randomUsername(),
            USER_PW
    );
    Selenide.open(CFG.frontUrl(), LoginPage.class)
         .successLogin(user.username(), USER_PW)
         .checkThatPageLoaded()
         .allPeoplesPage()
         .sendInvitationToPersonWithName(targetUser.username());
  }
}
