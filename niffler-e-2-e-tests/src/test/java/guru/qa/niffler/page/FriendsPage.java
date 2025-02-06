package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import static com.codeborne.selenide.CollectionCondition.*;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {

  public static final String URL = Config.getInstance().frontUrl() + "people/friends";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement friendsTable = $("#friends");
  private final SelenideElement popUp = $("div[role='dialog']");

  private final SearchField searchField = new SearchField();

  public FriendsPage checkExistingFriends(String... expectedUsernames) {
    friendsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

  public FriendsPage checkNoExistingFriends() {
    friendsTable.$$("tr").shouldHave(size(0));
    return this;
  }

  public FriendsPage checkExistingInvitations(String... expectedUsernames) {
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

  public FriendsPage checkAmountOfIncomeInvitations(int size) {
    requestsTable.$$("tr").shouldHave(size(size));
    return this;
  }

  public FriendsPage checkAmountOfFriends(int size) {
    friendsTable.$$("tr").shouldHave(size(size));
    return this;
  }

  @Step("Удаляем друга с именем {username}")
  public FriendsPage removeFriend(String username) {
    friendsTable.$$("tr")
            .find(text(username))
            .$("button").click();
    popUp.$(byText("Delete")).click();
    return this;
  }

  @Step("Принимаем инвайт от {username}")
  public FriendsPage acceptFriendInvitationFromUser(String username) {
    requestsTable.$$("tr")
            .find(text(username))
            .$(byText("Accept"))
            .click();
    return this;
  }

  @Step("Принимаем инвайт")
  public FriendsPage acceptFriendInvitation() {
    requestsTable.$$("tr")
            .first()
            .$(byText("Accept"))
            .click();
    return this;
  }

  @Step("Отклоняем инвайт от {username}")
  public FriendsPage declineFriendInvitationFromUser(String username) {
    requestsTable.$$("tr")
            .find(text(username))
            .$(byText("Decline"))
            .click();
    popUp.$(byText("Decline")).click();
    return this;
  }

  @Step("Отклоняем инвайт")
  public FriendsPage declineFriendInvitation() {
    requestsTable.$$("tr")
            .first()
            .$(byText("Decline"))
            .click();
    popUp.$(byText("Decline")).click();
    return this;
  }
}
