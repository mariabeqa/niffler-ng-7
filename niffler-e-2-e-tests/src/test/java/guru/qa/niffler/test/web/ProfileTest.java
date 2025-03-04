package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.jupiter.converter.BrowserConverter;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.Browser;
import guru.qa.niffler.utils.SelenideUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

import java.awt.image.BufferedImage;
import java.io.IOException;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomName;

@WebTest
public class ProfileTest {

  private final SelenideDriver driver = new SelenideDriver(SelenideUtils.chromeConfig);

  @User(
      categories = @Category(
          archived = true
      )
  )
  @ParameterizedTest
  @EnumSource(Browser.class)
  void archivedCategoryShouldPresentInCategoriesList(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded();

    driver.open(ProfilePage.URL);
    new ProfilePage(driver)
        .checkArchivedCategoryExists(categoryName);
  }

  @User(
      categories = @Category(
          archived = false
      )
  )
  @ParameterizedTest
  @EnumSource(Browser.class)
  void activeCategoryShouldPresentInCategoriesList(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String categoryName = user.testData().categoryDescriptions()[0];

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded();

    driver.open(ProfilePage.URL);
    new ProfilePage(driver)
        .checkCategoryExists(categoryName);
  }

  @User
  @ScreenShotTest(value = "img/expected-avatar.png")
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldUpdateProfileWithAllFieldsSet(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user, BufferedImage expectedAvatar) throws IOException {
    final String newName = randomName();

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded();
    new MainPage(driver)
        .getHeader()
        .toProfilePage(driver)
        .uploadPhotoFromClasspath("img/cat.jpeg")
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    driver.refresh();

    new ProfilePage(driver).checkName(newName)
        .checkPhotoExist()
        .checkPhoto(expectedAvatar);
  }

  @User
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldUpdateProfileWithOnlyRequiredFields(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    final String newName = randomName();

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage(driver)
        .setName(newName)
        .submitProfile()
        .checkAlertMessage("Profile successfully updated");

    driver.refresh();

    new ProfilePage(driver).checkName(newName);
  }

  @User
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldAddNewCategory(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    String newCategory = randomCategoryName();

    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage(driver)
        .addCategory(newCategory)
        .checkAlertMessage("You've added new category:")
        .checkCategoryExists(newCategory);
  }

  @User(
      categories = {
          @Category(name = "Food"),
          @Category(name = "Bars"),
          @Category(name = "Clothes"),
          @Category(name = "Friends"),
          @Category(name = "Music"),
          @Category(name = "Sports"),
          @Category(name = "Walks"),
          @Category(name = "Books")
      }
  )
  @ParameterizedTest
  @EnumSource(Browser.class)
  void shouldForbidAddingMoreThat8Categories(@ConvertWith(BrowserConverter.class) SelenideDriver driver, UserJson user) {
    driver.open(LoginPage.URL);
    new LoginPage(driver)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage(driver))
        .checkThatPageLoaded()
        .getHeader()
        .toProfilePage(driver)
        .checkThatCategoryInputDisabled();
  }
}
