Feature: Sending an email with an image attachment

  #Normal Scenario
  Scenario Outline: Send an email with image attachment

    Given I have a Gmail account
    When I login with username selenium662 and password test_user1
    And I want to send an email to <to> with subject <subject>
    And I attach an image with name <image>
    And I send the email
    Then The email with the attachment should be sent successfully

    Examples:

    |             to               |      subject     | image           |
    | itsabdullahirfan@gmail.com   | Test Subject 1   | Chiron.jpg      |
    | itsmustafairfan@gmail.com    | Test Subject 2   | Ferrari.jpg     |
    | owais.khan2@mail.mcgill.ca   | Test Subject 3   | Lamborghini.jpg |
    | itsirfannawaz@gmail.com      | Test Subject 4   | McLaren.jpg     |
    | gokuiskakarotalso@gmail.com  | Test Subject 5   | Zonda.jpg       |

  #Alternate Scenario
  Scenario Outline: Send an email with an image of large size

    Given I have a Gmail account
    When I login with username selenium662 and password test_user1
    And I want to send an email to <to> with subject <subject>
    And I attach a large image with name <image>
    And I send the email
    And I allow share access to the Google Drive link
    Then The email with the attachment should be sent successfully

    Examples:

    |             to               |         subject		  | 	   image          |
    | itsabdullahirfan@gmail.com   | Large Image Attachment   |   InvalidImage.jpg    |

  #Error Scenario
  Scenario Outline: Send an email with an invalid recipient address

    Given I have a Gmail account
    When I login with username selenium662 and password test_user1
    And I want to send an email to <to> with subject <subject>
    And I attach an image with name <image>
    And I send the email
    Then I should see an error and the mail will not be sent
    
        Examples:

    |   to    |         subject		   | 	  image     |
    | abcdef  | 	Invalid Address    |   Chiron.jpg   |
    