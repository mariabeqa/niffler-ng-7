package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {

  private static final Config CFG = Config.getInstance();
  private static final String USER_PW = "12345";

  @User(
      username = "duck",
      categories = @Category(
          archived = true
      )
  )
  @Test
  void archivedCategoryShouldPresentInCategoriesList(CategoryJson[] category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin("duck", "12345")
        .checkThatPageLoaded();

    Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
        .checkArchivedCategoryExists(category[0].name());
  }

  @User(
      username = "duck",
      categories = @Category(
          archived = false
      )
  )
  @Test
  void activeCategoryShouldPresentInCategoriesList(CategoryJson category) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .successLogin("duck", "12345")
        .checkThatPageLoaded();

    Selenide.open(CFG.frontUrl() + "profile", ProfilePage.class)
        .checkCategoryExists(category.name());
  }

  @User
  @Test
  void userInfoShouldBeSavedAfterEditing(UserJson user) {
    final String name =RandomDataUtils.randomName();
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), USER_PW)
            .checkThatPageLoaded()
            .header()
            .toProfilePage()
            .setName(name)
            .checkName(name);
  }

  @User(
     categories = {
         @Category(
                 name = "Food",
                 archived = false
         )
     }
  )
  @Test
  void userCategoriesShouldBeSavedAfterEditing(UserJson user) {
    final String categoryName = user.testData().categories().get(0).name();
    final String newName = RandomDataUtils.randomCategoryName();
    Selenide.open(CFG.frontUrl(), LoginPage.class)
            .successLogin(user.username(), USER_PW)
            .checkThatPageLoaded()
            .header()
            .toProfilePage()
            .checkCategoryExists(categoryName)
            .editCategoryName(categoryName, newName)
            .checkCategoryExists(newName);
  }
}
