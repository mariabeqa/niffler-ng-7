package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.DataFilterValues;
import guru.qa.niffler.page.EditSpendingPage;
import io.qameta.allure.Step;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpendingTable {

    private final SelenideElement self = $("#spendings tbody");
    private final ElementsCollection tableRows = self.$$("tr");
    private final SelenideElement periodInpt = $("#period");
    private final ElementsCollection dropdownList = $$("ul[role='listbox']");
    private final SelenideElement deleteBtn = $("#delete");
    private final SearchField searchField = new SearchField();

    @Step("Выбираем период '{period}' в таблице трат")
    public SpendingTable selectPeriod(DataFilterValues period) {
        periodInpt.click();
        dropdownList.find(text(period.name())).click();
        return this;
    }

    @Step("Редактируем трату с описанием '{description}'")
    public EditSpendingPage editSpending(String description) {
        searchSpendingByDescription(description);
        tableRows.find(text(description)).$$("td").get(0).click();
        return new EditSpendingPage();
    }

    @Step("Удаляем трату с описанием '{description}'")
    public SpendingTable deleteSpending(String description) {
        searchSpendingByDescription(description);
        tableRows.find(text(description)).$$("td").get(5).click();
        deleteBtn.click();
        return this;
    }

    @Step("Редактируем трату с описанием '{description}'")
    public SpendingTable searchSpendingByDescription(String description) {
        searchField.search(description);
        return this;
    }

    @Step("Проверяем наличие траты с категорией '{categoryName}' и с описанием - '{description}'")
    public SpendingTable checkSpendingWith(String categoryName, String description) {
        searchField.search(description);
        tableRows.find(text(description)).$$("td").get(1).shouldHave(text(categoryName));
        return this;
    }

    @Step("Проверяем наличие траты с описанием - '{spendingDescription}'")
    public SpendingTable checkTableContainsSpending(String spendingDescription) {
        searchSpendingByDescription(spendingDescription);
        tableRows.find(text(spendingDescription)).shouldBe(visible);
        return this;
    }

    @Step("Проверяем наличие трат по описанию - '{expectedSpends}'")
    public SpendingTable checkTableContains(String... expectedSpends) {
        Set<String> actualSpends = tableRows
                .stream()
                .map(SelenideElement::getText).collect(Collectors.toSet());
        assertTrue(
                Arrays.stream(expectedSpends).collect(Collectors.toSet())
                .containsAll(actualSpends)
        );
        return this;
    }

    @Step("Проверяем количество трат в таблице - {expectedSize}")
    public SpendingTable checkTableSize(int expectedSize) {
        tableRows.should(size(expectedSize));
        return this;
    }
}
