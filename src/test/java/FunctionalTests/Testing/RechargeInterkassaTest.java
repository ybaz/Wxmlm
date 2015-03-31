package FunctionalTests.Testing;

import ApiTests.Backend.GetConfigToRun;
import ApiTests.ObjectClasses.AConfig;
import FunctionalTests.Pages.*;
import UsedByAll.Config;
import UsedByAll.CsvUsersReader;
import UsedByAll.MakeRandomValue;
import UsedByAll.TestUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Collection;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Created by User on 3/31/2015.
 */
@RunWith(value = Parameterized.class)
public class RechargeInterkassaTest extends BaseTest {
    String email;
    String password;
    TestUser testUser;

    @Parameterized.Parameters
    public static Collection testData(){return CsvUsersReader.getDataForTest("_RechargeInterkassaTest(");}

    public RechargeInterkassaTest(TestUser testUser){
        this.email=testUser.getEmail();
        this.password=testUser.getPassword1();
        this.testUser=testUser;
    }
    @Test
    public void interkassaTest() throws IOException {
        String siteUrl = Config.getConfig().getProtocol() + Config.getConfig().getScheme();
        LogInPage loginPage = new LogInPage(driver, wait);
        AuthorizedUserPage userPage = new AuthorizedUserPage(driver, wait);
        MoneyFamilyPage moneyPage = new MoneyFamilyPage(driver, wait);
        RechargePage rechargePage = new RechargePage(driver, wait);
        InterkassaPage interkassaPage = new InterkassaPage(driver, wait);
        ProfilePage profilePage = new ProfilePage(driver, wait);

        loginPage.open();
        loginPage.goLogin(email, password);
        assertEquals(loginPage.getTitle(), "KairosNet");
        userPage.goMoney();
        moneyPage.goToRecharge();
        rechargePage.goInterkassa();
        interkassaPage.waitForPageLoading();
        profilePage.goProfilePage();

        String inviteCode = profilePage.getInviteCode();
        profilePage.closeProfilePage();

        assertTrue("Incorrect image", interkassaPage.getImageLink().contains("interkassa")); //проверка линка картинки
        MakeRandomValue getRandomValue = new MakeRandomValue(); // сгенерировать сумму
        String sum = getRandomValue.makeRandomValue();
        interkassaPage.enterAmount(sum);

        //расчитать сумму с комиссией, вытянув процент из конфига
        AConfig interkassaFeeConfig = new GetConfigToRun(testUser).getConfigByParameter("id", 8, Config.getConfig().getAdmin(), siteUrl);
        Float sumWithoutFee = Float.parseFloat(sum);
        Float sumWithFee = sumWithoutFee + (sumWithoutFee*100*Float.parseFloat(interkassaFeeConfig.getValue())/10000);
        sumWithFee = sumWithFee*100;
        int iSum = (int)Math.round(sumWithFee);
        sumWithFee = (float)iSum/100;

        assertEquals("Incorrect sum with fee", sumWithFee.toString(), interkassaPage.getSumWithFee());

        interkassaPage.createPayment();
        interkassaPage.waitForPaymentFormLoading();
        assertEquals("Incorrect payment sum", sumWithFee.toString(), interkassaPage.getPaymentSumm());
        assertEquals("Incorrect email in payment", interkassaPage.getEmailFromPaymentForm(), email);
        assertEquals("Incorrect invite code in payment", interkassaPage.getInviteCodeFromPaymentForm(), inviteCode);







    }

}
