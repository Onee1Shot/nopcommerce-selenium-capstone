package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.time.Duration;
import java.util.List;

public class CatalogPage extends BasePage {

    private final WebDriver drv;

    public CatalogPage(WebDriver driver) {
        this.drv = driver;
    }

    public void openCategory(String categoryName) {
        // Use top menu when possible, fallback to link text
        By topMenu = By.cssSelector("ul.top-menu.notmobile a");
        try {
            getWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(topMenu));
            List<WebElement> links = drv.findElements(topMenu);
            boolean clicked = false;
            for (WebElement a : links) {
                if (a.getText().trim().equalsIgnoreCase(categoryName)) {
                    getWait().until(ExpectedConditions.elementToBeClickable(a)).click();
                    clicked = true;
                    break;
                }
            }
            if (!clicked) {
                // fallback to plain link text
                drv.findElement(By.linkText(categoryName)).click();
            }
        } catch (Exception e) {
            // final fallback
            drv.findElement(By.linkText(categoryName)).click();
        }

        // Wait for either subcategories or product grid
        waitForCategoryOrProducts();
    }

    public void openSubcategoryOrFirst(String desiredSubcategory) {
        // If products already visible, do nothing
        if (hasProducts()) return;

        // Gather subcategory links on category landing
        List<WebElement> subs = drv.findElements(By.cssSelector(".sub-category-item .title a, .category-grid .item .title a"));
        if (subs.isEmpty()) {
            // No explicit subcategories; nothing to do
            return;
        }

        WebElement toClick = subs.get(0);
        if (desiredSubcategory != null && !desiredSubcategory.isBlank()) {
            String want = desiredSubcategory.trim().toLowerCase();
            for (WebElement s : subs) {
                if (s.getText().trim().toLowerCase().equals(want)) {
                    toClick = s;
                    break;
                }
            }
        }

        getWait().until(ExpectedConditions.elementToBeClickable(toClick)).click();
        waitForCategoryOrProducts();
    }

    public ProductPage openFirstProductDetails() {
        // Ensure we are on a product grid; if not, try first subcategory automatically
        if (!hasProducts()) {
            List<WebElement> subs = drv.findElements(By.cssSelector(".sub-category-item .title a, .category-grid .item .title a"));
            if (!subs.isEmpty()) {
                getWait().until(ExpectedConditions.elementToBeClickable(subs.get(0))).click();
                waitForCategoryOrProducts();
            }
        }

        // Find the first product title link and open details
        By productTitleLinks = By.cssSelector(".product-item .product-title a, .item-box .product-title a");
        WebElement first = getWait().until(ExpectedConditions.elementToBeClickable(productTitleLinks));
        first.click();

        // Wait for product details page (Add to cart button present)
        waitForProductDetails();
        return new ProductPage(drv);
    }

    private void waitForCategoryOrProducts() {
        // Either subcategory tiles OR products should appear
        By subs = By.cssSelector(".sub-category-item .title a, .category-grid .item .title a");
        By products = By.cssSelector(".product-item, .item-box .product-title a");
        getWait().withTimeout(Duration.ofSeconds(10))
                .until(d -> !drv.findElements(subs).isEmpty() || !drv.findElements(products).isEmpty());
    }

    private boolean hasProducts() {
        return !drv.findElements(By.cssSelector(".product-item, .item-box .product-title a")).isEmpty();
    }

    private void waitForProductDetails() {
        By addToCart = By.cssSelector("button[id^='add-to-cart-button'], input[id^='add-to-cart-button'], .add-to-cart-button");
        getWait().until(ExpectedConditions.presenceOfElementLocated(addToCart));
    }
}
