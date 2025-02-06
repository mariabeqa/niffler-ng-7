package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingWebTest {

  private static final Config CFG = Config.getInstance();
  private static final String USER_PW = "12345";

  @User(
      username = "duck",
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Advanced 2.0",
          amount = 79990
      )
  )
  @Test
  void categoryDescriptionShouldBeChangedFromTable(SpendJson spend) {
    final String newDescription = "Обучение Niffler Next Generation";

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin("duck", "12345")
        .spendings()
        .editSpending(spend.description())
        .setNewSpendingDescription(newDescription)
        .save();

    new MainPage().spendings().checkTableContainsSpending(newDescription);
  }

  @User(
      spendings = {
              @Spending(
                      category = "Обучение",
                      description = "Обучение Advanced 2.0",
                      amount = 79990
              )
      }
  )
  @Test
  void createSpendingTest(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), USER_PW)
            .spendings()
            .checkTableContainsSpending("Обучение Advanced 2.0");
  }
}

