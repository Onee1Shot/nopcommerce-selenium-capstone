package tests;



import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import pages.BasePage;

public class WishlistToCartTest {

    @BeforeClass
    @Parameters("baseUrl")
    public void setUp(String baseUrl) {
        BasePage.initializeDriver(baseUrl);
        BasePage.getDriver().get(baseUrl);
    }

    @Test
    public void testWishlistToCart() {
        BasePage.getDriver().findElement(By.linkText("Computers")).click();
        BasePage.getDriver().findElement(By.linkText("Desktops")).click();
        BasePage.getDriver().findElement(By.linkText("Build your own computer")).click();

        new Select(BasePage.getDriver().findElement(By.id("product_attribute_1")))
                .selectByVisibleText("2.5 GHz Intel Pentium Dual-Core E2200 [+$15.00]");
        new Select(BasePage.getDriver().findElement(By.id("product_attribute_2")))
                .selectByVisibleText("8GB [+$60.00]");

        BasePage.getDriver().findElement(By.id("product_attribute_3_7")).click();
        BasePage.getDriver().findElement(By.id("product_attribute_4_9")).click();
        BasePage.getDriver().findElement(By.id("product_attribute_5_12")).click();

        BasePage.getDriver().findElement(By.id("add-to-wishlist-button-1")).click();

        WebElement wishlistLink = BasePage.getWait()
                .until(ExpectedConditions.elementToBeClickable(By.linkText("Wishlist")));
        wishlistLink.click();

        WebElement wishlistItem = BasePage.getWait()
                .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Build your own computer")));
        Assert.assertTrue(wishlistItem.isDisplayed());

        BasePage.getDriver().findElement(By.name("addtocart")).click();
        BasePage.getDriver().findElement(By.name("addtocartbutton")).click();

        WebElement cartLink = BasePage.getWait()
                .until(ExpectedConditions.elementToBeClickable(By.linkText("Shopping cart")));
        cartLink.click();

        WebElement cartItem = BasePage.getWait()
                .until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Build your own computer")));
        Assert.assertTrue(cartItem.isDisplayed());
    }

    @AfterClass
    public void tearDown() {
        BasePage.quitDriver();
    }
}