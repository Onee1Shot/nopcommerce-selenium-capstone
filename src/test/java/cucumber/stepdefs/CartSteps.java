package cucumber.stepdefs;

import io.cucumber.java.en.And;
import pages.BasePage;

import static org.testng.Assert.assertEquals;

public class CartSteps {

    @And("I empty the shopping cart")
    public void i_empty_the_shopping_cart() {
        BasePage.emptyCartIfAny();
        // hard assert the cart is empty before we start
        assertEquals(BasePage.getCartCount(), 0, "Cart was not emptied");
    }
}
