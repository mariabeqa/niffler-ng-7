package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

@WebTest
public class RegistrationTest {

  @Test
  void shouldRegisterNewUser() {
    String newUsername = randomUsername();
    String password = "12345";
    Selenide.open(LoginPage.URL, LoginPage.class)
        .doRegister()
        .fillRegisterPage(newUsername, password, password)
        .successSubmit()
        .fillLoginPage(newUsername, password)
        .submit(new MainPage())
        .checkThatPageLoaded();
  }

  @User
  @Test
  void shouldNotRegisterUserWithExistingUsername(UserJson user) {
    String password = "12345";

    LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
    loginPage.doRegister()
        .fillRegisterPage(user.username(), password, password)
        .errorSubmit();
    loginPage.checkError("Username `" + user.username() + "` already exists");
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    String newUsername = randomUsername();
    String password = "12345";

    LoginPage loginPage = Selenide.open(LoginPage.URL, LoginPage.class);
    loginPage.doRegister()
        .fillRegisterPage(newUsername, password, "bad password submit")
        .errorSubmit();
    loginPage.checkError("Passwords should be equal");
  }
}
