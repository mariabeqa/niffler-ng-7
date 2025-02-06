package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {

  public static final String URL = Config.getInstance().authUrl() + "login";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitButton = $("button[type='submit']");
  private final SelenideElement registerButton = $("a[href='/register']");
  private final SelenideElement errorContainer = $(".form__error");

  @Step("Нажимаем на кнопку Регистрации")
  public RegisterPage doRegister() {
    registerButton.click();
    return new RegisterPage();
  }

  @Step("Логинимся под {username}/{password}")
  public MainPage successLogin(String username, String password) {
    login(username, password);
    return new MainPage();
  }

  private void login(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitButton.click();
  }

  @Step("Проверяем текст ошибки - '{error}'")
  public LoginPage checkError(String error) {
    errorContainer.shouldHave(text(error));
    return this;
  }
}
