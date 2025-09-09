package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends BasePage {

    private final WebDriver drv;
    public CartPage(WebDriver driver) { this.drv = driver; }

    private final By cartLink      = By.cssSelector("a.ico-cart");
    private final By termsCheckbox = By.id("termsofservice");
    private final By checkoutBtn   = By.id("checkout");

    private final By cartShell     = By.cssSelector(".order-summary-content, .cart");
    private final By tosErrorBar   = By.cssSelector(".bar-notification.error");
    private final By tosCloseBtn   = By.cssSelector(".bar-notification.error .close");

    public void openCart() {
        getWait().until(ExpectedConditions.elementToBeClickable(cartLink)).click();
        getWait().until(ExpectedConditions.visibilityOfElementLocated(cartShell));
    }

    public void ensureTermsAccepted() {
        try {
            WebElement cb = getWait().until(ExpectedConditions.presenceOfElementLocated(termsCheckbox));
            if (!cb.isSelected()) cb.click();
        } catch (Exception ignore) {}
    }

    public void clickCheckout() {
        WebElement btn = getWait().until(ExpectedConditions.elementToBeClickable(checkoutBtn));
        btn.click();

        // If a red error bar pops (terms not ticked), close, tick, retry once
        try {
            if (!drv.findElements(tosErrorBar).isEmpty()) {
                drv.findElement(tosCloseBtn).click();
                ensureTermsAccepted();
                getWait().until(ExpectedConditions.elementToBeClickable(checkoutBtn)).click();
            }
        } catch (Exception ignore) {}
    }
}
