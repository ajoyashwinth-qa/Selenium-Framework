Feature: Home page

  @smoke @regression
  Scenario: Validate example.com heading
    Given I open the homepage
    When I read the main heading
    Then I should see a non-empty heading