package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

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

        // Select / fill all required options first
        selectFirstOptionsIfAny();

        // Respect minimum quantity if present, otherwise default to 1
        int minQty = Math.max(1, detectMinimumQuantity());
        setQuantity(minQty);

        // Remember current cart count and try to add
        int before = BasePage.getCartCount();
        clickAddToCart();

        // If page shows validation like "Please select ..." or "Enter your text", fix and retry once
        if (isValidationBarVisibleWithText("please select") || isValidationBarVisibleWithText("enter your text")) {
            selectFirstOptionsIfAny();   // ensures dropdowns/radios/text/address are satisfied
            clickAddToCart();
        }

        // Wait for the cart badge to increase
        BasePage.waitForCartCountToIncrease(before);

        // Close success toast if present (cosmetic)
        closeSuccessBarIfPresent();
    }

    // ----------------- helpers -----------------

    private void waitUntilProductReady() {
        // Wait for add-to-cart button to be present; product pages differ in markup
        By addBtn = By.cssSelector(
                "button[id^='add-to-cart-button'], input[id^='add-to-cart-button'], " +
                "button.add-to-cart-button, input.add-to-cart-button");
        getWait().until(ExpectedConditions.presenceOfElementLocated(addBtn));
    }

    /** Selects a sane first value for all attribute controls on the page. */
    private void selectFirstOptionsIfAny() {
        // Dropdowns: ids/names like product_attribute_2, _3, ...
        List<WebElement> selects = drv.findElements(
                By.cssSelector("select[name^='product_attribute_'], select[id^='product_attribute_']"));
        for (WebElement s : selects) {
            try {
                Select sel = new Select(s);
                String current = sel.getFirstSelectedOption().getAttribute("value");
                if (current == null || current.isBlank() || current.equals("0")) {
                    for (WebElement opt : sel.getOptions()) {
                        String v = opt.getAttribute("value");
                        if (v != null && !v.isBlank() && !v.equals("0")) {
                            sel.selectByValue(v);
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {}
        }

        // Radio groups: pick the first enabled per group
        List<WebElement> radios = drv.findElements(By.cssSelector("input[type='radio'][name^='product_attribute_']"));
        Map<String, Boolean> selectedByGroup = new HashMap<>();
        for (WebElement r : radios) {
            try {
                String name = r.getAttribute("name");
                if (name == null) continue;
                if (Boolean.TRUE.equals(selectedByGroup.get(name))) continue; // already picked for this group
                if (r.isEnabled()) {
                    try { ((JavascriptExecutor) drv).executeScript("arguments[0].scrollIntoView({block:'center'});", r); } catch (Exception ignore) {}
                    r.click();
                    selectedByGroup.put(name, true);
                }
            } catch (Exception ignore) {}
        }

        // Fill required text/textarea attributes (e.g., "Enter your text")
        fillTextAttributesIfAny();

        // Some products show a shipping address dropdown on the product page
        selectShippingAddressIfAny();
    }

    /** Fill required text/textarea attributes if they are empty. */
    private void fillTextAttributesIfAny() {
        // Inputs like: <input type="text" name="product_attribute_XX"> or <textarea ...>
        List<WebElement> textAttrs = drv.findElements(
                By.cssSelector("input[type='text'][name^='product_attribute_'], textarea[name^='product_attribute_']"));
        for (WebElement t : textAttrs) {
            try {
                if (t.isDisplayed() && t.isEnabled()) {
                    String val = t.getAttribute("value");
                    if (val == null || val.isBlank()) {
                        ((JavascriptExecutor) drv).executeScript("arguments[0].scrollIntoView({block:'center'});", t);
                        t.clear();
                        t.sendKeys("Auto Text"); // default content for required text attributes
                    }
                }
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

    /** Detect "minimum quantity of N" text and return N (or 0 if not found). */
    private int detectMinimumQuantity() {
        try {
            // Typical text: "This product has a minimum quantity of 2"
            String pageText = drv.findElement(By.tagName("body")).getText();
            Matcher m = Pattern.compile("minimum\\s+quantity\\s+of\\s+(\\d+)", Pattern.CASE_INSENSITIVE).matcher(pageText);
            if (m.find()) return Integer.parseInt(m.group(1));
        } catch (Exception ignore) {}
        return 0;
    }

    private void setQuantity(int qty) {
        // Quantity inputs vary by theme: look for typical ids
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
        // If no qty box exists, product defaults to 1 (or site-enforced min); do nothing.
    }

    private void clickAddToCart() {
        By addBtn = By.cssSelector(
                "button[id^='add-to-cart-button'], input[id^='add-to-cart-button'], " +
                "button.add-to-cart-button, input.add-to-cart-button");
        WebElement btn = getWait().until(ExpectedConditions.elementToBeClickable(addBtn));
        try { ((JavascriptExecutor) drv).executeScript("arguments[0].scrollIntoView({block:'center'});", btn); } catch (Exception ignore) {}
        btn.click();

        // Wait for either success or error bar to render (whichever comes first)
        try {
            getWait().until(d -> isSuccessBarVisible() || isErrorBarVisible());
        } catch (Exception ignore) {}
    }

    private boolean isSuccessBarVisible() {
        try {
            return drv.findElements(By.cssSelector(".bar-notification.success")).size() > 0;
        } catch (Exception e) { return false; }
    }

    private boolean isErrorBarVisible() {
        try {
            return drv.findElements(By.cssSelector(".bar-notification.error, .message-error, .validation-summary-errors")).size() > 0;
        } catch (Exception e) { return false; }
    }

    private boolean isValidationBarVisibleWithText(String contains) {
        try {
            List<WebElement> bars = new ArrayList<>();
            bars.addAll(drv.findElements(By.cssSelector(".bar-notification.error")));
            bars.addAll(drv.findElements(By.cssSelector(".message-error, .validation-summary-errors")));
            String needle = contains == null ? "" : contains.toLowerCase();
            for (WebElement b : bars) {
                if (b.getText() != null && b.getText().toLowerCase().contains(needle)) return true;
            }
        } catch (Exception ignore) {}
        return false;
    }

    private void closeSuccessBarIfPresent() {
        try {
            WebElement success = drv.findElement(By.cssSelector(".bar-notification.success"));
            List<WebElement> close = success.findElements(By.cssSelector(".close"));
            if (!close.isEmpty()) close.get(0).click();
        } catch (Exception ignore) {}
    }
}
