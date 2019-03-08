Feature: Reset functionality on login page of Application


  Scenario Outline: Send an email with image attachment

    Given I have a gmail account
    When I login with username selenium662 and password test_user1
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

  Scenario: Send an email with an image of large size

    Given I have a gmail account
    When I login with username selenium662 and password test_user1
    And I want to send an email to owais.khan2@mail.mcgill.ca with subject Large Image Attachment
    And I attach a large image with name InvalidImage.jpg
    And I send the email
    And I allow share access to the google drive link
    Then The email with the attachment should be sent successfully

  Scenario: Send an email with an invalid recipient address

    Given I have a gmail account
    When I login with username selenium662 and password test_user1
    And I want to send an email to abcdef with subject Invalid Email Address
    And I attach an image with name Chiron.jpg
    And I send the email
    Then I should see an error and the mail will not be sent