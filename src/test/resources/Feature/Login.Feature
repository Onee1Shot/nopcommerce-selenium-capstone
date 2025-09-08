Feature: User Login

  Background:
    Given I am on the homepage

  Scenario Outline: Login with valid credentials
    When I navigate to "Login" page
    And I login with email "<Email>" and password "<Password>"
    Then I should be logged in successfully

  Examples:
      | Email | Password |
      | john.doe11@gmail.com | Pass@123 |
      | jane.smith21@gmail.com | Pass@456 |
      | omp9819@gmail.com | rootroot |
