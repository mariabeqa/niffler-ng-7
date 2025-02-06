package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

public class SpendApiTest {

    private static final Config CFG = Config.getInstance();
    public static final String USER_PW = "12345";
    static SpendClient spendApiClient = new SpendApiClient();

    @Test
    void createSpendTest() {
        String categoryName = RandomDataUtils.randomCategoryName();
        String username = "snake";

        SpendJson createdSpend = spendApiClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                categoryName,
                                username,
                                false
                        ),
                        CurrencyValues.RUB,
                        1000.0,
                        "Spend creation test",
                        username
                )
        );

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(username, USER_PW)
                .spendings()
                .checkSpendingWith(
                        createdSpend.category().name(),
                        createdSpend.description()
                );

    }

    @Test
    void createCategoryTest() {
        String categoryName = RandomDataUtils.randomCategoryName();
        String username = "snake";

        CategoryJson createdCategory = spendApiClient.createCategory(
                new CategoryJson(
                        null,
                        categoryName,
                        username,
                        false
                )
        );

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .successLogin(username, USER_PW)
                .checkThatPageLoaded();

        Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
                .checkCategoryExists(createdCategory.name());
    }
}
