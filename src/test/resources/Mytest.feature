Feature: Reset functionality on login page of Application


  Scenario Outline: Send an email with image attachment

    Given I have a gmail account
    When I am logged in as "selenium662" with password "test_user1"
    And I want to send an email to <to> with subject <subject>
    And I attach an image with name <image>
    And I send the email
    Then The email with the attachment should be sent successfully

    Examples:
    |             to               |      subject     | image           |
    | owais.khan2@mail.mcgill.ca   | Test Subject 1   | Chiron.jpg      |
    | owais.khan2@mail.mcgill.ca   | Test Subject 2   | Ferrari.jpg     |
    | owais.khan2@mail.mcgill.ca   | Test Subject 3   | Lamborghini.jpg |
    | owais.khan2@mail.mcgill.ca   | Test Subject 4   | McLaren.jpg     |
    | owais.khan2@mail.mcgill.ca   | Test Subject 5   | Zonda.jpg       |