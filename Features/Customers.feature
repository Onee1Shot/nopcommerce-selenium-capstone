Feature:Customer
Scenario: Add New Customer
 Given User Launch Chrome browser
    When User opens URL "https://admin-demo.nopcommerce.com/login"
    And User enters Email as "admin@yourstore.com" and Password as "admin"
    And Click on Login
    Then User can view Dashboard
    When User click on customers Menu
    And click on customers Menu Item
    And click on Add new button
    Then User can view Add new customer page
    When User enters customer info
    When click on Save button
    Then User can view confirmation message "The new customer has been added successfully."
    And close browser 


Scenario: Search Customer by Email
Given User Launch Chrome browser
     When User opens URL "https://admin-demo.nopcommerce.com/login"
     And User enters Email as "admin@yourstore.com" and Password as "admin"
     And Click on Login
     Then User can view Dashboard
     When User click on customers Menu
     And click on customers Menu Item
     And Enter customers mail
     When click on search button
     And close browser