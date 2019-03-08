package gradle.cucumber;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;

import static org.junit.Assert.fail;

public class Steps {

    public static WebDriver chrome;
    public static WebDriverWait wait;
    public static WebDriverWait waitUpload;
    public static final String CHROME_DRIVER_PATH = "Driver/chromedriver.exe";

    @Given("^I have a gmail account$")
    public void setupSeleniumDriverForTests() {
        setupSeleniumDriver();
    }

    @When("I login with username (.*) and password (.*)")
    public void gmailLoginUsernamePassword(String username, String password) {

        chrome.get("https://mail.google.com");
        WebElement emailElement = chrome.findElement(By.id("identifierId"));
        emailElement.click();
        emailElement.sendKeys(username);
        chrome.findElement(By.id("identifierNext")).click();
        WebElement passwordElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
        passwordElement.sendKeys(password);
        chrome.findElement(By.id("passwordNext")).click();

    }

    @And("^I click on the compose button$")
    public void clickComposeButton() {
        WebElement composeButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@role='button' and text()='Compose']")));
        composeButton.click();
    }

    @And("I send the email to (.*) with subject (.*)")
    public void setEmailAddressAndSubject(String emailAddress, String subject) {
        WebElement recipientAddressElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@name='to' and @aria-label='To']")));
        recipientAddressElement.sendKeys(emailAddress);

        WebElement subjectElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='subjectbox' and @aria-label='Subject']")));
        subjectElement.sendKeys(subject);

        WebElement bodyElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-label='Message Body']")));
        bodyElement.sendKeys("Email sent by selenium");
    }

    @And("I attach an image with name (.*)")
    public void attachImage(String imageName) throws IOException {
        WebElement attachFileElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@command='Files' and @aria-label='Attach files']")));
        attachFileElement.click();

        String cmd = "Images/AttachFile.exe Images/" + imageName;

        Runtime.getRuntime().exec(cmd);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='attach' and @type='hidden']")));
    }

    @And("I attach a large image with name (.*)")
    public void attachLargeImage(String imageName) throws IOException {
        WebElement attachFileElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@command='Files' and @aria-label='Attach files']")));
        attachFileElement.click();

        String cmd = "Images/AttachFile.exe Images/" + imageName;

        Runtime.getRuntime().exec(cmd);

        WebElement googleDriveLink = waitUpload.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@aria-label='InvalidImage.jpg']")));
        String href = googleDriveLink.getAttribute("href");
        System.out.println(href);
    }

    @And("^I click the send button$")
    public void sendEmail() {
        WebElement sendButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@role='button' and @aria-label='Send \u202A(Ctrl-Enter)\u202C']")));
        sendButton.click();
    }

    @And("I allow share access to the google drive link")
    public void giveAccess() throws Exception{
        chrome.switchTo().frame(13);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("span")));
        WebElement accessLink = chrome.findElements(By.tagName("span")).get(2);
        accessLink.click();
        chrome.switchTo().defaultContent();
    }

    @Then("The email should be sent successfully")
    public void closeChrome() {
        if(checkIfEmailSent()) {
            logout();
            chrome.manage().deleteAllCookies();
        } else {
            fail("Email was not sent successfully");
        }
    }

    private void logout() {
        WebElement accountLabel = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[@aria-label='Google Account: Selenium Test  \n" +
                "(selenium662@gmail.com)']")));
        accountLabel.click();
        WebElement signoutButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//a[text()='Sign out']")));
        signoutButton.click();
    }

    private boolean checkIfEmailSent() {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[@class='bAq' and text()='Message sent.']")));
        } catch(Exception e) {
            return false;
        }
        return true;
    }

    private void setupSeleniumDriver() {
        if(chrome == null) {
            System.out.println("Setting up chrome driver");
            System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
            chrome = new ChromeDriver();
            wait = new WebDriverWait(chrome, 15);
            waitUpload = new WebDriverWait(chrome, 100);
        }
    }
}
