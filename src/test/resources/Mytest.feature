Feature: Reset functionality on login page of Application


  Scenario: Send an email with image attachment

    Given I have a gmail account
    When I login with username "selenium662" and password "test_user1"
    And I click on the compose button
    And I send the email to "owais.khan2@mail.mcgill.ca" with subject "Test Email"
    And I attach an image
    And I click the send button
    Then The email should be sent successfully