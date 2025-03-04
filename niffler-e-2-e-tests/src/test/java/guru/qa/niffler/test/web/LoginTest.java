package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static com.codeborne.selenide.Condition.text;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class LoginTest {

  @RegisterExtension
  private final BrowserExtension browserExtension = new BrowserExtension();
  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @User
  @ParameterizedTest
  @EnumSource(Browser.class)
  void mainPageShouldBeDisplayedAfterSuccessLogin(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    driver.open(LoginPage.URL);

    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded();
  }

  @ParameterizedTest
  @EnumSource(Browser.class)
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
    driver.open(LoginPage.URL);

    new LoginPage(driver)
        .fillLoginPage(randomUsername(), "BAD")
        .submit(new LoginPage(driver))
        .checkError("Bad credentials");

    driver.$(".logo-section__text").should(text("Niffler"));
  }
}
