package ru.netology.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;
import ru.netology.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;

class Tests {

    @Test
    void shouldTransferMoneyBetweenCard() {
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var cardNumber1 = DataHelper.getCardNumber1();
        var cardNumber2 = DataHelper.getCardNumber2();
        var balanceCard1 = dashboardPage.getCardBalance(cardNumber1.getIndex());
        var balanceCard2 = dashboardPage.getCardBalance(cardNumber2.getIndex());
        var transferAmount = DataHelper.generateValidAmount(balanceCard2);
        var expBalanceCard1 = balanceCard1 + transferAmount;
        var expBalanceCard2 = balanceCard2 - transferAmount;
        var topUpProcess = dashboardPage.transferTo(cardNumber1.getIndex());
        dashboardPage = topUpProcess.validTransfer(String.valueOf(transferAmount), cardNumber2);
        var actBalanceCard1 = dashboardPage.getCardBalance(cardNumber1.getIndex());
        var actBalanceCard2 = dashboardPage.getCardBalance(cardNumber2.getIndex());
        Assertions.assertEquals(expBalanceCard1, actBalanceCard1);
        Assertions.assertEquals(expBalanceCard2, actBalanceCard2);
    }

    @Test
    void shouldBeErrorMessageIfSendInvalidAmount(){
        var loginPage = open("http://localhost:9999", LoginPage.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        var dashboardPage = verificationPage.validVerify(verificationCode);
        var cardNumber1 = DataHelper.getCardNumber1();
        var cardNumber2 = DataHelper.getCardNumber2();
        var balanceCard1 = dashboardPage.getCardBalance(cardNumber1.getIndex());
        var balanceCard2 = dashboardPage.getCardBalance(cardNumber2.getIndex());
        var transferAmount = DataHelper.generateInvalidAmount(balanceCard1);
        var topUpProcess = dashboardPage.transferTo(cardNumber2.getIndex());
        topUpProcess.transferAmount(String.valueOf(transferAmount), cardNumber1);
        topUpProcess.findErrorMessage("Ошибка! На Вашем счёте недостаточно средств для перевода!");
        Assertions.assertEquals(dashboardPage.getCardBalance(cardNumber1.getIndex()), balanceCard1);
        Assertions.assertEquals(dashboardPage.getCardBalance(cardNumber2.getIndex()), balanceCard2);
    }
}