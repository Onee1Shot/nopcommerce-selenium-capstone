Feature: Add Electronics product to cart

  Background:
    Given I am on the homepage
    And I empty the shopping cart

  Scenario: Add a product from Electronics to the cart
    When I navigate to category "Electronics"
    And I open the first product details page
    And I configure options if required and add the product to the cart
    Then the shopping cart should show 1 item
