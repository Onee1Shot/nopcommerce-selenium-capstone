package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.time.Duration;
import java.util.List;

public class CheckoutPage extends BasePage {

    private final WebDriver drv;
    public CheckoutPage(WebDriver driver) { this.drv = driver; }

    /* ---------- sign-in / shell ---------- */
    private final By guestBtn = By.cssSelector(
            "button.checkout-as-guest-button, button#guestCheckout, button:nth-of-type(1).new-address-next-step-button");
    private final By signInHeader = By.xpath("//*[contains(.,'Please Sign In') and (self::h1 or self::h2 or contains(@class,'page-title'))]");
    private final By checkoutShell = By.cssSelector(".checkout-page, .checkout-data");

    /* ---------- billing ---------- */
    private final By billingPanel    = By.cssSelector("#checkout-step-billing, .checkout-data");
    private final By shipSameCheck   = By.id("ShipToSameAddress");
    private final By countrySelect   = By.id("BillingNewAddress_CountryId");
    private final By stateSelect     = By.id("BillingNewAddress_StateProvinceId");
    private final By stateText       = By.id("BillingNewAddress_StateProvince");
    private final By cityInput       = By.id("BillingNewAddress_City");
    private final By addr1Input      = By.id("BillingNewAddress_Address1");
    private final By zipInput        = By.id("BillingNewAddress_ZipPostalCode");
    private final By phoneInput      = By.id("BillingNewAddress_PhoneNumber");
    private final By faxInput        = By.id("BillingNewAddress_FaxNumber");
    private final By firstNameInput  = By.id("BillingNewAddress_FirstName");
    private final By lastNameInput   = By.id("BillingNewAddress_LastName");
    private final By emailInput      = By.id("BillingNewAddress_Email");
    private final By companyInput    = By.id("BillingNewAddress_Company");
    private final By billingContinue = By.cssSelector(
            "#billing-buttons-container .new-address-next-step-button, button.new-address-next-step-button");

    /* ---------- shipping address (optional) ---------- */
    private final By shippingContinue = By.cssSelector(
            "#shipping-buttons-container .new-address-next-step-button, button.shipping-address-next-step-button");

    /* ---------- shipping method ---------- */
    private final By shippingMethodForm    = By.id("shipping-methods-form");
    private final By shippingMethodContinue= By.cssSelector("#shipping-method-buttons-container .button-1, button.shipping-method-next-step-button");

    /* ---------- payment method ---------- */
    private final By paymentMethodForm     = By.id("payment-methods-form");
    private final By paymentMethodContinue = By.cssSelector("#payment-method-buttons-container .button-1, button.payment-method-next-step-button");

    /* ---------- payment info ---------- */
    private final By paymentInfoContinue   = By.cssSelector("#payment-info-buttons-container .button-1, button.payment-info-next-step-button");

    /* ---------- confirm ---------- */
    private final By confirmBtn        = By.cssSelector("#confirm-order-buttons-container .button-1, .confirm-order-next-step-button");
    private final By orderNumberBlock  = By.xpath("//*[contains(.,'ORDER NUMBER') or contains(.,'Order Number')]");
    private final By thankYouHeader    = By.xpath("//*[normalize-space()='Thank you' or contains(.,'successfully processed')]");

    /* ===================== public API ===================== */

    public void arriveOnOnePageCheckout() {
        if (!drv.findElements(signInHeader).isEmpty()) {
            getWait().until(ExpectedConditions.elementToBeClickable(guestBtn)).click();
        }
        getWait().until(ExpectedConditions.visibilityOfElementLocated(checkoutShell));
    }

    public static class BillingData {
        public String firstName, lastName, email, company;
        public String country, stateOrProvince, city, address1, zip, phone, fax;
    }

    public void fillBillingAddress(BillingData d) {
        getWait().until(ExpectedConditions.visibilityOfElementLocated(billingPanel));

        type(firstNameInput, d.firstName);
        type(lastNameInput,  d.lastName);
        type(emailInput,     d.email);
        type(companyInput,   d.company);

        // Country first (triggers async state reload)
        selectByVisibleText(countrySelect, d.country);
        waitForStateLoaded();

        if (isDisplayed(stateSelect)) {
            selectStateLoose(d.stateOrProvince); // try preferred (Odisha), else first valid
        } else if (isDisplayed(stateText)) {
            type(stateText, d.stateOrProvince);
        }

        type(cityInput,  d.city);
        type(addr1Input, d.address1);
        type(zipInput,   d.zip);
        type(phoneInput, d.phone);
        type(faxInput,   d.fax);

        // Keep "Ship to the same address" checked
        try {
            WebElement same = drv.findElement(shipSameCheck);
            if (!same.isSelected()) same.click();
        } catch (Exception ignore) {}

        clickAndHandleStateAlert(billingContinue);
    }

