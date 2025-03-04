package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.extension.ScreenShotTestExtension;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.attributeMatching;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement avatar;
  private final SelenideElement userName;
  private final SelenideElement nameInput;
  private final SelenideElement photoInput;
  private final SelenideElement submitButton;
  private final SelenideElement categoryInput;
  private final SelenideElement archivedSwitcher;

  private final ElementsCollection bubbles;
  private final ElementsCollection bubblesArchived;

  public ProfilePage(SelenideDriver driver) {
    super(driver);
    this.avatar = driver.$("#image__input").parent().$("img");
    this.userName = driver.$("#username");
    this.nameInput = driver.$("#name");
    this.photoInput = driver.$("input[type='file']");
    this.submitButton = driver.$("button[type='submit']");
    this.categoryInput = driver.$("input[name='category']");
    this.archivedSwitcher = driver.$(".MuiSwitch-input");

    this.bubbles = driver.$$(".MuiChip-filled.MuiChip-colorPrimary");
    this.bubblesArchived = driver.$$(".MuiChip-filled.MuiChip-colorDefault");
  }

  @Step("Set name: '{0}'")
  @Nonnull
  public ProfilePage setName(String name) {
    nameInput.clear();
    nameInput.setValue(name);
    return this;
  }

  @Step("Upload photo from classpath")
  @Nonnull
  public ProfilePage uploadPhotoFromClasspath(String path) {
    photoInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Set category: '{0}'")
  @Nonnull
  public ProfilePage addCategory(String category) {
    categoryInput.setValue(category).pressEnter();
    return this;
  }

  @Step("Check category: '{0}'")
  @Nonnull
  public ProfilePage checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
    return this;
  }

  @Step("Check archived category: '{0}'")
  @Nonnull
  public ProfilePage checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
    return this;
  }

  @Step("Check userName: '{0}'")
  @Nonnull
  public ProfilePage checkUsername(String username) {
    this.userName.should(value(username));
    return this;
  }

  @Step("Check name: '{0}'")
  @Nonnull
  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }

  @Step("Check photo")
  @Nonnull
  public ProfilePage checkPhoto(BufferedImage expected) throws IOException {
    Selenide.sleep(1000);
    BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(avatar.screenshot()));
    assertFalse(
        new ScreenDiffResult(
            actualImage, expected
        ),
        ScreenShotTestExtension.ASSERT_SCREEN_MESSAGE
    );
    return this;
  }

  @Step("Check photo exist")
  @Nonnull
  public ProfilePage checkPhotoExist() {
    avatar.should(attributeMatching("src", "data:image.*"));
    return this;
  }

  @Step("Check that category input is disabled")
  @Nonnull
  public ProfilePage checkThatCategoryInputDisabled() {
    categoryInput.should(disabled);
    return this;
  }

  @Step("Save profile")
  @Nonnull
  public ProfilePage submitProfile() {
    submitButton.click();
    return this;
  }

  @Override
  @Step("Check that page is loaded")
  @Nonnull
  public ProfilePage checkThatPageLoaded() {
    userName.should(visible);
    return this;
  }
}
