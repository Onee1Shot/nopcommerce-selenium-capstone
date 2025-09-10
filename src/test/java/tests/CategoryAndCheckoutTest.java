package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import pages.BasePage;

public class CategoryAndCheckoutTest {
    private static int stepCounter;
    private static final Logger logger = LogManager.getLogger(CategoryAndCheckoutTest.class);

    @BeforeClass
    @Parameters("baseUrl")
    public void setUp(String baseUrl) {
        stepCounter = 1;
        logStep("Launching browser and setting base URL: " + baseUrl);
        BasePage.initializeDriver(baseUrl);
    }

    @BeforeMethod
    public void goToHomePage() {
        logStep("Navigating to home page");
        BasePage.getDriver().get(BasePage.getBaseUrl());
    }

    protected void logStep(String message) {
        logger.info("Step " + stepCounter++ + ": " + message);
       }
    @AfterClass
    public void tearDown() {
        logStep("Closing browser after completing checkout");
        BasePage.quitDriver();

    }

    // ======== Add Products ========
    @Test(priority = 1)
    public void addComputerProduct() {
        logStep("Clicking on Computers");
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

        BasePage.getDriver().findElement(By.id("add-to-cart-button-1")).click();
    
    }

    @Test(priority = 2)
    public void addElectronicsProduct() {
        logStep("Clicking on Electronics");
        BasePage.getDriver().findElement(By.linkText("Electronics")).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Cell phones"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("HTC One Mini Blue"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-19"))).click();
     
    }

    @Test(priority = 3)
    public void addApparelProduct() {
        logStep("Clicking on Apparel");
        BasePage.getDriver().findElement(By.linkText("Apparel")).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Shoes"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.linkText("adidas Consortium Campus 80s Running Shoes"))).click();
        WebElement sizeDropdown = BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("product_attribute_9")));
        new Select(sizeDropdown).selectByVisibleText("9");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-27"))).click();
      
    }

    @Test(priority = 4)
    public void addDigitalDownloadProduct() {
        logStep("Clicking on Digital downloads");
        BasePage.getDriver().findElement(By.linkText("Digital downloads")).click();
        BasePage.getDriver().findElement(By.cssSelector(".product-title a")).click();
        BasePage.getDriver().findElement(By.cssSelector("button.add-to-cart-button")).click();
     
    }

    @Test(priority = 5)
    public void addBooksProduct() {
        logStep("Clicking on Books");
        BasePage.getDriver().findElement(By.linkText("Books")).click();
        BasePage.getDriver().findElement(By.cssSelector(".product-title a")).click();
        BasePage.getDriver().findElement(By.cssSelector("button.add-to-cart-button")).click();
     
    }

    @Test(priority = 6)
    public void addJewelryRentalProduct() {
        logStep("Clicking on Jewelry");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Jewelry"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Elegant Gemstone Necklace (rental)"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("rental_start_date_42"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table[@class='ui-datepicker-calendar']//a[text()='10']"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("rental_end_date_42"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.xpath("//table[@class='ui-datepicker-calendar']//a[text()='20']"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("add-to-cart-button-42"))).click();
     
    }

    @Test(priority = 7)
    public void addVirtualGiftCardProduct() {
        logStep("Clicking on Gift Cards");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Gift Cards"))).click();
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("$25 Virtual Gift Card"))).click();
        BasePage.getDriver().findElement(By.id("giftcard_45_RecipientName")).sendKeys("Alice Recipient");
        BasePage.getDriver().findElement(By.id("giftcard_45_RecipientEmail")).sendKeys("alice@example.com");
        BasePage.getDriver().findElement(By.id("giftcard_45_SenderName")).sendKeys("Bob Sender");
        BasePage.getDriver().findElement(By.id("giftcard_45_SenderEmail")).sendKeys("bob@example.com");
        BasePage.getDriver().findElement(By.id("add-to-cart-button-45")).click();
      
    }

    // ======== Checkout All Products ========
    @Test(priority = 8)
    public void checkoutAllProducts() {
        logStep("Opening Shopping Cart");
        BasePage.getDriver().findElement(By.cssSelector("span.cart-label")).click();

        logStep("Accepting Terms of Service");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("termsofservice"))).click();

        logStep("Clicking Checkout");
        BasePage.getDriver().findElement(By.id("checkout")).click();

        logStep("Choosing Guest Checkout");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.checkout-as-guest-button"))).click();

        logStep("Filling Billing Address");

        // Country selection
        WebElement countryDropdown = BasePage.getWait().until(
                ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_CountryId"))
        );
        new Select(countryDropdown).selectByVisibleText("United States of America");

        // Dynamic State handling
        WebElement stateField = BasePage.getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.id("BillingNewAddress_StateProvinceId"))
        );
        if (stateField.getTagName().equals("select")) {
            new Select(stateField).selectByIndex(1);
        } else {
            stateField.clear();
            stateField.sendKeys("New York");
        }

        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_FirstName"))).sendKeys("John");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_LastName"))).sendKeys("Doe");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_Email"))).sendKeys("johndoe@example.com");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_City"))).sendKeys("New York");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_Address1"))).sendKeys("123 Main St");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_ZipPostalCode"))).sendKeys("10001");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(By.id("BillingNewAddress_PhoneNumber"))).sendKeys("1234567890");

        logStep("Clicking Next on Billing Address");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.new-address-next-step-button"))).click();

        logStep("Selecting Shipping Method");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.shipping-method-next-step-button"))).click();

        logStep("Selecting Payment Method");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.payment-method-next-step-button"))).click();

        logStep("Filling Payment Information");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.payment-info-next-step-button"))).click();

        logStep("Confirming Order");
        BasePage.getWait().until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.confirm-order-next-step-button"))).click();

        logStep("Verifying Order Confirmation");
        WebElement confirmation = BasePage.getWait().until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.section.order-completed"))
        );
        Assert.assertTrue(confirmation.getText().contains("Your order has been successfully processed!"));

        logStep("Checkout completed successfully");
    }
}
  