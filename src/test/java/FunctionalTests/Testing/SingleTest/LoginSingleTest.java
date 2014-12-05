package FunctionalTests.Testing.SingleTest;

import FunctionalTests.Pages.LoginPage;
import FunctionalTests.Testing.LoginTest;
import UsedByAll.TestUser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by User on 12/5/2014.
 */
public class LoginSingleTest extends LoginTest {
    @Test
    public void loginSingleTest(TestUser testUser){
        LoginPage loginPage = new LoginPage(driver);
        //TestUser[] testUser = new CsvUsersReader().getUsersFromFile("src/Users.csv");
        loginPage.open();
        assertTrue("Page not opened", loginPage.isOpened());

        loginPage.goLogin(testUser);
        assertEquals(loginPage.getTitle(), "KairosNet");
        System.out.println("Тест для " + testUser.getEmail() + " успешно пройден");
    }
}
