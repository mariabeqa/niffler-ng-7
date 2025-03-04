package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.PeoplePage;
import guru.qa.niffler.page.ProfilePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class Header extends BaseComponent<Header> {

  private final SelenideElement mainPageLink;
  private final SelenideElement addSpendingBtn;
  private final SelenideElement menuBtn;
  private final SelenideElement menu;
  private final ElementsCollection menuItems;

  public Header(SelenideDriver driver) {
    super(driver.$("#root header"));
    this.mainPageLink = driver.$("a[href*='/main']");
    this.addSpendingBtn = driver.$("a[href*='/spending']");
    this.menuBtn = driver.$("button[aria-label='Menu']");
    this.menu = driver.$("ul[role='menu']");
    this.menuItems = driver.$$("li");
  }

  @Step("Open Friends page")
  @Nonnull
  public FriendsPage toFriendsPage(SelenideDriver driver) {
    menuBtn.click();
    menuItems.find(text("Friends")).click();
    return new FriendsPage(driver);
  }

  @Step("Open All Peoples page")
  @Nonnull
  public PeoplePage toAllPeoplesPage(SelenideDriver driver) {
    menuBtn.click();
    menuItems.find(text("All People")).click();
    return new PeoplePage(driver);
  }

  @Step("Open Profile page")
  @Nonnull
  public ProfilePage toProfilePage(SelenideDriver driver) {
    menuBtn.click();
    menuItems.find(text("Profile")).click();
    return new ProfilePage(driver);
  }

  @Step("Sign out")
  @Nonnull
  public LoginPage signOut() {
    menuBtn.click();
    menuItems.find(text("Sign out")).click();
    return new LoginPage();
  }

  @Step("Add new spending")
  @Nonnull
  public void addSpendingPage() {
    addSpendingBtn.click();
  }

  @Step("Go to main page")
  @Nonnull
  public MainPage toMainPage(SelenideDriver driver) {
    mainPageLink.click();
    return new MainPage(driver);
  }
}
