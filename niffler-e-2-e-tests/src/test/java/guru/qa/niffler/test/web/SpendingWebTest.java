package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class SpendingWebTest {

    private static final Config CFG = Config.getInstance();

    @User(
         username = "ivan",
         spendings = {
                 @Spending(
                         category = "Food",
                         description = "Tuna salad",
                         amount = 2000,
                         currency = CurrencyValues.RUB
                 )
         }
    )
    @Test
    public void categoryDescriptionShouldBeEditedByTableAction(SpendJson spend) {
        final String newSpendingDescription = RandomDataUtils.randomCategoryName();
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doLogin("ivan", "12345")
                .editSpending(spend.description())
                .setDescription(newSpendingDescription);

        new MainPage().checkThatTableContains(newSpendingDescription);
    }
}
