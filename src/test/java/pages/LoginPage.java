package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    private final By email = By.id("Email");
    private final By password = By.id("Password");
    private final By loginBtn = By.xpath("//button[@class='button-1 login-button']");

    private final By logoutLink = By.className("ico-logout");
    private final By myAccountLink = By.className("ico-account");

    private final By errorBox = By.cssSelector(".message-error, .validation-summary-errors");

    // Actions
    public void enterEmail(String emailId) {
        BasePage.getDriver().findElement(email).sendKeys(emailId);
    }
    public void enterPassword(String pass) {
        BasePage.getDriver().findElement(password).sendKeys(pass);
    }
    public void clickLogin() {
        BasePage.getDriver().findElement(loginBtn).click();
    }

    public void clearEmail() {
        WebElement e = BasePage.getDriver().findElement(email);
        e.clear();
    }
    public void clearPassword() {
        WebElement p = BasePage.getDriver().findElement(password);
        p.clear();
    }

    public boolean isLoggedIn() {
        return isPresent(logoutLink) || isPresent(myAccountLink);
    }

    public String getLoginError() {
        try {
            return BasePage.getDriver().findElement(errorBox).getText().trim();
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isPresent(By locator) {
        try {
            return BasePage.getDriver().findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
