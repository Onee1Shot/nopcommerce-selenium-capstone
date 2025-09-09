Feature: Add Jewelry product to cart

  Background:
    Given I am on the homepage

  Scenario: Add a product from Jewelry to the cart
    When I navigate to category "Jewelry"
    And I open the first product details page
    And I configure options if required and add the product to the cart
    Then the shopping cart should show 1 item
