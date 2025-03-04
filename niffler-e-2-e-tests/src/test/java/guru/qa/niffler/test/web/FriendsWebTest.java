package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

@WebTest
public class FriendsWebTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @User(friends = 1)
  @ParameterizedTest
  @EnumSource(Browser.class)
  void friendShouldBePresentInFriendsTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String friendUsername = user.testData().friendsUsernames()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toFriendsPage(driver)
        .checkExistingFriends(friendUsername);
  }

  @User
  @ParameterizedTest
  @EnumSource(Browser.class)
  void friendsTableShouldBeEmptyForNewUser(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toFriendsPage(driver)
        .checkExistingFriendsCount(0);
  }

  @User(incomeInvitations = 1)
  @ParameterizedTest
  @EnumSource(Browser.class)
  void incomeInvitationBePresentInFriendsTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String incomeInvitationUsername = user.testData().incomeInvitationsUsernames()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toFriendsPage(driver)
        .checkExistingInvitations(incomeInvitationUsername);
  }

  @User(outcomeInvitations = 1)
  @ParameterizedTest
  @EnumSource(Browser.class)
  void outcomeInvitationBePresentInAllPeoplesTable(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String outcomeInvitationUsername = user.testData().outcomeInvitationsUsernames()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toAllPeoplesPage(driver)
        .checkInvitationSentToUser(outcomeInvitationUsername);
  }

  @User(friends = 1)
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldRemoveFriend(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String userToRemove = user.testData().friendsUsernames()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toFriendsPage(driver)
        .removeFriend(userToRemove)
        .checkExistingFriendsCount(0);
  }

  @User(incomeInvitations = 1)
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldAcceptInvitation(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String userToAccept = user.testData().incomeInvitationsUsernames()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toFriendsPage(driver)
        .checkExistingInvitationsCount(1)
        .acceptFriendInvitationFromUser(userToAccept)
        .checkExistingInvitationsCount(0);

    driver.refresh();

    new FriendsPage(driver)
        .checkExistingFriendsCount(1)
        .checkExistingFriends(userToAccept);
  }

  @User(incomeInvitations = 1)
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldDeclineInvitation(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String userToDecline = user.testData().incomeInvitationsUsernames()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .getHeader()
        .toFriendsPage(driver)
        .checkExistingInvitationsCount(1)
        .declineFriendInvitationFromUser(userToDecline)
        .checkExistingInvitationsCount(0);

    driver.refresh();

    new FriendsPage(driver).checkExistingFriendsCount(0);

    driver.open(PeoplePage.URL);
    new PeoplePage(driver)
        .checkExistingUser(userToDecline);
  }
}
