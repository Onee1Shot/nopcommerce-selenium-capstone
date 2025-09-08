Feature: Login

  Scenario: Successful Login with Valid Credentials
    Given User Launch Chrome browser
    When User opens URL "https://demo.nopcommerce.com/login"
    And User enters Email as "admin@yourstore.com" and Password as "admin"
    And Click on Login
    Then Page Title should be "Dashboard / nopCommerce administration"
    When User click on Log out link
    Then Page Title should be "nopCommerce demo store. Login"
    And close browser

Scenario: Successful Login with Valid Credentials DOT
    Given User Launch Chrome browser
    When User opens URL "https://admin-demo.nopcommerce.com/"
    And User enters Email as "<email>" and Password as "<password>"
    And Click on Login
    Then Page Title should be "Dashboard / nopCommerce administration"
    When User click on Log out link
    Then Page Title should be "nopCommerce demo store. Login"
    And close browser

Examples:
|email|password|
|admin@yourstore.com|admin|
|test@yourstory.com|admin|