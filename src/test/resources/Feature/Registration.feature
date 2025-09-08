Feature: User Registration

  Background:
    Given I am on the homepage

  Scenario Outline: Successful registration with valid details
    When I navigate to "Register" page
    And I select gender "<Gender>"
    And I fill the registration form with:
      | FirstName       | <FirstName>       |
      | LastName        | <LastName>        |
      | Email           | <Email>           |
      | Company         | <Company>         |
      | Newsletter      | <Newsletter>      |
      | Password        | <Password>        |
      | ConfirmPassword | <ConfirmPassword> |
    And I submit the registration form
    Then I should see "Your registration completed"

  Examples:
      | FirstName | LastName | Email | Company | Gender | Newsletter | Password | ConfirmPassword |
      | John | Doe | john.doe11@gmail.com | AcmeCorp | Male | Yes | Pass@123 | Pass@123 |
      | Jane | Smith | jane.smith21@gmail.com | TechSoft | Female | No | Pass@456 | Pass@456 |
      | Om Prakash | Sahu | omp9819@gmail.com | Wipro | Male | Yes | rootroot | rootroot |
