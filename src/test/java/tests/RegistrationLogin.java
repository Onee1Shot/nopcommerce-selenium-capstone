//package tests;
//
//import java.io.IOException;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.openqa.selenium.By;
//import org.openqa.selenium.WebElement;
//import org.openqa.selenium.support.ui.ExpectedConditions;
//import org.testng.Assert;
//import org.testng.annotations.*;
//
//import listeners.ExtentReportListener;
//import pages.BasePage;
//import pages.LoginPage;
//import pages.RegistrationPage;
//import utils.ExcelUtils;
//
//@Listeners(listeners.ExtentReportListener.class)
//public class RegistrationLogin {
//
//    private static final Logger logger = LogManager.getLogger(RegistrationLogin.class);
//    private static int stepCounter;
//
//    @BeforeClass
//    @Parameters("baseUrl")
//    public void setup(String baseUrl) {
//        stepCounter = 1;
//        logStep("Launching browser and setting base URL: " + baseUrl);
//        BasePage.initializeDriver(baseUrl);
//
//        // Attach driver to listener
//        ExtentReportListener.setDriver(BasePage.getDriver());
//    }
//
//    protected void logStep(String message) {
//        logger.info("Step " + stepCounter++ + ": " + message);
//    }
//
//    @Test(dataProvider = "registrationData", priority = 1)
//    public void testRegistration(String firstName, String lastName, String email, String company, String gender,
//                                 String newsletter, String password, String confirmPassword) {
//
//        logStep("Navigating to Registration Page");
//        BasePage.getDriver().get(BasePage.getBaseUrl() + "/register");
//
//        RegistrationPage reg = new RegistrationPage(BasePage.getDriver());
//
//        reg.selectGender(gender);
//        reg.enterFirstName(firstName);
//        reg.enterLastName(lastName);
//        reg.enterEmail(email);
//        reg.enterCompany(company);
//        reg.setNewsletterSubscription(newsletter);
//        reg.enterPassword(password);
//        reg.enterConfirmPassword(confirmPassword);
//        reg.clickRegister();
//
//        logStep("Validating Registration Success");
//        BasePage.getWait().until(ExpectedConditions.visibilityOfElementLocated(By.className("result")));
//        Assert.assertTrue(BasePage.getDriver().getPageSource().contains("Your registration completed"),
//                "Registration failed for: " + email);
//
//        logoutUser();
//    }
//
//    @Test(dataProvider = "loginData", priority = 2)
//    public void testLogin(String email, String password) {
//        logStep("Navigating to Login Page");
//        BasePage.getDriver().get(BasePage.getBaseUrl() + "/login");
//
//        LoginPage loginPage = new LoginPage(BasePage.getDriver());
//
//        loginPage.enterEmail(email);
//        loginPage.enterPassword(password);
//        loginPage.clickLogin();
//
//        BasePage.getWait().until(ExpectedConditions.visibilityOfElementLocated(By.linkText("My account")));
//        Assert.assertTrue(BasePage.getDriver().getPageSource().contains("My account"),
//                "Login failed for user: " + email);
//
//        logStep("Login successful for user: " + email);
//        logoutUser();
//    }
//
//    private void logoutUser() {
//        try {
//            WebElement logoutLink = BasePage.getDriver().findElement(By.linkText("Log out"));
//            if (logoutLink.isDisplayed()) {
//                logoutLink.click();
//                logStep("Logout successful");
//
//                BasePage.getDriver().manage().deleteAllCookies();
//                BasePage.getDriver().get(BasePage.getBaseUrl());
//            }
//        } catch (Exception e) {
//            logger.warn("Logout link not found: " + e.getMessage());
//        }
//    }
//
//    @AfterClass
//    public void tearDown() {
//        logStep("Closing browser");
//        BasePage.quitDriver();
//    }
//
//    @DataProvider(name = "registrationData")
//    public Object[][] getRegistrationData() throws IOException {
//        String filePath = System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";
//        return ExcelUtils.getTestData(filePath, "Sheet1");
//    }
//
//    @DataProvider(name = "loginData")
//    public Object[][] getLoginData() throws IOException {
//        String filePath = System.getProperty("user.dir") + "/src/test/resources/TestData.xlsx";
//        return ExcelUtils.getTestData(filePath, "Sheet2");
//    }
//}
