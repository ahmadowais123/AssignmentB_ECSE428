# Assignment B - ECSE 428

This is the repository for Assignment B of the ECSE 428 - Software Engineering Practices

The goal of this project is to automate acceptance testing of a story related to sending an email with an image attachment.

**Gherkin** is used to describe the automation.

**Selenium** is used to control the application.

**AutoIt** is used to attach an image to the email body. Inorder for the tests to run properly AutoIt needs to be installed on the machine being used to run the tests. To download the software click [here](https://www.autoitscript.com/site/autoit/downloads/).

The project has been setup using **Gradle**.

## Instructions to Run
For windows users
1) Run "gradlew.bat cucumber"

For linux/MAC users
1) Run "./gradlew cucumber"