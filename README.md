# Assignment B - ECSE 428

This is the repository for Assignment B of the ECSE 428 - Software Engineering Practices

The goal of this project is to automate acceptance testing of a story related to sending an email with an image attachment.

**Gherkin** is used to describe the automation.

**Selenium** is used to control the application.

The project has been setup using **Gradle**.

## Instructions to Run
Before running the gradle wrapper command ensure that your PATH environment variable has the jdk path in it. For instructions on how to do this click [here](https://www.java.com/en/download/help/path.xml).

Also ensure that the environment variable JAVA_HOME is set. For instructions on how to do this click [here](https://docs.oracle.com/cd/E19182-01/820-7851/inst_cli_jdk_javahome_t/).

For windows users
1) Run "gradlew.bat cucumber"

For linux/MAC users
1) Run "./gradlew cucumber"