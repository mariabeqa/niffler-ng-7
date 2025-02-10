package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement saveBtn = $("#save");

  private final Calendar calendar = new Calendar($(".SpendingCalendar"));

  @Nonnull
  public EditSpendingPage setNewSpendingDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    return this;
  }

  @Nonnull
  @Step("Заполняем имя категории {categoryName}")
  public EditSpendingPage setNewCategoryName(String categoryName) {
    categoryInput.clear();
    categoryInput.setValue(categoryName);
    return this;
  }

  @Nonnull
  @Step("Заполняем сумму {amount}")
  public EditSpendingPage setNewAmount(String amount) {
    amountInput.clear();
    amountInput.setValue(amount);
    return this;
  }

  public void save() {
    saveBtn.click();
  }
}
