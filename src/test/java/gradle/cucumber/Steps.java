package gradle.cucumber;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

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
    public static Gmail service;
    public List<String> SCOPES;
    public final String CHROME_DRIVER_PATH = "Driver/chromedriver.exe";
    private final String APPLICATION_NAME = "Gmail API Java Quickstart";
    private final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private final String TOKENS_DIRECTORY_PATH = "tokens";
    private final String CREDENTIALS_FILE_PATH = "src/test/resources/credentials.json";

    @Given("^I have a gmail account$")
    public void setupSeleniumDriverForTests() throws Exception {
        setupSeleniumDriver();
        SCOPES = new ArrayList<>();
        SCOPES.add(GmailScopes.GMAIL_COMPOSE);
        SCOPES.add(GmailScopes.GMAIL_SEND);

        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        service = new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
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

    /**
     * Creates an authorized Credential object.
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
        // Load client secrets.
        File file = new File(CREDENTIALS_FILE_PATH);
        InputStream in = new FileInputStream(file);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }
}
