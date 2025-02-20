package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import java.time.Duration;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

  private final SelenideElement img = $("canvas[role='img']");

  public StatComponent() {
    super($("#stat"));
  }

  private final ElementsCollection bubbles = self.$("#legend-container").$$("li");

  @Nonnull
  public SelenideElement pieChartImage() {
    return img;
  }

  @Nonnull
  @Step("Check that legend contains data '{0}'")
  public StatComponent checkBubblesHasText(String description) {
    bubbles.find(text(description))
            .should(visible);
    return this;
  }

  @Nonnull
  @Step("Wait for pie chart to load")
  public StatComponent waitForPieChartToLoad() {
    img.is(image, Duration.ofSeconds(5));
    return this;
  }

}
