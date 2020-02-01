package page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import data.Card;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selectors.*;
import static com.codeborne.selenide.Selenide.$;

public class PaymentPage {
    private SelenideElement header = $(By.cssSelector("h3"));
    private SelenideElement cardNumberField = $(byText("Номер карты")).parent().$(byCssSelector(".input__control"));
    private SelenideElement monthField = $(byText("Месяц")).parent().$(byCssSelector(".input__control"));
    private SelenideElement yearField = $(byText("Год")).parent().$(byCssSelector(".input__control"));
    private SelenideElement ownerField = $(byText("Владелец")).parent().$(byCssSelector(".input__control"));
    private SelenideElement cvcField = $(byText("CVC/CVV")).parent().$(byCssSelector(".input__control"));
    private SelenideElement continueButton = $(byText("Продолжить")).parent().parent();
    private SelenideElement notificationOK = $(byCssSelector(".notification_status_ok"));
    private SelenideElement notificationError = $(byCssSelector(".notification_status_error"));
    private SelenideElement inputInvalid = $(".input__sub");

    public PaymentPage() {
        header.shouldBe(Condition.visible);
    }

    public void fillData(Card card) {
        cardNumberField.setValue(card.getNumber());
        monthField.setValue(card.getMonth());
        yearField.setValue(card.getYear());
        ownerField.setValue(card.getOwner());
        cvcField.setValue(card.getCvc());
        continueButton.click();
    }

    public boolean notificationOkIsVisible() {
        notificationOK.waitUntil(Condition.visible, 12000);
        return true;
    }

    public boolean checkOkNotification() {
        notificationOK.shouldNotBe(Condition.visible);
        return true;
    }

    public boolean notificationErrorIsVisible() {
        notificationError.waitUntil(Condition.visible, 12000);
        notificationError.$(".icon").click();
        checkOkNotification();
        return true;
    }

    public boolean inputInvalidIsNotVisible() {
        inputInvalid.shouldNotBe(Condition.visible);
        return true;
    }

    public void cleanData() {
        cardNumberField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        monthField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        yearField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        ownerField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        cvcField.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    }

    // Ошибки при вводе невалидных данных:

    public boolean inputInvalidFormat() {
        inputInvalid.shouldHave(Condition.exactText("Неверный формат"));
        return true;
    }

    public boolean inputInvalidMonth() {
        inputInvalid.shouldHave(Condition.exactText("Неверно указан срок действия карты"));
        return true;
    }

    public boolean inputInvalidExpireDate() {
        inputInvalid.shouldHave(Condition.exactText("Истёк срок действия карты"));
        return true;
    }

    public boolean inputInvalidFillData() {
        inputInvalid.shouldHave(Condition.exactText("Поле обязательно для заполнения"));
        return true;
    }
}
