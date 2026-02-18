Feature: Product Purchase - Complete Purchase Flow
  As a web user
  I want to select the cheapest product, add to cart and complete purchase
  So that I can buy products through the web application

  @purchase @smoke
  Scenario: Complete purchase flow selecting the lowest price product
    Given the user is logged in to the website
    When the user selects "Price (low to high)" sorting
    And the user verifies the first item has the lowest price
    And the user adds the first item to cart
    Then the Remove button should be visible
    And the shopping cart badge should be incremented
    When the user opens the shopping cart
    Then the product should be in the cart
    When the user proceeds to checkout
    And the user fills shipping information
    And the user continues to payment review
    Then the product should be in the overview
    When the user completes the order
    Then the order confirmation should be displayed
    When the user goes back home
    Then the user should be on the products page

  @purchase @cart @low-price
  Scenario: Add lowest priced product, verify cart badge, remove and logout
    Given the user is logged in to the website
    When the user selects "Price (low to high)" sorting
    And the user verifies the first item has the lowest price
    And the user stores the item at position 1
    And the user adds the item at position 1 to the cart
    Then the shopping cart badge should be "1"
    When the user opens the shopping cart
    And the user removes all items from the cart
    And the user logs out

  @purchase @cart @low-high
  Scenario: Add lowest and highest priced items, verify cart badge, remove and logout
    Given the user is logged in to the website
    When the user selects "Price (low to high)" sorting
    And the user verifies the first item has the lowest price
    And the user stores the item at position 1
    And the user adds the item at position 1 to the cart
    Then the shopping cart badge should be "1"
    When the user switches sorting to "Price (high to low)"
    And the user adds the item at position 1 to the cart
    Then the shopping cart badge should be "2"
    When the user opens the shopping cart
    And the user removes all items from the cart
    And the user logs out

  @purchase @cart @persist
  Scenario: Add lowest and highest priced items, logout, login again, and keep cart count
    Given the user is logged in to the website
    When the user selects "Price (low to high)" sorting
    And the user verifies the first item has the lowest price
    And the user stores the item at position 1
    And the user adds the item at position 1 to the cart
    Then the shopping cart badge should be "1"
    When the user switches sorting to "Price (high to low)"
    And the user adds the item at position 1 to the cart
    Then the shopping cart badge should be "2"
    When the user logs out
    And the user logs in again
    Then the shopping cart badge should be "2"
