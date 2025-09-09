package pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public abstract class BasePage {

    private static WebDriver DRIVER;
    private static WebDriverWait WAIT;

    public static WebDriver initializeDriver(String browser) {
        if (DRIVER != null) return DRIVER;

        String b = browser == null ? "chrome" : browser.trim().toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", "false"));

        switch (b) {
            case "edge" -> {
                WebDriverManager.edgedriver().setup();
                DRIVER = new EdgeDriver();
            }
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                DRIVER = new FirefoxDriver();
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                opts.addArguments("--disable-notifications", "--disable-infobars", "--start-maximized");
                if (headless) opts.addArguments("--headless=new");
                DRIVER = new ChromeDriver(opts);
            }
        }
        attach(DRIVER, Duration.ofSeconds(20));
        return DRIVER;
    }

    public static void quitDriver() {
        try {
            if (DRIVER != null) DRIVER.quit();
        } catch (Exception ignore) { }
        DRIVER = null;
        WAIT = null;
    }

    public static void attach(WebDriver driver, Duration timeout) {
        DRIVER = driver;
        WAIT = new WebDriverWait(driver, timeout == null ? Duration.ofSeconds(20) : timeout);
        WAIT.ignoring(StaleElementReferenceException.class);
    }

    public static WebDriver getDriver() { return DRIVER; }

    public static WebDriverWait getWait() {
        if (WAIT == null) WAIT = new WebDriverWait(DRIVER, Duration.ofSeconds(20));
        return WAIT;
    }

    public static void clickHeaderLink(String visibleText) {
        By link = By.linkText(visibleText);
        getWait().until(ExpectedConditions.elementToBeClickable(link)).click();
    }

    public static int getCartCount() {
        try {
            String[] selectors = new String[]{
                    "a.ico-cart .cart-qty", ".header-links .cart-qty", ".header .cart-qty", ".shopping-cart-link .cart-qty"
            };
            for (String css : selectors) {
                List<WebElement> els = DRIVER.findElements(By.cssSelector(css));
                if (!els.isEmpty() && els.get(0).isDisplayed()) {
                    String t = els.get(0).getText();
                    if (t == null || t.isBlank()) continue;
                    String digits = t.replaceAll("\\D+", "");
                    if (!digits.isEmpty()) return Integer.parseInt(digits);
                }
            }
        } catch (Exception ignored) {}
        return 0;
    }

    public static boolean isSuccessBarVisible() {
        try {
            List<WebElement> ok = DRIVER.findElements(By.cssSelector(".bar-notification.success"));
            return !ok.isEmpty() && ok.get(0).isDisplayed();
        } catch (Exception e) { return false; }
    }

    public static void waitForCartCountToIncreaseOrSuccess(int before) {
        getWait().until(d -> {
            int now = getCartCount();
            if (now > before) return true;
            if (isSuccessBarVisible()) return true;
            List<WebElement> fly = d.findElements(By.cssSelector(".mini-shopping-cart, .flyout-cart"));
            return !fly.isEmpty() && fly.get(0).isDisplayed();
        });
    }

    public static void openCartPage() {
        clickHeaderLink("Shopping cart");
        getWait().until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".order-summary-content, .cart")));
    }

    public static void emptyCartIfAny() {
        openCartPage();
        List<WebElement> rows = getDriver().findElements(
                By.cssSelector(".cart tbody tr, .order-summary-content .cart-item-row, .table-wrapper tbody tr"));
        if (rows.isEmpty()) return;

        List<WebElement> removeChecks = getDriver().findElements(By.cssSelector("input[name='removefromcart']"));
        if (!removeChecks.isEmpty()) {
            for (WebElement c : removeChecks) {
                try { if (!c.isSelected()) c.click(); } catch (Exception ignore) {}
            }
            By update = By.cssSelector("button[name='updatecart'], input[name='updatecart'], .update-cart-button");
            getWait().until(ExpectedConditions.elementToBeClickable(update)).click();
        } else {
            By removeBtn = By.cssSelector(".remove-btn, button.remove-btn, .cart .remove-product");
            while (true) {
                List<WebElement> btns = getDriver().findElements(removeBtn);
                if (btns.isEmpty()) break;
                int before = btns.size();
                try { btns.get(0).click(); } catch (Exception ignore) {}
                getWait().until(d -> d.findElements(removeBtn).size() < before);
            }
        }

        getWait().until(d ->
                getCartCount() == 0 ||
                !d.findElements(By.cssSelector(".order-summary-content .no-data, .cart-empty, .page-body .no-data")).isEmpty()
        );
        closeSuccessBarIfPresent();
    }

    public static void acceptAlertIfPresent() {
        try {
            getWait().withTimeout(Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent())
                    .accept();
        } catch (Exception ignore) {}
    }

    public static void closeSuccessBarIfPresent() {
        try {
            WebElement success = DRIVER.findElement(By.cssSelector(".bar-notification.success"));
            List<WebElement> close = success.findElements(By.cssSelector(".close"));
            if (!close.isEmpty()) close.get(0).click();
        } catch (Exception ignore) {}
    }

    public static String getBaseUrl() { return "https://demo.nopcommerce.com/"; }
}
