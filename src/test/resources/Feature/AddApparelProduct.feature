Feature: Add Apparel product to cart

  Background:
    Given I am on the homepage
    And I empty the shopping cart

  # Try a few subcategories; the steps will pick the right page structure
  Scenario Outline: Add a product from Apparel > <Subcategory> to the cart
    When I navigate to category "Apparel"
    And I open the "<Subcategory>" subcategory if present, otherwise the first subcategory
    And I open the first product details page
    And I configure options if required and add the product to the cart
    Then the shopping cart should show 1 item

  Examples:
    | Subcategory |
    | Clothing    |
    | Shoes       |
    | Accessories |
