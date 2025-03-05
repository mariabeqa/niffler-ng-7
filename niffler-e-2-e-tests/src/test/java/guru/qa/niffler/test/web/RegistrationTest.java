package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.RegisterPage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldRegisterNewUser(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
    String newUsername = randomUsername();
    String password = "12345";
    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .doRegister();
    new RegisterPage(driver)
        .fillRegisterPage(newUsername, password, password)
        .successSubmit();
    new LoginPage(driver)
        .fillLoginPage(newUsername, password)
        .submit(new MainPage(driver))
        .checkThatPageLoaded();
  }

  @User
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldNotRegisterUserWithExistingUsername(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    String existingUsername = user.username();
    String password = "12345";

    driver.open(LoginPage.URL);
    new LoginPage(driver).doRegister();
    new RegisterPage(driver)
        .fillRegisterPage(existingUsername, password, password)
        .errorSubmit();
    new LoginPage(driver).checkError("Username `" + existingUsername + "` already exists");
  }

  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual(@ConvertWith(BrowserConverter.class) SelenideDriver driver) {
    String newUsername = randomUsername();
    String password = "12345";

    driver.open(LoginPage.URL);
    new LoginPage(driver).doRegister();
    new RegisterPage(driver)
        .fillRegisterPage(newUsername, password, "bad password submit")
        .errorSubmit();
    new LoginPage(driver).checkError("Passwords should be equal");
  }
}
