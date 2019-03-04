Feature: Reset functionality on login page of Application


  Scenario: Send an email with image attachment

    Given I open chrome and navigate to gmail
    When I enter my username and password
    And I successfully login
    And I compose a new email
    And I attach an image
    And I click the submit button
    Then The email should be sent successfully