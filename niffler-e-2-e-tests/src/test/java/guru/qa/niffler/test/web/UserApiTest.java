package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.UsersApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UserApiTest {

    private static final Config CFG = Config.getInstance();
    private static final String USER_PW = "12345";
    static UsersApiClient usersApiClient = new UsersApiClient();

    @Test
    void createUserTest() {
        String username = randomUsername();

        UserJson createdUser = usersApiClient.createUser(
                username,
                USER_PW
        );

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(createdUser.username(),
                        createdUser.testData().password())
                .checkThatPageLoaded();
    }

    @Test
    void sendInvitationTest() {
        String username = randomUsername();

        UserJson targetUser = usersApiClient.createUser(
                username,
                USER_PW
        );
        usersApiClient.sendInvitation(targetUser, 1);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(targetUser.username(), USER_PW)
                .checkThatPageLoaded();
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkAmountOfIncomeInvitations(1);
    }

    @Test
    void addFriendTest() {
        String username = randomUsername();

        UserJson targetUser = usersApiClient.createUser(
                username,
                USER_PW
        );
        usersApiClient.addFriend(targetUser, 1);

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(targetUser.username(), USER_PW)
                .checkThatPageLoaded();
        Selenide.open(FriendsPage.URL, FriendsPage.class)
                .checkAmountOfFriends(1);
    }
}
