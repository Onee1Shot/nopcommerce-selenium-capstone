package stepDefinition;

import static org.junit.Assert.assertEquals;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.After;
import io.cucumber.java.en.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import pageObject.LoginPage;

public class StepDef {

	public WebDriver driver;
	public LoginPage loginPg;

	@Given("User Launch chrome browser")
	public void user_launch_browser() {
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver(); // Assign to instance variable, no 'WebDriver' keyword here
		driver.manage().window().maximize(); // Optional but recommended to see the full browser window
		loginPg = new LoginPage(driver);
	}

	@When("User opens URL {string}")
	public void user_opens_url(String url) {
		System.out.println("Navigating to: " + url);
		driver.get(url);

	}

	@When("User enters Email as {string} and Password as {string}")
	public void user_enters_email_as_and_password_as(String email, String password) {
		loginPg.enterEmail(email);
		loginPg.enterPassword(password);
	}

	@When("Click on Login")
	public void click_on_login() {
		loginPg.clickOnLoginButton();
	}

	@Then("Page Title should be {string}")
	public void page_title_should_be(String expectedTitle) {
		String actualTitle = driver.getTitle();
		assertEquals(expectedTitle, actualTitle);
	}

	@When("User click on Log out link")
	public void user_click_on_log_out_link() {
		loginPg.clickOnLogOutButton();
	}

	@Then("Close Browser")
	public void close_Browser() {
		if (driver != null) {
			driver.quit();
		}
	}

}