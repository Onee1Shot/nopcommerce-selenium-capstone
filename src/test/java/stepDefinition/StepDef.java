package stepDefinition;

import org.junit.Assert;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import pageObject.AddNewCustomerPage;
import pageObject.LoginPage;
import io.cucumber.java.en.*;
import io.github.bonigarcia.wdm.WebDriverManager;

public class StepDef {

    public WebDriver driver;
    public LoginPage loginPg;
    public AddNewCustomerPage addNewCustomerPg;
    
    
    @Given("User Launch Chrome browser")
    public void user_launch_chrome_browser() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.setCapability("unhandledPromptBehavior", "dismiss");  // auto-dismiss unexpected alerts
        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
        loginPg = new LoginPage(driver);
        addNewCustomerPg = new AddNewCustomerPage(driver);
    }

    @When("User opens URL {string}")
    public void user_opens_url(String url) {
        driver.get(url);
    }

    @When("User enters Email as {string} and Password as {string}")
    public void user_enters_email_as_and_password_as(String emailadd, String password) {
        loginPg.enterEmail(emailadd);
        loginPg.enterPassword(password);
    }

    @When("Click on Login")
    public void click_on_login() {
        loginPg.clickOnLoginButton();
    }

    @Then("Page Title should be {string}")
    public void page_title_should_be(String expectedTitle) {

        // handle any unexpected alert first
        try {
            Alert alert = driver.switchTo().alert();
            System.out.println("Unexpected alert detected: " + alert.getText());
            alert.accept(); // accept and continue
        } catch (NoAlertPresentException e) {
            // no alert, continue
        }

        String actualTitle = driver.getTitle().trim();
        System.out.println("Expected Title: " + expectedTitle);
        System.out.println("Actual Title: " + actualTitle);

        Assert.assertEquals("Page title mismatch!", expectedTitle.trim(), actualTitle);
    }

    @When("User click on Log out link")
    public void user_click_on_log_out_link() {
        loginPg.clickOnLogOutButton();
    }

    @When("close browser")
    public void close_browser() {
        driver.quit();
    }
    
    
    @Then("User can view Dashboard")
    public void user_can_view_dashboard() {
    	
    	String actualTitle = addNewCustomerPg.getPageTitle();
    	String expectedTitle= "Dashboard / nopCommerce administration";
    	
    	if(actualTitle.equals(expectedTitle)) {
    		Assert.assertTrue(true);
    	}
    	else {
    		Assert.assertTrue(false);
    	}
    	      
    }

    @When("User click on customers Menu")
    public void user_click_on_customers_menu() {
    	
    	addNewCustomerPg.clickOnCustomersMenu();
       
    }

    @When("click on customers Menu Item")
    public void click_on_customers_menu_item() {
    	addNewCustomerPg.clickOnCustomersMenuItem();
    	
    }

    @When("click on Add new button")
    public void click_on_add_new_button() {
    	
    	addNewCustomerPg.clickOnAddnew();
    	     
    }

    @Then("User can view Add new customer page")
    public void user_can_view_add_new_customer_page() {
    	
    	String actualTitle= addNewCustomerPg.getPageTitle();
    	String expectedTitle = "Add a new customer / nopCommerce administration";
    	
    	if(actualTitle.equals(expectedTitle)) {
    		Assert.assertTrue(true);
    	}
    	else {
    		Assert.assertTrue(false);
    	}    
    }

    @When("User enters customer info")
    public void user_enters_customer_info() {
    	
    	addNewCustomerPg.enterEmail("NEW_WIPRO@gmail.com");
    	addNewCustomerPg.enterPassword("test1");
    	addNewCustomerPg.enterFirstName("Himanshu");
    	addNewCustomerPg.enterLastName("Gupta");
    	addNewCustomerPg.enterGender("Male");
//    	addNewCustomerPg.enterDob("28/08/2002");
    	addNewCustomerPg.enterCompanyName("Wipro");
    	addNewCustomerPg.enterAdminContent("Admin content");
//    	addNewCustomerPg.enterManagerOfVendor("Manager");
    	    
    }

    @When("click on Save button")
    public void click_on_save_button() {
    	addNewCustomerPg.clickOnSave();
        
    }

    @Then("User can view confirmation message {string}")
    public void user_can_view_confirmation_message(String expectedConfirmationMessage) {
    	String bodyTagText = driver.findElement(By.tagName("Body")).getText();
    	if (bodyTagText.contains(expectedConfirmationMessage)) {
    		Assert.assertTrue(true);
			
		}
    	else {
    		Assert.assertTrue(false);
    		
    	}
    	
        
    }


}