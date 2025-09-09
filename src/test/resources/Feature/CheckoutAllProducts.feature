Feature: Checkout with all products

  Background:
    Given I am on the homepage

  Scenario: Add one product from each category and complete checkout
    When I add one product from each supported category to the cart
    And I open the shopping cart
    And I accept the terms of service and proceed to checkout
    And I choose to checkout as "Guest"
    And I enter billing details
      | FirstName | Om                                                     |
      | LastName  | Sahu                                                   |
      | Email     | omp9819@gmail.com                                      |
      | Company   | wipro                                                  |
      | Country   | India                                                  |
      | State     | Odisha                                                 |
      | City      | Berhampur                                              |
      | Address1  | House No. 30, Biswanath Trust Road, Kanapalli          |
      | Zip       | 760001                                                 |
      | Phone     | 07978798704                                            |
      | Fax       | 798785                                                 |
    And I select shipping method "Ground"
    And I select payment method "Check / Money Order"
    Then I should see an order confirmation with a number
