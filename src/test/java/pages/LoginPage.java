package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage extends BasePage {

    // Locators
    private final By email = By.id("Email");
    private final By password = By.id("Password");
    private final By loginBtn = By.xpath("//button[@class='button-1 login-button']");

    // Header indicators that user is logged in
    private final By logoutLink = By.className("ico-logout");
    private final By myAccountLink = By.className("ico-account");

    // Error containers shown on failed login
    private final By errorBox = By.cssSelector(".message-error, .validation-summary-errors");

    public LoginPage(WebDriver driver) {
        // driver is managed by BasePage (static), but ctor kept for consistency
    }

    // Actions
    public void enterEmail(String emailId) {
        driver.findElement(email).sendKeys(emailId);
    }
    public void enterPassword(String pass) {
        driver.findElement(password).sendKeys(pass);
    }
    public void clickLogin() {
        driver.findElement(loginBtn).click();
    }

    public void clearEmail() {
        WebElement e = driver.findElement(email);
        e.clear();
    }
    public void clearPassword() {
        WebElement p = driver.findElement(password);
        p.clear();
    }

    // State checks
    public boolean isLoggedIn() {
        return isPresent(logoutLink) || isPresent(myAccountLink);
    }

    public String getLoginError() {
        try {
            return driver.findElement(errorBox).getText().trim();
        } catch (Exception e) {
            return null;
        }
    }

    // Utility
    private boolean isPresent(By locator) {
        try {
            return driver.findElements(locator).size() > 0;
        } catch (Exception e) {
            return false;
        }
    }
}
