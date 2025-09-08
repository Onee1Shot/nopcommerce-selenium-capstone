package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
// add this import at the top:
import org.openqa.selenium.By;

import java.time.Duration;
import java.util.List;

public class BasePage {
	protected static WebDriver driver;
	protected static WebDriverWait wait;
	private static String baseUrl;

	// Initialize driver and base URL
	public static void initializeDriver(String url) {
		if (driver == null) {
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, Duration.ofSeconds(10));
			baseUrl = url;
		}
	}
//	public static int getCartCount() {
//	    try {
//	        String qty = driver.findElement(By.cssSelector("span.cart-qty")).getText(); // e.g. "(1)"
//	        String digits = qty.replaceAll("[^0-9]", "");
//	        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
//	    } catch (Exception e) {
//	        return 0;
//	    }
//	}

	// Get WebDriver
	public static WebDriver getDriver() {
		return driver;
	}

	// Get WebDriverWait
	public static WebDriverWait getWait() {
		return wait;
	}

	// Get Base URL
	public static String getBaseUrl() {
		return baseUrl;
	}

//    public static void clickHeaderLink(String linkText) {
//        driver.findElement(By.linkText(linkText)).click();
//    }
	public static void clickHeaderLink(String label) {
		String key = label == null ? "" : label.trim().toLowerCase();

		// Prefer stable header icon classes; fall back to visible text
		By locator;
		switch (key) {
		case "login":
		case "log in":
			locator = By.cssSelector("a.ico-login");
			break;
		case "logout":
		case "log out":
			locator = By.cssSelector("a.ico-logout");
			break;
		case "register":
			locator = By.cssSelector("a.ico-register");
			break;
		case "my account":
			locator = By.cssSelector("a.ico-account");
			break;
		case "wishlist":
			locator = By.cssSelector("a.ico-wishlist");
			break;
		case "shopping cart":
			locator = By.cssSelector("a.ico-cart");
			break;
		default:
			// fallback to the literal text you passed in
			locator = By.linkText(label);
		}

		WebElement el = getWait().until(ExpectedConditions.elementToBeClickable(locator));
		el.click();

		// If someone passed "Login" (no space) but the site uses "Log in",
		// the css selector above already handled it. If the default path ran,
		// add a soft fallback to the actual link text.
		if (key.equals("login")) {
			try {
				getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Log in"))).click();
			} catch (Exception ignored) {
			}
		} else if (key.equals("logout")) {
			try {
				getWait().until(ExpectedConditions.elementToBeClickable(By.linkText("Log out"))).click();
			} catch (Exception ignored) {
			}
		}
	}
	
	public static void emptyCartIfAny() {
	    try {
	        // If cart is already empty, nothing to do
	        if (getCartCount() == 0) return;

	        // Open cart page
	        getWait().until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.ico-cart"))).click();

	        // Try the standard remove checkboxes
	        List<WebElement> removeBoxes = getDriver().findElements(By.cssSelector("input.remove-from-cart"));
	        if (!removeBoxes.isEmpty()) {
	            for (WebElement cb : removeBoxes) {
	                if (!cb.isSelected()) cb.click();
	            }
	            getDriver().findElement(By.name("updatecart")).click();
	        } else {
	            // Fallback: set quantities to 0 then update
	            List<WebElement> qtyInputs = getDriver().findElements(By.cssSelector("input.qty-input"));
	            if (!qtyInputs.isEmpty()) {
	                for (WebElement q : qtyInputs) { q.clear(); q.sendKeys("0"); }
	                getDriver().findElement(By.name("updatecart")).click();
	            }
	        }

	        // Wait until the header badge shows 0
	        getWait().until(d -> getCartCount() == 0);

	        // Go back to homepage (logo click or direct URL)
	        try {
	            getDriver().findElement(By.cssSelector("img[alt*='nopCommerce']")).click();
	        } catch (Exception e) {
	            getDriver().get(getBaseUrl());
	        }
	    } catch (Exception ignore) { /* keep tests flowing even if cart already empty */ }
	}

	// --- Cart Helpers ---

	/** Reads the cart quantity badge (top-right link "Shopping cart (X)") */
	public static int getCartCount() {
	    try {
	        WebElement cartLink = getDriver().findElement(By.cssSelector("a.ico-cart"));
	        String txt = cartLink.getText();  // e.g. "Shopping cart (2)"
	        if (txt != null && txt.contains("(") && txt.contains(")")) {
	            String inside = txt.substring(txt.indexOf("(") + 1, txt.indexOf(")")).trim();
	            return Integer.parseInt(inside);
	        }
	    } catch (Exception e) {
	        // fallback if badge missing or not parsable
	    }
	    return 0;
	}

	/** Wait until the cart count increases compared to previous value */
	public static void waitForCartCountToIncrease(int before) {
	    getWait().until(driver -> {
	        int now = getCartCount();
	        return now > before;
	    });
	}

	// Quit driver
	public static void quitDriver() {
		if (driver != null) {
			driver.quit();
			driver = null;
			wait = null;
			baseUrl = null;
		}
	}
}
