package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class RegistrationPage extends BasePage {
    private WebDriver drv;

    public RegistrationPage(WebDriver driver) { this.drv = driver; }

//    public void clickHeaderLink(String text) { drv.findElement(By.linkText(text)).click(); }

    public void selectGender(String gender) {
        if (gender != null && gender.equalsIgnoreCase("male")) {
            drv.findElement(By.id("gender-male")).click();
        } else {
            drv.findElement(By.id("gender-female")).click();
        }
    }

    public void enterFirstName(String s)      { drv.findElement(By.id("FirstName")).sendKeys(s); }
    public void enterLastName(String s)       { drv.findElement(By.id("LastName")).sendKeys(s); }
    public void enterEmail(String s)          { drv.findElement(By.id("Email")).sendKeys(s); }
    public void enterCompany(String s)        { drv.findElement(By.id("Company")).sendKeys(s); }

    public void setNewsletter(String yesNo) {
        boolean on = yesNo != null && yesNo.trim().equalsIgnoreCase("Yes");
        WebElement cb = drv.findElement(By.id("Newsletter"));
        if (cb.isSelected() != on) cb.click();
    }

    public void enterPassword(String s)       { drv.findElement(By.id("Password")).sendKeys(s); }
    public void enterConfirmPassword(String s){ drv.findElement(By.id("ConfirmPassword")).sendKeys(s); }
    public void clickRegister()               { drv.findElement(By.id("register-button")).click(); }

    public String getResultMessage() {
        try { return drv.findElement(By.cssSelector("div.result")).getText(); }
        catch (Exception e) { return ""; }
    }
}
