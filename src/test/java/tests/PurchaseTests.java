package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.github.javafaker.Faker;
import data.Card;
import data.GetData;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import page.CreditPage;
import page.PaymentPage;
import page.StartPage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.*;

public class PurchaseTests {

    private Card cardOne = new Card();
    private Card cardTwo = new Card();
    private Card invalidNumberCard = new Card();
    Faker faker = new Faker(new Locale("en"));

    @BeforeEach
    void setUp() {
        setCards();
    }

    @AfterEach
    void cleanTables() throws SQLException {
        GetData.cleanTables();
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
        assertTrue(openAndFillPaymentPage(cardOne).notificationOkIsVisible());
        assertEquals(GetData.findPaymentStatus(), "APPROVED");
        assertNotNull(GetData.findPaymentId());
    }

    @Test
    @DisplayName("Должен подтверждать кредит при валидных данных и карте со статусом APPROVED")
    void shouldConfirmCreditWithValidDataCardOne() throws SQLException {
        assertTrue(openAndFillCreditPage(cardOne).notificationOkIsVisible());
        assertEquals(GetData.findCreditStatus(), "APPROVED");
        assertNotNull(GetData.findCreditId());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку при использовании карты со статусом DECLINED")
    void shouldNotConfirmPaymentWithInvalidCardTwo() throws SQLException{
        assertTrue(openAndFillPaymentPage(cardTwo).notificationErrorIsVisible());
        assertEquals(GetData.findPaymentStatus(), "DECLINED");
        assertNull(GetData.findPaymentId());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит при использовании карты со статусом DECLINED")
    void shouldNotConfirmCreditWithInvalidCardTwo() throws SQLException {
        assertTrue(openAndFillCreditPage(cardTwo).notificationErrorIsVisible());
        assertEquals(GetData.findCreditStatus(), "DECLINED");
        assertNull(GetData.findCreditId());
    }

    // Негативные сценарии с номером карты при оплате:

    @Test
    @DisplayName("Не должен подтверждать покупку при невалидном номере карты")
    void shouldNotSubmitPaymentWithIllegalCard() throws SQLException {
        cardOne.setNumber("4444 4444 4444 4444");
        assertTrue(openAndFillPaymentPage(cardOne).notificationErrorIsVisible());
        assertFalse(GetData.isNotEmpty());
}

    // Негативные сценарии с номером карты при кредите:

    @Test
    @DisplayName("Не должен подтверждать кредит при невалидном номере карты")
    void shouldNotSubmitCreditWithIllegalCard() throws SQLException{
        cardOne.setNumber("4444 4444 4444 4444");
        assertTrue(openAndFillCreditPage(cardOne).notificationErrorIsVisible());
        assertFalse(GetData.isNotEmpty());
    }

    // Негативные сценарии с датой при оплате:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitPaymentWithWrongMonth(String month, String message) throws SQLException {
        cardOne.setMonth(month);
        assertTrue((openAndFillPaymentPage(cardOne).inputInvalidFormat()), message);
        assertFalse(GetData.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку, если введен несуществующий месяц")
    void shouldNotConfirmPaymentWithInvalidMonth() throws SQLException {
        cardOne.setMonth("22");
        assertTrue(openAndFillPaymentPage(cardOne).inputInvalidMonth());
        assertFalse(GetData.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку без указания года")
    void shouldNotConfirmPaymentIfEmptyYear() throws SQLException {
        cardOne.setYear("");
        assertTrue(openAndFillPaymentPage(cardOne).inputInvalidFormat());
        assertFalse(GetData.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать покупку, если год предшествует текущему")
    void shouldNotConfirmPaymentWithOldYear() throws SQLException {
        cardOne.setYear(setWrongYear());
        assertTrue(openAndFillPaymentPage(cardOne).inputInvalidExpireDate());
        assertFalse(GetData.isNotEmpty());
    }

    // Негативные сценарии с датой при кредите:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    void shouldNotSubmitCreditWithWrongMonth(String month, String message) throws SQLException{
        cardOne.setMonth(month);
        assertTrue((openAndFillCreditPage(cardOne).inputInvalidFormat()), message);
        assertFalse(GetData.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит, если введен несуществующий месяц")
    void shouldNotConfirmCreditWithInvalidMonth() throws SQLException{
        cardOne.setMonth("22");
        assertTrue(openAndFillCreditPage(cardOne).inputInvalidMonth());
        assertFalse(GetData.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит без указания года")
    void shouldNotConfirmCreditIfEmptyYear() throws SQLException{
        cardOne.setYear("");
        assertTrue(openAndFillCreditPage(cardOne).inputInvalidFormat());
        assertFalse(GetData.isNotEmpty());
    }

    @Test
    @DisplayName("Не должен подтверждать кредит, если год предшествует текущему")
    void shouldNotConfirmCreditWithOldYear() throws SQLException{
        cardOne.setYear(setWrongYear());
        assertTrue(openAndFillCreditPage(cardOne).inputInvalidExpireDate());
        assertFalse(GetData.isNotEmpty());
    }

    // Негативные сценарии с полем владелец при покупке:

    @Test
    @DisplayName("Не должен подтверждать покупку без имени владельца")
    void shouldNotConfirmPaymentWithoutOwner() throws SQLException{
        cardOne.setOwner("");
        assertTrue(openAndFillPaymentPage(cardOne).inputInvalidFillData());
        assertFalse(GetData.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidOwner(String owner, String message) throws SQLException {
        cardOne.setOwner(owner);
        assertTrue(openAndFillPaymentPage(cardOne).inputInvalidFormat(), message);
        assertFalse(GetData.isNotEmpty());
    }

    // Негативные сценарии с полем владелец при кредите:

    @Test
    @DisplayName("Не должен подтверждать кредит без имени владельца")
    void shouldNotConfirmCreditWithoutOwner() throws SQLException{
        cardOne.setOwner("");
        assertTrue(openAndFillCreditPage(cardOne).inputInvalidFillData());
        assertFalse(GetData.isNotEmpty());
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongOwner.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidOwner(String owner, String message) throws SQLException{
        cardOne.setOwner(owner);
        assertTrue(openAndFillCreditPage(cardOne).inputInvalidFormat(), message);
        assertFalse(GetData.isNotEmpty());
    }

    // Негативные сценарии с полем cvc/cvv при оплате:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmPaymentWithInvalidCvc(String cvc, String message) throws SQLException{
        cardOne.setCvc(cvc);
        assertTrue(openAndFillPaymentPage(cardOne).inputInvalidFormat(), message);
        assertFalse(GetData.isNotEmpty());
    }

    // Негативные сценарии с полем cvc/cvv при кредите:

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCvc.cvs", numLinesToSkip = 1)
    void shouldNotConfirmCreditWithInvalidCvc(String cvc, String message) throws SQLException {
        cardOne.setCvc(cvc);
        assertTrue(openAndFillCreditPage(cardOne).inputInvalidFormat(), message);
        assertFalse(GetData.isNotEmpty());
    }

    // Дополнительные методы

    private StartPage openStartPage() {
        open("http://localhost:8080/");
        val startPage = new StartPage();
        return startPage;
    }

    private PaymentPage openAndFillPaymentPage(Card card) {
        val paymentPage = openStartPage().paymentPage();
        paymentPage.fillData(card);
        return paymentPage;
    }

    private CreditPage openAndFillCreditPage(Card card) {
        val creditPage = openStartPage().creditPage();
        creditPage.fillData(card);
        return creditPage;
    }

    private void setCards() {
        cardOne.setNumber("4444 4444 4444 4441");
        cardTwo.setNumber("4444 4444 4444 4442");
        invalidNumberCard.setNumber("4444 4444 4444 4444");
        cardOne.setMonth("01");
        cardTwo.setMonth("01");
        cardOne.setYear(setCorrectYear());
        cardTwo.setYear(setCorrectYear());
        cardOne.setOwner(setFakeOwner());
        cardTwo.setOwner(setFakeOwner());
        cardOne.setCvc(randomCvc());
        cardTwo.setCvc(randomCvc());
    }

    private String setCorrectYear() {
        LocalDate date = LocalDate.now().plusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        String year = date.format(formatter);
        return year;
    }

    private String setWrongYear() {
        LocalDate date = LocalDate.now().minusYears(2);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy");
        String year = date.format(formatter);
        return year;
    }

   private String setFakeOwner() {
        String owner = faker.name().fullName();
        return owner;
    }

    private String randomCvc() {
        String[] cvcOptions = {"123", "999", "985", "015", "888", "656", "001", "234", "601", "111"};
        int chooseCvc =(int) (Math.random()*cvcOptions.length);
        String cvc = cvcOptions[chooseCvc];
        return cvc;
    }
}
