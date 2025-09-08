package cucumber.stepdefs;

import io.cucumber.java.en.*;
import io.cucumber.datatable.DataTable;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.RegistrationPage;

import java.util.Map;
import java.util.UUID;

public class RegistrationSteps {
    private WebDriver driver;
    private RegistrationPage reg;

    @And("I select gender {string}")
    public void i_select_gender(String gender) {
        driver = BasePage.getDriver();
        reg = (reg == null) ? new RegistrationPage(driver) : reg;
        reg.selectGender(gender);
    }

    @And("I fill the registration form with:")
    public void i_fill_the_registration_form_with(DataTable table) {
        driver = BasePage.getDriver();
        reg = (reg == null) ? new RegistrationPage(driver) : reg;

        Map<String, String> m = table.asMap(String.class, String.class);

        String email = m.getOrDefault("Email", "").trim();
        if (email.isEmpty() || !email.contains("@")) {
            email = "user+" + UUID.randomUUID().toString().substring(0,8) + "@example.com";
        } else {
            int at = email.indexOf("@");
            email = email.substring(0, at) + "+" + UUID.randomUUID().toString().substring(0,6) + email.substring(at);
        }

        reg.enterFirstName(m.getOrDefault("FirstName", ""));
        reg.enterLastName(m.getOrDefault("LastName", ""));
        reg.enterEmail(email);
        reg.enterCompany(m.getOrDefault("Company", ""));
        reg.setNewsletter(m.getOrDefault("Newsletter", "Yes"));
        reg.enterPassword(m.getOrDefault("Password", ""));
        reg.enterConfirmPassword(m.getOrDefault("ConfirmPassword", ""));
    }

    @And("I submit the registration form")
    public void i_submit_the_registration_form() {
        reg.clickRegister();
    }

    @Then("I should see {string}")
    public void i_should_see(String expected) {
        String actual = reg.getResultMessage();
        if (actual == null) actual = "";
        if (!actual.contains(expected)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
}
