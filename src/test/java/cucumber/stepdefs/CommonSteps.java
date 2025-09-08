package cucumber.stepdefs;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import pages.BasePage;

public class CommonSteps {
	@Given("I am on the homepage")
	public void i_am_on_the_homepage() {
		BasePage.getDriver().get(BasePage.getBaseUrl());
	}

	@When("I navigate to {string} page")
	public void i_navigate_to_page(String page) {
		BasePage.clickHeaderLink(page);
	}

}
