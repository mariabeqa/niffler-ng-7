package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class MainPage {

  public static String URL = Config.getInstance().frontUrl() + "main";

  private final SelenideElement statComponent = $("#stat");
  private final SelenideElement tableName = $("div#spendings h2");

  private final Header header = new Header();
  private final SpendingTable spendings = new SpendingTable();

  public Header header() {
    return header;
  }

  public SpendingTable spendings() {
    return spendings;
  }

  @Step("Проверяем, что главная страница прогрузилась")
  public MainPage checkThatPageLoaded() {
    statComponent.should(visible).shouldHave(text("Statistics"));
    tableName.should(visible).shouldHave(text("History of Spendings"));
    return this;
  }
}
