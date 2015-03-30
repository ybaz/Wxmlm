package FunctionalTests.Pages;

import UsedByAll.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Set;

/**
 * Created by User on 3/24/2015.
 */
public class SwiftPage extends RechargePage {
    public SwiftPage(WebDriver driver, WebDriverWait wait){super(driver, wait);}
    public static final By summ = By.xpath("//div[@id='Swift']//input[@name='amount']");
    public static final By createInvoice = By.xpath("//div[@id='Swift']//button[text()='Create invoice']");
    public static final By invoiceFullName = By.xpath("//body/div[2]//div[@class='row']/div/div/div[3]//h4/strong");
    public static final By invoiceInviteCode = By.xpath("//body/div[2]//div[@class='row']/div/div/div[3]//h4/em");
    public static final By paymentPurpose = By.xpath("//table[@class='payment-details']/tbody/tr[1]/td[2]");
    public static final By adminName = By.xpath("//table[@class='payment-details']/tbody/tr[2]/td[2]");
    public static final By adminAddress = By.xpath("//table[@class='payment-details']/tbody/tr[3]/td[2]");
    public static final By adminBank = By.xpath("//table[@class='payment-details']/tbody/tr[4]/td[2]");
    public static final By adminBankAddress = By.xpath("//table[@class='payment-details']/tbody/tr[5]/td[2]");
    public static final By adminIban = By.xpath("//table[@class='payment-details']/tbody/tr[6]/td[2]");
    public static final By adminSwift = By.xpath("//table[@class='payment-details']/tbody/tr[7]/td[2]");
    public static final By amountInvoice = By.className("amount");


    public void enterAmount(String value){
        wait.until(ExpectedConditions.visibilityOfElementLocated(summ));
        driver.findElement(summ).clear();
        driver.findElement(summ).click();
        driver.findElement(summ).sendKeys(value);
    }
    public void createInvoice(){

        String originalWindow = driver.getWindowHandle();
        final Set<String> oldWindowsSet = driver.getWindowHandles(); //набор текущих открытых окон
        driver.findElement(createInvoice).click();

        String newWindow = wait.until(new ExpectedCondition<String>() {
        public String apply(WebDriver driver){
                Set<String> newWindowsSet = driver.getWindowHandles();
                newWindowsSet.removeAll(oldWindowsSet);
                return newWindowsSet.size()>0 ? newWindowsSet.iterator().next() : null;
            }
        }
        );
        driver.switchTo().window(newWindow);

    }
    public String getPaymentPurpose(){
        return(driver.findElement(paymentPurpose).getText());
    }
    public String getAdminName(){
        return(driver.findElement(adminName).getText());
    }
    public String getAdminAddress(){
        return(driver.findElement(adminAddress).getText());
    }
    public String getAdminBank(){
        return(driver.findElement(adminBank).getText());

    }
    public String getAdminBankAddress(){
        return(driver.findElement(adminBankAddress).getText());
    }
    public String getAdminIban(){
        return(driver.findElement(adminIban).getText());
    }
    public String getAdminSwift(){
        return(driver.findElement(adminSwift).getText());
    }
    public String getAmountInvoice(){
        return(driver.findElement(amountInvoice).getText());
    }
    public String getInvoiceInviteCode(){
        return(driver.findElement(invoiceInviteCode).getText().split(": ")[1]);
    }
    public String getInvoiceFullName(){
        return(driver.findElement(invoiceFullName).getText().split(", ")[0]);
    }
    public String getInvoiceEmail(){
        return(driver.findElement(invoiceFullName).getText().split(", ")[1]);
    }

}