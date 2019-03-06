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

public class Steps {

    public static WebDriver chrome;
    public static WebDriverWait wait;
    public static final String CHROME_DRIVER_PATH = "Driver/chromedriver.exe";

    @Given("^I have a gmail account$")
    public void setupSeleniumDriverForTests() throws Exception {
        setupSeleniumDriver();
    }

    @When("I login with username {string} and password {string}")
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

    @And("I send the email to {string} with subject {string}")
    public void setEmailAddressAndSubject(String emailAddress, String subject) {
        WebElement recipientAddressElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@name='to' and @aria-label='To']")));
        recipientAddressElement.sendKeys(emailAddress);

        WebElement subjectElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='subjectbox' and @aria-label='Subject']")));
        subjectElement.sendKeys(subject);

        WebElement bodyElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-label='Message Body']")));
        bodyElement.sendKeys("Email sent by selenium");
    }

    @And("^I attach an image$")
    public void attachImage() throws Exception {
        WebElement attachFileElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@command='Files' and @aria-label='Attach files']")));
        attachFileElement.click();
        Runtime.getRuntime().exec("Autoit/attach.exe");
        Thread.sleep(5000);
    }

    @And("^I click the submit button$")
    public void sendEmail() {
        WebElement sendButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@role='button' and @aria-label='Send \u202A(Ctrl-Enter)\u202C']")));
        sendButton.click();
    }

    private void setupSeleniumDriver() {
        if(chrome == null) {
            System.out.println("Setting up chrome driver");
            System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
            chrome = new ChromeDriver();
            wait = new WebDriverWait(chrome, 30);
        }
    }
}
