package cucumber.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.LoginPage;
import pages.RegistrationPage;

public class LoginSteps {

    private WebDriver driver;
    private LoginPage login;
    private RegistrationPage reg;

    @And("I login with email {string} and password {string}")
    public void i_login_with_email_and_password(String email, String password) {
        driver = BasePage.getDriver();
        login = new LoginPage(driver);

        // Try to login first
        login.clearEmail();
        login.enterEmail(email);
        login.clearPassword();
        login.enterPassword(password);
        login.clickLogin();

        // If not logged in, auto-create the account (makes this feature run independently)
        if (!login.isLoggedIn()) {
            String err = login.getLoginError();
            if (err != null && err.toLowerCase().contains("no customer account found")) {
                // Quick registration flow using the same email/password
                BasePage.clickHeaderLink("Register");
                reg = new RegistrationPage(driver);
                reg.selectGender("Male");
                reg.enterFirstName("Auto");
                reg.enterLastName("User");
                reg.enterEmail(email);
                reg.enterCompany("");
                reg.setNewsletter("No");
                reg.enterPassword(password);
                reg.enterConfirmPassword(password);
                reg.clickRegister();

                // Back to login and try again
                BasePage.clickHeaderLink("Log in");
                login.clearEmail();
                login.enterEmail(email);
                login.clearPassword();
                login.enterPassword(password);
                login.clickLogin();
            }
        }
    }

    @Then("I should be logged in successfully")
    public void i_should_be_logged_in_successfully() {
        login = (login == null) ? new LoginPage(BasePage.getDriver()) : login;
        if (!login.isLoggedIn()) {
            String err = login.getLoginError();
            throw new AssertionError("Login did not succeed. Error: " + (err == null ? "<none>" : err));
        }
    }
}
