package cucumber.stepdefs;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import pages.BasePage;
import pages.CartPage;
import pages.ProductPage;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertTrue;

public class CheckoutSteps {

    private final WebDriver driver = BasePage.getDriver();
    private final WebDriverWait wait = BasePage.getWait();

    /* ========================= navigation helpers ========================= */

    private void openCategory(String label) {
        By menu = By.linkText(label);
        wait.until(ExpectedConditions.elementToBeClickable(menu)).click();

        // Wait until either a product grid or a subcategory grid appears
        wait.until(d ->
                !d.findElements(By.cssSelector(".product-grid .item-box, .product-list .item-box")).isEmpty()
             || !d.findElements(By.cssSelector(".sub-category-grid .item-box, .category-grid .item-box")).isEmpty()
        );
    }

    private void openFirstProductOnPageOrInFirstSubcategory() {
        // 1) products on page now?
        List<WebElement> products = driver.findElements(By.cssSelector(
                ".product-grid .item-box h2.product-title a, .product-list .item-box h2.product-title a, " +
                ".product-grid .item-box .product-title a, .product-list .item-box .product-title a"));
        if (!products.isEmpty()) {
            products.get(0).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("button[id^='add-to-cart-button'], .add-to-cart-button, .rent-button")));
            return;
        }

        // 2) go into first subcategory then pick first product
        List<WebElement> subs = driver.findElements(By.cssSelector(
                ".sub-category-grid .item-box a, .category-grid .item-box a"));
        if (!subs.isEmpty()) {
            subs.get(0).click();
            wait.until(d -> !d.findElements(By.cssSelector(
                    ".product-grid .item-box h2.product-title a, .product-list .item-box h2.product-title a, " +
                    ".product-grid .item-box .product-title a, .product-list .item-box .product-title a")).isEmpty());
            driver.findElements(By.cssSelector(
                    ".product-grid .item-box h2.product-title a, .product-list .item-box h2.product-title a, " +
                    ".product-grid .item-box .product-title a, .product-list .item-box .product-title a"))
                  .get(0).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("button[id^='add-to-cart-button'], .add-to-cart-button, .rent-button")));
            return;
        }

        // 3) last resort
        List<WebElement> anyTitles = driver.findElements(By.cssSelector("h2 a, .product-title a"));
        if (!anyTitles.isEmpty()) {
            anyTitles.get(0).click();
            wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("button[id^='add-to-cart-button'], .add-to-cart-button, .rent-button")));
        }
    }

    private void goHome() {
        List<WebElement> logo = driver.findElements(By.cssSelector("a[href='/'], a[href='/nopcommerce'], img[alt*='nopCommerce']"));
        if (!logo.isEmpty()) logo.get(0).click();
        else driver.navigate().to(BasePage.getBaseUrl());
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".header, .master-wrapper-page")));
    }

    /* =========================== cart & adding ============================ */

    @When("I open the shopping cart")
    public void i_open_the_shopping_cart() {
        BasePage.clickHeaderLink("Shopping cart");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".cart, .order-summary-content")));
    }

    @When("I add one product from each supported category to the cart")
    public void i_add_one_product_from_each_supported_category_to_the_cart() {
        String[] cats = {
            "Computers",
            "Electronics",
            "Apparel",
            "Digital downloads",
            "Books",
            "Jewelry",
            "Gift Cards"
        };

        for (String cat : cats) {
            openCategory(cat);
            openFirstProductOnPageOrInFirstSubcategory();
            new ProductPage(driver).configureDefaultsAndAddToCart();
            goHome();
        }
    }

    @When("I accept the terms of service and proceed to checkout")
    public void i_accept_terms_and_checkout() {
        CartPage cart = new CartPage(driver);

        cart.openCart();           // be sure we’re on the cart page
        cart.ensureTermsAccepted();
        cart.clickCheckout();

        // Wait until sign-in (guest) or any checkout step is visible
        wait.until(d -> {
            if (d.getCurrentUrl().toLowerCase().contains("checkout")) return true;
            if (!d.findElements(By.cssSelector(
                    ".checkout-data, .checkout-page, #opc-billing, #checkout-step-billing, " +
                    "#checkout-step-shipping-method, #payment-methods-form, " +
                    "#payment-info-buttons-container, #confirm-order-buttons-container")).isEmpty()) return true;

            return !d.findElements(By.xpath(
                    "//*[contains(.,'Please Sign In') and (self::h1 or self::h2 or contains(@class,'page-title'))]")).isEmpty();
        });
    }

    /* ====================== choosing guest checkout ======================= */

    @When("I choose to checkout as {string}")
    public void i_choose_to_checkout_as(String type) {
        if (!type.equalsIgnoreCase("Guest"))
            throw new UnsupportedOperationException("Only Guest checkout is implemented");

        // If we’re at the “Please Sign In” page, hit “Checkout as Guest”
        By signInHeader = By.xpath("//*[contains(.,'Please Sign In') and (self::h1 or self::h2 or contains(@class,'page-title'))]");
        if (!driver.findElements(signInHeader).isEmpty()) {
            By guestBtn = By.cssSelector(
                "button.checkout-as-guest-button, button#guestCheckout, button.new-address-next-step-button");
            wait.until(ExpectedConditions.elementToBeClickable(guestBtn)).click();
        }

        // One-page checkout shell
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".checkout-page, .checkout-data")));
    }

    /* ========================== billing details ========================== */

    @When("I enter billing details")
    public void i_enter_billing_details(DataTable table) {
        Map<String,String> m = table.asMap(String.class, String.class);

        // Fill main fields
        type(By.id("BillingNewAddress_FirstName"), m.get("FirstName"));
        type(By.id("BillingNewAddress_LastName"),  m.get("LastName"));
        type(By.id("BillingNewAddress_Email"),     m.get("Email"));
        type(By.id("BillingNewAddress_Company"),   m.get("Company"));

        // Country → triggers AJAX for state list
        selectByVisibleText(By.id("BillingNewAddress_CountryId"), m.get("Country"));

        // State can be dropdown or textbox (theme dependent)
        By stateSelect = By.id("BillingNewAddress_StateProvinceId");
        By stateText   = By.id("BillingNewAddress_StateProvince");

        waitForStateOptionsToLoad(stateSelect); // tolerates textbox mode

        if (isDisplayed(stateSelect)) {
            selectStateWithFallback(stateSelect, m.get("State")); // prefers Odisha/Orissa
        } else if (isDisplayed(stateText)) {
            type(stateText, m.get("State"));
        }

        // rest of the fields
        type(By.id("BillingNewAddress_City"),          m.get("City"));
        type(By.id("BillingNewAddress_Address1"),      m.get("Address1"));
        type(By.id("BillingNewAddress_ZipPostalCode"), m.get("Zip"));
        type(By.id("BillingNewAddress_PhoneNumber"),   m.get("Phone"));
        type(By.id("BillingNewAddress_FaxNumber"),     m.get("Fax"));

        // Continue — handle “State / province is required.” alert gracefully
        clickAndHandleAlert(
            By.cssSelector("#billing-buttons-container .new-address-next-step-button, button.new-address-next-step-button"),
            () -> {
                // On alert, re-pick a valid state and retry
                if (isDisplayed(stateSelect)) {
                    selectStateWithFallback(stateSelect, m.get("State"));
                }
            }
        );

        // If a “Shipping address” step appears, just continue (we ship to same address)
        advanceIfPresent(By.cssSelector("#shipping-buttons-container .new-address-next-step-button, button.shipping-address-next-step-button"));
    }

    /* ========================== shipping & payment ======================= */

    @When("I select shipping method {string}")
    public void i_select_shipping_method(String methodLabel) {
        BasePage.acceptAlertIfPresent(); // in case anything remained from billing

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("shipping-methods-form")));
        clickRadioByLabelText(methodLabel);
        click(By.cssSelector("#shipping-method-buttons-container .shipping-method-next-step-button, button.shipping-method-next-step-button"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payment-methods-form")));
    }

    @When("I select payment method {string}")
    public void i_select_payment_method(String methodLabel) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payment-methods-form")));
        clickRadioByLabelText(methodLabel);
        click(By.cssSelector("#payment-method-buttons-container .payment-method-next-step-button, button.payment-method-next-step-button"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("payment-info-buttons-container")));
        click(By.cssSelector("#payment-info-buttons-container .payment-info-next-step_button, #payment-info-buttons-container .payment-info-next-step-button, button.payment-info-next-step-button"));

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".confirm-order")));
    }

    @Then("I should see an order confirmation with a number")
    public void i_should_see_order_number() {
        click(By.cssSelector("#confirm-order-buttons-container .confirm-order-next-step_button, #confirm-order-buttons-container .confirm-order-next-step-button, .confirm-order-next-step-button"));

        By done = By.cssSelector(".order-completed, .section.order-completed");
        wait.until(ExpectedConditions.visibilityOfElementLocated(done));

        String page = driver.findElement(By.tagName("body")).getText();
        assertTrue(page.matches("(?s).*ORDER NUMBER\\s*:\\s*\\d+.*"), "Order number not found on completion page.");
    }

    /* ============================ small helpers ========================== */

    private void type(By by, String val) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(by));
        el.clear();
        if (val != null) el.sendKeys(val);
    }

    private void click(By by) {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(by));
        try { ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
        el.click();
    }

    private boolean isDisplayed(By by) {
        List<WebElement> els = driver.findElements(by);
        return !els.isEmpty() && els.get(0).isDisplayed();
    }

    private void selectByVisibleText(By selectBy, String visible) {
        if (visible == null || visible.isBlank()) return;
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(selectBy));
        new Select(el).selectByVisibleText(visible);
    }

    /** Waits until the state list is ready after the country change (or textbox is used). */
    private void waitForStateOptionsToLoad(By stateSelect) {
        wait.withTimeout(Duration.ofSeconds(12))
            .pollingEvery(Duration.ofMillis(200))
            .ignoring(StaleElementReferenceException.class)
            .until(d -> {
                // If dropdown missing or hidden, theme may use a textbox instead
                List<WebElement> sel = d.findElements(stateSelect);
                if (sel.isEmpty() || !sel.get(0).isDisplayed()) return true;
                try {
                    Select s = new Select(sel.get(0));
                    List<WebElement> opts = s.getOptions();
                    if (opts == null) return false;
                    // consider loaded if >1 option or first option looks like a real one
                    if (opts.size() > 1) return true;
                    String v = opts.get(0).getAttribute("value");
                    return v != null && !v.trim().equals("0");
                } catch (Exception e) { return false; }
            });
    }

    /** Tries exact “Odisha”/“Orissa”, then falls back to first valid state. */
    private void selectStateWithFallback(By selectBy, String desired) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(selectBy));
        Select s = new Select(el);

        String want = (desired != null && !desired.isBlank()) ? desired.trim() : "Odisha";
        String wantLower = want.toLowerCase();

        // Try exact/contains match (Odisha or Orissa)
        for (WebElement opt : s.getOptions()) {
            String t = opt.getText().trim();
            String tl = t.toLowerCase();
            if (tl.equals(wantLower) || tl.equals("orissa") || tl.contains(wantLower)) {
                s.selectByVisibleText(t);
                return;
            }
        }

        // fallback: first non-placeholder option
        for (WebElement opt : s.getOptions()) {
            String v = opt.getAttribute("value");
            String t = opt.getText().trim().toLowerCase();
            if (v != null && !v.isBlank() && !"0".equals(v) && !t.contains("select")) {
                s.selectByValue(v);
                return;
            }
        }
    }

    /** Clicks and, if an alert pops (e.g., “State / province is required.”), accepts it and runs the fixer, then retries once. */
    private void clickAndHandleAlert(By button, Runnable fixIfAlert) {
        try {
            click(button);
            // tiny window to catch a browser alert
            new WebDriverWait(driver, Duration.ofSeconds(1))
                .ignoring(NoAlertPresentException.class)
                .until(d -> {
                    try { d.switchTo().alert().accept(); return true; }
                    catch (NoAlertPresentException e) { return true; }
                });
        } catch (UnhandledAlertException e) {
            BasePage.acceptAlertIfPresent();
            if (fixIfAlert != null) fixIfAlert.run();
            click(button);
        }
    }

    private void clickRadioByLabelText(String labelText) {
        String needle = labelText.trim().toLowerCase();
        for (WebElement l : driver.findElements(By.cssSelector("label"))) {
            try {
                if (l.getText() != null && l.getText().trim().toLowerCase().contains(needle)) {
                    String forId = l.getAttribute("for");
                    if (forId != null && !forId.isBlank()) {
                        WebElement r = driver.findElement(By.id(forId));
                        if (!r.isSelected()) r.click();
                        return;
                    }
                }
            } catch (Exception ignore) {}
        }
        // fallback: first radio
        List<WebElement> radios = driver.findElements(By.cssSelector("input[type='radio']"));
        if (!radios.isEmpty() && !radios.get(0).isSelected()) radios.get(0).click();
    }

    /** Click a continue button if present/visible (used for the optional “Shipping address” panel). */
    private void advanceIfPresent(By contBtn) {
        List<WebElement> b = driver.findElements(contBtn);
        if (!b.isEmpty() && b.get(0).isDisplayed()) b.get(0).click();
    }
}
