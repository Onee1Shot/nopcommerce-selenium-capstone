package pages;

import org.openqa.selenium.By;

public class RegistrationPage extends BasePage {

    private final By genderMale = By.id("gender-male");
    private final By genderFemale = By.id("gender-female");
    private final By firstName = By.id("FirstName");
    private final By lastName = By.id("LastName");
    private final By email = By.id("Email");
    private final By company = By.id("Company");
    private final By newsletter = By.id("Newsletter");
    private final By password = By.id("Password");
    private final By confirmPassword = By.id("ConfirmPassword");
    private final By registerBtn = By.id("register-button");

    // Success text on nopCommerce after successful registration
    private final By resultMsg = By.cssSelector(".result, .page-body .result, .registration-result");


    public RegistrationPage() {}
    public RegistrationPage(org.openqa.selenium.WebDriver ignored) {}

    public void selectGender(String g) {
        if ("female".equalsIgnoreCase(g)) {
            BasePage.getDriver().findElement(genderFemale).click();
        } else {
            BasePage.getDriver().findElement(genderMale).click();
        }
    }

    public void enterFirstName(String v) { BasePage.getDriver().findElement(firstName).sendKeys(v); }
    public void enterLastName(String v)  { BasePage.getDriver().findElement(lastName).sendKeys(v); }
    public void enterEmail(String v)     { BasePage.getDriver().findElement(email).sendKeys(v); }
    public void enterCompany(String v)   { BasePage.getDriver().findElement(company).sendKeys(v); }

    public void setNewsletter(String yesNo) {
        boolean want = "yes".equalsIgnoreCase(yesNo);
        var el = BasePage.getDriver().findElement(newsletter);
        if (el.isSelected() != want) el.click();
    }

    public void enterPassword(String v)        { BasePage.getDriver().findElement(password).sendKeys(v); }
    public void enterConfirmPassword(String v) { BasePage.getDriver().findElement(confirmPassword).sendKeys(v); }
    public void clickRegister()                { BasePage.getDriver().findElement(registerBtn).click(); }

    public String getResultMessage() {
        try {
            return BasePage.getDriver().findElement(resultMsg).getText().trim();
        } catch (Exception e) {
            return null;
        }
    }
}
