package guru.qa.niffler.page.component;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header {

    private final SelenideElement self = $("#root header");
    private final SelenideElement menuBtn = self.$("button");
    private final SelenideElement headerMenu = $("ul[role='menu']");

    @Step("Открываем страницу Friends")
    public FriendsPage toFriendsPage() {
        menuBtn.click();
        headerMenu.$$("li").find(text("Friends")).click();
        return new FriendsPage();
    }

    @Step("Открываем страницу All People")
    public PeoplePage toAllPeoplesPage() {
        menuBtn.click();
        headerMenu.$$("li").find(text("All People")).click();
        return new PeoplePage();
    }

    @Step("Открываем страницу Profile")
    public ProfilePage toProfilePage() {
        menuBtn.click();
        headerMenu.$$("li").find(text("Profile")).click();
        return new ProfilePage();
    }

    @Step("Делаем Log out")
    public LoginPage signOut() {
        menuBtn.click();
        headerMenu.$$("li").find(text("Sign out")).click();
        return new LoginPage();
    }

    @Step("Добавляем новый Spending")
    public EditSpendingPage addSpendingPage() {
        self.$("a[href='/spending']").click();
        return new EditSpendingPage();
    }

    @Step("Открываем главную страницу")
    public MainPage toMainPage() {
        self.$("a[href='/main']").click();
        return new MainPage();
    }
}
