package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProductPage extends BasePage {

    private final WebDriver drv;

    public ProductPage(WebDriver driver) {
        this.drv = driver;
    }

    /** Main action used by steps */
    public void configureDefaultsAndAddToCart() {
        waitUntilProductReady();

        // Fill/select required inputs first
        selectFirstOptionsIfAny();
        fillTextAttributesIfAny();     // e.g. custom t-shirt text
        fillGiftCardFieldsIfAny();     // virtual gift card name/emails/message
        fillRentalDatesIfAny();        // rental start/end if present
        selectShippingAddressIfAny();  // rarely appears on PDP

        // Quantity (respect minimum)
        int minQty = Math.max(1, detectMinimumQuantity());
        setQuantity(minQty);

        // Remember current cart count and click Add/Rent
        int before = BasePage.getCartCount();
        clickAddOrRent();

        // If validation tells us something is missing, do one more pass and retry once
        if (hasValidationText(
                "please select", "enter your text", "enter rental",
                "recipient", "sender", "email", "start date", "end date")) {

            selectFirstOptionsIfAny();
            fillTextAttributesIfAny();
            fillGiftCardFieldsIfAny();
            fillRentalDatesIfAny();
            selectShippingAddressIfAny();
            clickAddOrRent();
        }

        // Robust wait for either badge increase or green toast
        BasePage.waitForCartCountToIncreaseOrSuccess(before);

        // Close toast (cosmetic)
        BasePage.closeSuccessBarIfPresent();
    }

    // ----------------- helpers -----------------

    private void waitUntilProductReady() {
        By addOrRent = By.cssSelector(
                "button[id^='add-to-cart-button'], input[id^='add-to-cart-button'], " +
                "button.add-to-cart-button, input.add-to-cart-button, " +
                "button.rent-button, input.rent-button");
        getWait().until(ExpectedConditions.presenceOfElementLocated(addOrRent));
    }

    /** Select a sane first value for all attribute select/radio controls on the page. */
    private void selectFirstOptionsIfAny() {
        // Dropdowns
        List<WebElement> selects = drv.findElements(
                By.cssSelector("select[name^='product_attribute_'], select[id^='product_attribute_']"));
        for (WebElement s : selects) {
            try {
                Select sel = new Select(s);
                String current = sel.getFirstSelectedOption().getAttribute("value");
                if (current == null || current.isBlank() || current.equals("0")) {
                    for (WebElement opt : sel.getOptions()) {
                        String v = opt.getAttribute("value");
                        if (v != null && !v.isBlank() && !v.equals("0")) { sel.selectByValue(v); break; }
                    }
                }
            } catch (Exception ignore) {}
        }

        // Radios (pick first enabled per group)
        List<WebElement> radios = drv.findElements(
                By.cssSelector("input[type='radio'][name^='product_attribute_']"));
        Map<String, Boolean> selectedByGroup = new HashMap<>();
        for (WebElement r : radios) {
            try {
                String name = r.getAttribute("name");
                if (name == null) continue;
                if (Boolean.TRUE.equals(selectedByGroup.get(name))) continue;
                if (r.isEnabled()) {
                    try { ((JavascriptExecutor) drv).executeScript(
                            "arguments[0].scrollIntoView({block:'center'});", r); } catch (Exception ignore) {}
                    r.click();
                    selectedByGroup.put(name, true);
                }
            } catch (Exception ignore) {}
        }
    }

    /** Fill required text/textarea attribute editors (e.g., Custom T-Shirt text). */
    private void fillTextAttributesIfAny() {
        List<WebElement> textAttrs = drv.findElements(
                By.cssSelector("input[type='text'][name^='product_attribute_'], textarea[name^='product_attribute_']"));
        for (WebElement t : textAttrs) {
            try {
                if (t.isDisplayed() && t.isEnabled()) {
                    String val = t.getAttribute("value");
                    if (val == null || val.isBlank()) {
                        ((JavascriptExecutor) drv).executeScript(
                                "arguments[0].scrollIntoView({block:'center'});", t);
                        t.clear();
                        t.sendKeys("Auto Text");
                    }
                }
            } catch (Exception ignore) {}
        }
    }

    /** Virtual Gift Card fields: recipient/sender name/email & message. */
    private void fillGiftCardFieldsIfAny() {
        setIfEmpty(findFirstDisplayed(Arrays.asList(
                By.cssSelector("input[id^='giftcard_'][id$='_RecipientName']"),
                By.cssSelector("input[name*='RecipientName']"))), "QA Recipient");

        setIfEmpty(findFirstDisplayed(Arrays.asList(
                By.cssSelector("input[id^='giftcard_'][id$='_RecipientEmail']"),
                By.cssSelector("input[name*='RecipientEmail']"))), "qa.recipient@example.com");

        setIfEmpty(findFirstDisplayed(Arrays.asList(
                By.cssSelector("input[id^='giftcard_'][id$='_SenderName']"),
                By.cssSelector("input[name*='SenderName']"))), "QA Sender");

        setIfEmpty(findFirstDisplayed(Arrays.asList(
                By.cssSelector("input[id^='giftcard_'][id$='_SenderEmail']"),
                By.cssSelector("input[name*='SenderEmail']"))), "qa.sender@example.com");

        setIfEmpty(findFirstDisplayed(Arrays.asList(
                By.cssSelector("textarea[id^='giftcard_'][id$='_Message']"),
                By.cssSelector("textarea[name*='Message']"))), "Automated gift card purchase");
    }

    private void setIfEmpty(WebElement el, String value) {
        if (el == null) return;
        try {
            if (!el.isDisplayed() || !el.isEnabled()) return;
            String current = el.getAttribute("value");
            if (current == null || current.isBlank()) {
                try { ((JavascriptExecutor) drv).executeScript(
                        "arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
                el.click();
                el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
                el.sendKeys(value);
                el.sendKeys(Keys.TAB);
            }
        } catch (Exception ignore) {}
    }

    /** Rental products: fill Start/End date (today + 1 day) if fields exist. */
    private void fillRentalDatesIfAny() {
        WebElement start = findFirstDisplayed(Arrays.asList(
                By.cssSelector("input[id*='rental'][id*='start']"),
                By.cssSelector("input[name*='rental'][name*='start']"),
                By.cssSelector("input[id*='Start'][id*='date']"),
                By.cssSelector("input[name*='Start'][name*='date']")
        ));
        WebElement end = findFirstDisplayed(Arrays.asList(
                By.cssSelector("input[id*='rental'][id*='end']"),
                By.cssSelector("input[name*='rental'][name*='end']"),
                By.cssSelector("input[id*='End'][id*='date']"),
                By.cssSelector("input[name*='End'][name*='date']")
        ));
        if (start == null || end == null) return;

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");

        setInputValue(start, today.format(fmt));
        setInputValue(end,   tomorrow.format(fmt));
    }

    private WebElement findFirstDisplayed(List<By> locators) {
        for (By by : locators) {
            try {
                List<WebElement> els = drv.findElements(by);
                for (WebElement e : els) if (e.isDisplayed()) return e;
            } catch (Exception ignore) {}
        }
        return null;
    }

    private void setInputValue(WebElement el, String value) {
        try { ((JavascriptExecutor) drv).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
        try {
            el.click();
            el.sendKeys(Keys.chord(Keys.CONTROL, "a"));
            el.sendKeys(value);
            el.sendKeys(Keys.TAB);
        } catch (Exception ex) {
            try {
                ((JavascriptExecutor) drv).executeScript(
                        "arguments[0].value = arguments[1]; " +
                                "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));",
                        el, value);
            } catch (Exception ignore) {}
        }
    }

    /** If a shipping-address dropdown is present on the product page, pick the first real value. */
    private void selectShippingAddressIfAny() {
        List<By> locators = Arrays.asList(
                By.cssSelector("select[id*='address']"),
                By.cssSelector("select[name*='address']")
        );
        for (By by : locators) {
            List<WebElement> sels = drv.findElements(by);
            if (!sels.isEmpty()) {
                try {
                    Select sel = new Select(sels.get(0));
                    for (WebElement opt : sel.getOptions()) {
                        String v = opt.getAttribute("value");
                        if (v != null && !v.isBlank() && !v.equals("0")) { sel.selectByValue(v); break; }
                    }
                } catch (Exception ignore) {}
                break;
            }
        }
    }

    /** Detect “minimum quantity of N” text and return N (or 0 if not found). */
    private int detectMinimumQuantity() {
        try {
            String pageText = drv.findElement(By.tagName("body")).getText();
            Matcher m = Pattern.compile("minimum\\s+quantity\\s+of\\s+(\\d+)",
                    Pattern.CASE_INSENSITIVE).matcher(pageText);
            if (m.find()) return Integer.parseInt(m.group(1));
        } catch (Exception ignore) {}
        return 1;
    }

    private void setQuantity(int qty) {
        List<By> qtyLocators = Arrays.asList(
                By.cssSelector("input[id^='product_enteredQuantity']"),
                By.cssSelector("input.qty-input"),
                By.name("addtocart_EnteredQuantity")
        );
        for (By locator : qtyLocators) {
            try {
                WebElement q = drv.findElement(locator);
                if (q.isDisplayed()) {
                    q.clear();
                    q.sendKeys(String.valueOf(qty));
                    return;
                }
            } catch (NoSuchElementException ignore) {}
        }
    }

    /** Clicks RENT if visible, else clicks Add to cart. Uses JS click fallback. */
    private void clickAddOrRent() {
        By addOrRent = By.cssSelector(
                "button.rent-button, input.rent-button, " + // rental
                "button[id^='add-to-cart-button'], input[id^='add-to-cart-button'], " +
                "button.add-to-cart-button, input.add-to-cart-button");

        WebElement btn = getWait().until(ExpectedConditions.elementToBeClickable(addOrRent));
        try { ((JavascriptExecutor) drv).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", btn); } catch (Exception ignore) {}
        try {
            btn.click();
        } catch (Exception clickProblem) {
            // fallback to JS click
            ((JavascriptExecutor) drv).executeScript("arguments[0].click();", btn);
        }

        // Wait for either success or error toast to appear (whichever first)
        try {
            getWait().until(d -> BasePage.isSuccessBarVisible() ||
                    !d.findElements(By.cssSelector(".bar-notification.error, .message-error, .validation-summary-errors")).isEmpty());
        } catch (Exception ignore) {}
    }

    private boolean hasValidationText(String... needles) {
        try {
            List<WebElement> bars = new ArrayList<>();
            bars.addAll(drv.findElements(By.cssSelector(".bar-notification.error")));
            bars.addAll(drv.findElements(By.cssSelector(".message-error, .validation-summary-errors")));
            String all = "";
            for (WebElement b : bars) {
                String t = b.getText();
                if (t != null) all += " " + t.toLowerCase();
            }
            for (String n : needles) {
                if (all.contains(n.toLowerCase())) return true;
            }
        } catch (Exception ignore) {}
        return false;
    }
}
