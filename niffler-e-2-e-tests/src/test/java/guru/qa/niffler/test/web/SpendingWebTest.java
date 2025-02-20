package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import guru.qa.niffler.utils.ScreenDiffResult;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;

@WebTest
public class SpendingWebTest {

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @Test
  void categoryDescriptionShouldBeChangedFromTable(UserJson user) {
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getSpendingTable()
        .editSpending("Обучение Advanced 2.0")
        .setNewSpendingDescription(newDescription)
        .saveSpending();

    new MainPage().getSpendingTable()
        .checkTableContains(newDescription);
  }

  @User
  @Test
  void shouldAddNewSpending(UserJson user) {
    String category = "Friends";
    int amount = 100;
    Date currentDate = new Date();
    String description = RandomDataUtils.randomSentence(3);

    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingCategory(category)
        .setNewSpendingAmount(amount)
        .setNewSpendingDate(currentDate)
        .setNewSpendingDescription(description)
        .saveSpending()
        .checkAlertMessage("New spending is successfully created");

    new MainPage().getSpendingTable()
        .checkTableContains(description);
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyCategory(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingAmount(100)
        .setNewSpendingDate(new Date())
        .saveSpending()
        .checkFormErrorMessage("Please choose category");
  }

  @User
  @Test
  void shouldNotAddSpendingWithEmptyAmount(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getHeader()
        .addSpendingPage()
        .setNewSpendingCategory("Friends")
        .setNewSpendingDate(new Date())
        .saveSpending()
        .checkFormErrorMessage("Amount has to be not less then 0.01");
  }

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @Test
  void deleteSpendingTest(UserJson user) {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .fillLoginPage(user.username(), user.testData().password())
        .submit(new MainPage())
        .getSpendingTable()
        .deleteSpending("Обучение Advanced 2.0")
        .checkTableSize(0);
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected/expected-stat.png")
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    MainPage mainPage = Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage());

    mainPage.statComponent()
            .waitForPieChartToLoad();

    BufferedImage actual = ImageIO.read(mainPage.statComponent().pieChartImage().screenshot());

    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected/expected-empty-spendings.png")
  void shouldUpdateStatAfterSpendingIsRemoved(UserJson user, BufferedImage expected) throws IOException {
    MainPage mainPage = Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage());

    mainPage
            .getSpendingTable()
            .deleteSpending("Обучение Advanced 2.0")
            .checkTableSize(0);

    Selenide.refresh();

    BufferedImage actual = ImageIO.read(
            mainPage.statComponent().pieChartImage().screenshot()
    );
    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected/expected-updated-spending.png")
  void shouldUpdateStatAfterSpendingIsUpdated(UserJson user, BufferedImage expected) throws IOException {
    final int newAmount = 5000;
    MainPage mainPage = Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage());

    mainPage
            .getSpendingTable()
            .editSpending("Обучение Advanced 2.0")
            .setNewSpendingAmount(newAmount)
            .saveSpending();

    mainPage.statComponent()
            .waitForPieChartToLoad()
            .checkBubblesHasText("Обучение " + newAmount);

    BufferedImage actual = ImageIO.read(
            mainPage.statComponent().pieChartImage().screenshot()
    );
    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 79990
          )
  )
  @ScreenShotTest("img/expected/expected-stat.png")
  void shouldUpdateStatAfterCategoryIsArchived(UserJson user, BufferedImage expected) throws IOException {
    MainPage mainPage = Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage());

    mainPage
            .getHeader()
            .toProfilePage()
            .updateCategory("Обучение");

    Selenide.open(MainPage.URL, MainPage.class)
            .statComponent()
            .waitForPieChartToLoad()
            .checkBubblesHasText("Archived " + "79990");

    BufferedImage actual = ImageIO.read(
            mainPage.statComponent().pieChartImage().screenshot()
    );
    assertFalse(new ScreenDiffResult(
            expected,
            actual
    ));
  }

  @User(
          spendings = @Spending(
                  category = "Обучение",
                  description = "Обучение Advanced 2.0",
                  amount = 50000
          )
  )
  @ScreenShotTest(
          value = "img/expected/expected-stat.png",
          rewriteExpected = true
  )
  void overwriteScreenshotTest(UserJson user, BufferedImage expected) throws IOException {
    MainPage mainPage = Selenide.open(LoginPage.URL, LoginPage.class)
            .fillLoginPage(user.username(), user.testData().password())
            .submit(new MainPage());

    mainPage.statComponent()
            .waitForPieChartToLoad();

    BufferedImage actual = ImageIO.read(mainPage.statComponent().pieChartImage().screenshot());
    assertFalse(new ScreenDiffResult(
            actual,
            expected
    ));
  }
}

