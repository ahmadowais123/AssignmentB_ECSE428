Feature: Reset functionality on login page of Application


  Scenario Outline: Send an email with image attachment

    Given I have a gmail account
    When I login with username "selenium662" and password "test_user1"
    And I click on the compose button
    And I send the email to <to> with subject <subject>
    And I attach an image with name <image>
    And I click the send button
    Then The email should be sent successfully

    Examples:
    |             to               |      subject     | image           |
    | owais.khan2@mail.mcgill.ca   | Test Subject 1   | Chiron.jpg      |
    | owais.khan2@mail.mcgill.ca   | Test Subject 2   | Ferrari.jpg     |
    | owais.khan2@mail.mcgill.ca   | Test Subject 3   | Lamborghini.jpg |
    | owais.khan2@mail.mcgill.ca   | Test Subject 4   | McLaren.jpg     |
    | owais.khan2@mail.mcgill.ca   | Test Subject 5   | Zonda.jpg       |