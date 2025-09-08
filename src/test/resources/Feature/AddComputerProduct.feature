Feature: Add Computers product to cart

  Background:
    Given I am on the homepage

  Scenario Outline: Add a product from the Computers > <Subcategory> subcategory to the cart
    When I navigate to category "Computers"
    And I open the "<Subcategory>" subcategory if present, otherwise the first subcategory
    And I open the first product details page
    And I configure options if required and add the product to the cart
    Then the shopping cart should show 1 item

  Examples:
    | Subcategory |
    | Desktops    |
    | Notebooks   |
    | Software |
