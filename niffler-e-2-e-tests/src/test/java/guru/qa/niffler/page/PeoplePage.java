package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class PeoplePage {

  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement peopleTable = $("#all");
  private final SelenideElement searchInput = $("input[placeholder='Search']");
  private final ElementsCollection sentInvites = $$(By.xpath("//tr[.//span [text() = 'Waiting...']]"));

  public PeoplePage checkInvitationSentToUser(String username) {
    SelenideElement friendRow = peopleTable.$$("tr").find(text(username));
    friendRow.shouldHave(text("Waiting..."));
    return this;
  }

  public PeoplePage checkUserHasOneOutcomeInvitation() {
    sentInvites.shouldHave(size(1));
    return this;
  }

  public PeoplePage sendInvitationToPersonWithName(String username) {
    SelenideElement personRow = peopleTable.$$(By.xpath("tr[.//button]")).find(text(username));
    if (personRow.isDisplayed()) {
      personRow.find("button").click();
    } else {
      searchInput.click();
      searchInput.sendKeys(username);
      searchInput.submit();
      personRow.find("button").click();
    }

    return this;
  }
}
