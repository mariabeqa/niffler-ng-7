package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.SearchField;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage extends BasePage<FriendsPage> {

  public static final String URL = CFG.frontUrl() + "people/friends";

  private final SelenideElement peopleTab;
  private final SelenideElement allTab;

  private final SearchField searchInput;
  private final SelenideElement popup;

  private final SelenideElement requestsTable;
  private final SelenideElement friendsTable;
  private final SelenideElement pagePrevBtn;
  private final SelenideElement pageNextBtn;


  public FriendsPage(SelenideDriver driver) {
    super(driver);
    this.searchInput = new SearchField(driver);
    this.peopleTab = driver.$("a[href='/people/friends']");
    this.allTab = driver.$("a[href='/all']");
    this.popup = driver.$("div[role='dialog']");
    this.requestsTable = driver.$("#requests");
    this.friendsTable = driver.$("#friends");
    this.pagePrevBtn = driver.$("#page-prev");
    this.pageNextBtn = driver.$("#page-next");
  }

  @Step("Check that the page is loaded")
  @Override
  @Nonnull
  public FriendsPage checkThatPageLoaded() {
    peopleTab.shouldBe(Condition.visible);
    allTab.shouldBe(Condition.visible);
    return this;
  }

  @Step("Check that friends count is equal to {expectedCount}")
  @Nonnull
  public FriendsPage checkExistingFriendsCount(int expectedCount) {
    friendsTable.$$("tr").shouldHave(size(expectedCount));
    return this;
  }

  @Step("Check that income invitations count is equal to {expectedCount}")
  @Nonnull
  public FriendsPage checkExistingInvitationsCount(int expectedCount) {
    requestsTable.$$("tr").shouldHave(size(expectedCount));
    return this;
  }

  @Step("Check that friends list contains data '{0}'")
  @Nonnull
  public FriendsPage checkExistingFriends(String... expectedUsernames) {
    friendsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

  @Nonnull
  public FriendsPage checkExistingInvitations(String... expectedUsernames) {
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(expectedUsernames));
    return this;
  }

  @Step("Delete user from friends: {username}")
  @Nonnull
  public FriendsPage removeFriend(String username) {
    SelenideElement friendRow = friendsTable.$$("tr").find(text(username));
    friendRow.$("button[type='button']").click();
    popup.$(byText("Delete")).click(usingJavaScript());
    return this;
  }

  @Step("Accept invitation from user: {username}")
  @Nonnull
  public FriendsPage acceptFriendInvitationFromUser(String username) {
    SelenideElement friendRow = requestsTable.$$("tr").find(text(username));
    friendRow.$(byText("Accept")).click();
    return this;
  }

  @Step("Decline invitation from user: {username}")
  @Nonnull
  public FriendsPage declineFriendInvitationFromUser(String username) {
    SelenideElement friendRow = requestsTable.$$("tr").find(text(username));
    friendRow.$(byText("Decline")).click();
    popup.$(byText("Decline")).click(usingJavaScript());
    return this;
  }
}
