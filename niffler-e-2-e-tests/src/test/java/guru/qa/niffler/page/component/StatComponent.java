package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.Color;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.color;

@ParametersAreNonnullByDefault
public class StatComponent extends BaseComponent<StatComponent> {

  public StatComponent() {
    super($("#stat"));
  }

  private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
  private final SelenideElement chart =  $("canvas[role='img']");

  @Nonnull
  public BufferedImage chartScreenshot() throws IOException {
    return ImageIO.read(Objects.requireNonNull(chart.screenshot()));
  }

  public StatComponent checkBubbles(Color... expectedColors) {
    bubbles.should(color(expectedColors));
    return this;
  }
}
