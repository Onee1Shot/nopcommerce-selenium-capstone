package cucumber.stepdefs;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import pages.CatalogPage;
import pages.ProductPage;

import static org.testng.Assert.assertTrue;

public class CatalogSteps {

    private WebDriver driver;
    private CatalogPage catalog;
    private ProductPage product;

    @When("I navigate to category {string}")
    public void i_navigate_to_category(String category) {
        driver = BasePage.getDriver();
        catalog = new CatalogPage(driver);
        catalog.openCategory(category);
    }

    @And("I open the {string} subcategory if present, otherwise the first subcategory")
    public void i_open_the_subcategory_or_first(String subcategory) {
        catalog.openSubcategoryOrFirst(subcategory);
    }

    @And("I open the first product details page")
    public void i_open_the_first_product_details_page() {
        product = catalog.openFirstProductDetails();
    }

    @And("I configure options if required and add the product to the cart")
    public void i_configure_options_if_required_and_add_product() {
        if (product == null) product = new ProductPage(BasePage.getDriver());
        product.configureDefaultsAndAddToCart();
    }

    @Then("the shopping cart should show {int} item")
    public void the_shopping_cart_should_show_item(Integer expected) {
        int actual = BasePage.getCartCount();
        assertTrue(actual >= expected, "Expected cart qty >= " + expected + " but was " + actual);
    }
}
