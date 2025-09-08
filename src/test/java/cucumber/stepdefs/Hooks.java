package cucumber.stepdefs;



import java.time.Duration;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import pages.BasePage;

public class Hooks {

	@Before
	public void setUp() {
	    BasePage.initializeDriver("https://demo.nopcommerce.com");
	    BasePage.getDriver().manage().window().maximize();
	    BasePage.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
	}


    @After
    public void tearDown() {
        BasePage.quitDriver();
    }
}

