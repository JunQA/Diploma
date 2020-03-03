package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.javafaker.Faker;
import data.Card;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.sql.SQLException;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static data.DataGenerator.*;
import static page.PaymentPage.getFilledPaymentPage;
import static page.CreditPage.getFilledCreditPage;

public class PurchaseTests {

    private Card cardOne = new Card();
    private Card cardTwo = new Card();
    private Card invalidNumberCard = new Card();
    private Faker faker = new Faker(new Locale("en"));

    @BeforeEach
    void setUp() {
        setCards();
    }

    @AfterEach
    void cleanTables() throws SQLException {
        SQLHelper.cleanTables();
    }

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @Test
    @DisplayName("Должен подтверждать покупку при валидных данных и карте со статусом APPROVED")
    void shouldConfirmPaymentWithValidDataCardOne() throws SQLException {
        getFilledPaymentPage(cardOne).assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), "APPROVED");
        assertNotNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Должен подтверждать кредит при валидных данных и карте со статусом APPROVED")
    void shouldConfirmCreditWithValidDataCardOne() throws SQLException {
        getFilledCreditPage(cardOne).assertNotificationOkIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), "APPROVED");
        assertNotNull(SQLHelper.findCreditId());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку при использовании карты со статусом DECLINED")
    void shouldNotConfirmPaymentWithInvalidCardTwo() throws SQLException{
        getFilledPaymentPage(cardTwo).assertNotificationErrorIsVisible();
        assertEquals(SQLHelper.findPaymentStatus(), "DECLINED");
        assertNull(SQLHelper.findPaymentId());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит при использовании карты со статусом DECLINED")
    void shouldNotConfirmCreditWithInvalidCardTwo() throws SQLException {
        getFilledCreditPage(cardTwo).notificationErrorIsVisible();
        assertEquals(SQLHelper.findCreditStatus(), "DECLINED");
        assertNull(SQLHelper.findCreditId());
    }

    // Негативные сценарии с номером карты при оплате:

    @Test
    @DisplayName("Не должен подтверждать покупку при невалидном номере карты")
    void shouldNotSubmitPaymentWithIllegalCard() throws SQLException {
        cardOne.setNumber("4444 4444 4444 4444");
        getFilledPaymentPage(cardOne).assertNotificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
}

    // Негативные сценарии с номером карты при кредите:

    @Test
    @DisplayName("Не должен подтверждать кредит при невалидном номере карты")
    void shouldNotSubmitCreditWithIllegalCard() throws SQLException{
        cardOne.setNumber("4444 4444 4444 4444");
        getFilledCreditPage(cardOne).notificationErrorIsVisible();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Негативные сценарии с датой при оплате:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitPaymentWithWrongMonth(String month, String message) throws SQLException {
        cardOne.setMonth(month);
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку, если введен несуществующий месяц")
    void shouldNotConfirmPaymentWithInvalidMonth() throws SQLException {
        cardOne.setMonth("22");
        getFilledPaymentPage(cardOne).assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку без указания года")
    void shouldNotConfirmPaymentIfEmptyYear() throws SQLException {
        cardOne.setYear("");
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку, если год предшествует текущему")
    void shouldNotConfirmPaymentWithOldYear() throws SQLException {
        cardOne.setYear(getWrongYear());
        getFilledPaymentPage(cardOne).assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Негативные сценарии с датой при кредите:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitCreditWithWrongMonth(String month, String message) throws SQLException{
        cardOne.setMonth(month);
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит, если введен несуществующий месяц")
    void shouldNotConfirmCreditWithInvalidMonth() throws SQLException{
        cardOne.setMonth("22");
        getFilledCreditPage(cardOne).assertInputInvalidMonth();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит без указания года")
    void shouldNotConfirmCreditIfEmptyYear() throws SQLException{
        cardOne.setYear("");
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит, если год предшествует текущему")
    void shouldNotConfirmCreditWithOldYear() throws SQLException{
        cardOne.setYear(getWrongYear());
        getFilledCreditPage(cardOne).assertInputInvalidExpireDate();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Негативные сценарии с полем владелец при покупке:

    @Test
    @DisplayName("Не должен подтверждать покупку без имени владельца")
    void shouldNotConfirmPaymentWithoutOwner() throws SQLException{
        cardOne.setOwner("");
        getFilledPaymentPage(cardOne).assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidOwner(String owner, String message) throws SQLException {
        cardOne.setOwner(owner);
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Негативные сценарии с полем владелец при кредите:

    @Test
    @DisplayName("Не должен подтверждать кредит без имени владельца")
    void shouldNotConfirmCreditWithoutOwner() throws SQLException{
        cardOne.setOwner("");
        getFilledCreditPage(cardOne).assertInputInvalidFillData();
        assertFalse(SQLHelper.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidOwner(String owner, String message) throws SQLException{
        cardOne.setOwner(owner);
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Негативные сценарии с полем cvc/cvv при оплате:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidCvc(String cvc, String message) throws SQLException{
        cardOne.setCvc(cvc);
        getFilledPaymentPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Негативные сценарии с полем cvc/cvv при кредите:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidCvc(String cvc, String message) throws SQLException {
        cardOne.setCvc(cvc);
        getFilledCreditPage(cardOne).assertInputInvalidFormat();
        assertFalse(SQLHelper.isNotEmpty());
    }

    // Дополнительные методы

    private void setCards() {
        cardOne.setNumber("4444 4444 4444 4441");
        cardTwo.setNumber("4444 4444 4444 4442");
        invalidNumberCard.setNumber("4444 4444 4444 4444");
        cardOne.setMonth("01");
        cardTwo.setMonth("01");
        cardOne.setYear(getCorrectYear());
        cardTwo.setYear(getCorrectYear());
        cardOne.setOwner(setFakeOwner());
        cardTwo.setOwner(setFakeOwner());
        cardOne.setCvc(getRandomCvc());
        cardTwo.setCvc(getRandomCvc());
    }

   private String setFakeOwner() {
        String owner = faker.name().fullName();
        return owner;
   }
}