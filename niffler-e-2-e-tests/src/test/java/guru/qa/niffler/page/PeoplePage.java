package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.page.component.PeopleTable;

import static com.codeborne.selenide.Selenide.$;

public class PeoplePage {

  public static final String URL = Config.getInstance().frontUrl() + "people/all";

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");

  private final PeopleTable peopleTable = new PeopleTable();

  public PeopleTable peopleTable() {
    return peopleTable;
  }

}