    public void continueShippingAddressIfPresent() {
        List<WebElement> btns = drv.findElements(shippingContinue);
        if (!btns.isEmpty() && btns.get(0).isDisplayed()) btns.get(0).click();
    }

    public void chooseShippingMethod(String label) {
        // In case a previous alert is lingering
        acceptAlertIfPresent();

        getWait().until(ExpectedConditions.visibilityOfElementLocated(shippingMethodForm));
        clickRadioByLabelText(label);
        getWait().until(ExpectedConditions.elementToBeClickable(shippingMethodContinue)).click();
    }

    public void choosePaymentMethod(String label) {
        getWait().until(ExpectedConditions.visibilityOfElementLocated(paymentMethodForm));
        clickRadioByLabelText(label);
        getWait().until(ExpectedConditions.elementToBeClickable(paymentMethodContinue)).click();
    }

    public void continuePaymentInfo() {
        getWait().until(ExpectedConditions.elementToBeClickable(paymentInfoContinue)).click();
    }

    public String confirmOrderAndGetNumber() {
        getWait().until(ExpectedConditions.elementToBeClickable(confirmBtn)).click();
        getWait().until(ExpectedConditions.visibilityOfElementLocated(thankYouHeader));
        WebElement block = getWait().until(ExpectedConditions.visibilityOfElementLocated(orderNumberBlock));
        String digits = block.getText().replaceAll("\\D+","");
        return digits;
    }

    /* ===================== internals ===================== */

    private void waitForStateLoaded() {
        getWait().withTimeout(Duration.ofSeconds(12))
                 .ignoring(StaleElementReferenceException.class)
                 .until(d -> {
                     // If dropdown not present/visible, maybe a text input is used – treat as loaded
                     List<WebElement> sel = d.findElements(stateSelect);
                     if (sel.isEmpty() || !sel.get(0).isDisplayed()) return true;
                     try {
                         Select s = new Select(sel.get(0));
                         List<WebElement> opts = s.getOptions();
                         return opts != null && opts.size() > 1; // more than placeholder
                     } catch (Exception e) { return false; }
                 });
    }

    private void selectStateLoose(String preferred) {
        Select s = new Select(getWait().until(ExpectedConditions.visibilityOfElementLocated(stateSelect)));

        // Try preferred, also accept "Orissa" variant
        if (preferred != null && !preferred.isBlank()) {
            for (WebElement opt : s.getOptions()) {
                String t = opt.getText().trim();
                if (t.equalsIgnoreCase(preferred) || (preferred.equalsIgnoreCase("Odisha") && t.equalsIgnoreCase("Orissa"))) {
                    opt.click();
                    return;
                }
            }
        }
        // Else first non-placeholder
        for (WebElement opt : s.getOptions()) {
            String v = opt.getAttribute("value");
            String t = opt.getText().trim().toLowerCase();
            if (v != null && !v.equals("0") && !t.contains("select")) {
                opt.click();
                return;
            }
        }
        // Fallback: first option
        s.selectByIndex(0);
    }

    private boolean isDisplayed(By by) {
        List<WebElement> els = drv.findElements(by);
        return !els.isEmpty() && els.get(0).isDisplayed();
    }

    private void type(By by, String val) {
        if (val == null) return;
        WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(by));
        el.clear();
        el.sendKeys(val);
    }

    private void selectByVisibleText(By by, String visible) {
        WebElement el = getWait().until(ExpectedConditions.visibilityOfElementLocated(by));
        new Select(el).selectByVisibleText(visible);
    }

    private void clickRadioByLabelText(String labelText) {
        String needle = labelText.trim().toLowerCase();
        for (WebElement l : drv.findElements(By.cssSelector("label"))) {
            try {
                if (l.getText() != null && l.getText().trim().toLowerCase().contains(needle)) {
                    String forId = l.getAttribute("for");
                    if (forId != null && !forId.isBlank()) {
                        WebElement r = drv.findElement(By.id(forId));
                        if (!r.isSelected()) r.click();
                        return;
                    }
                }
            } catch (Exception ignore) {}
        }
        // fallback: first radio
        List<WebElement> radios = drv.findElements(By.cssSelector("input[type='radio']"));
        if (!radios.isEmpty() && !radios.get(0).isSelected()) radios.get(0).click();
    }

    private void clickAndHandleStateAlert(By contBtn) {
        try {
            getWait().until(ExpectedConditions.elementToBeClickable(contBtn)).click();
            // brief window to catch possible alert
            new WebDriverWait(drv, Duration.ofSeconds(1))
                .ignoring(NoAlertPresentException.class)
                .until(d -> { try { d.switchTo().alert().accept(); return true; } catch (NoAlertPresentException e) { return true; }});
        } catch (UnhandledAlertException e) {
        	BasePage.acceptAlertIfPresent();
            // ensure state is selected then retry
            if (isDisplayed(stateSelect)) selectStateLoose("Odisha");
            getWait().until(ExpectedConditions.elementToBeClickable(contBtn)).click();
        }
    }

}
