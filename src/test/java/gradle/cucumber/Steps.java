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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.api.services.gmail.model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.apache.commons.codec.binary.Base64;
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

        File image = new File("Images/download.jpeg");

        MimeMessage message = createEmailWithAttachment("abdullah.irfan@mail.mcgill.ca",
                "selenium662@gmail.com",
                "Test Subject",
                "My name is Owais",
                image);

        sendMessage(service, "me", message);
    }

//    @When("I login with username {string} and password {string}")
//    public void gmailLoginUsernamePassword(String username, String password) {
//
//        chrome.get("https://mail.google.com");
//        WebElement emailElement = chrome.findElement(By.id("identifierId"));
//        emailElement.click();
//        emailElement.sendKeys(username);
//        chrome.findElement(By.id("identifierNext")).click();
//        WebElement passwordElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("password")));
//        passwordElement.sendKeys(password);
//        chrome.findElement(By.id("passwordNext")).click();
//
//    }
//
//    @And("^I click on the compose button$")
//    public void clickComposeButton() {
//        WebElement composeButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@role='button' and text()='Compose']")));
//        composeButton.click();
//    }
//
//    @And("I send the email to {string} with subject {string}")
//    public void setEmailAddressAndSubject(String emailAddress, String subject) {
//        WebElement recipientAddressElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//textarea[@name='to' and @aria-label='To']")));
//        recipientAddressElement.sendKeys(emailAddress);
//
//        WebElement subjectElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//input[@name='subjectbox' and @aria-label='Subject']")));
//        subjectElement.sendKeys(subject);
//
//        WebElement bodyElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@aria-label='Message Body']")));
//        bodyElement.sendKeys("Email sent by selenium");
//    }
//
//    @And("^I attach an image$")
//    public void attachImage() throws Exception {
//        WebElement attachFileElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@command='Files' and @aria-label='Attach files']")));
//        attachFileElement.click();
//        Thread.sleep(5000);
//    }
//
//    @And("^I click the submit button$")
//    public void sendEmail() {
//        WebElement sendButton = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//div[@role='button' and @aria-label='Send \u202A(Ctrl-Enter)\u202C']")));
//        sendButton.click();
//    }

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

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to email address of the receiver
     * @param from email address of the sender, the mailbox account
     * @param subject subject of the email
     * @param bodyText body text of the email
     * @return the MimeMessage to be used to send email
     * @throws MessagingException
     */
    public static MimeMessage createEmail(String to,
                                          String from,
                                          String subject,
                                          String bodyText)
            throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    /**
     * Create a message from an email.
     *
     * @param emailContent Email to be set to raw of message
     * @return a message containing a base64url encoded email
     * @throws IOException
     * @throws MessagingException
     */
    public static Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    /**
     * Send an email from the user's mailbox to its recipient.
     *
     * @param service Authorized Gmail API instance.
     * @param userId User's email address. The special value "me"
     * can be used to indicate the authenticated user.
     * @param emailContent Email to be sent.
     * @return The sent message
     * @throws MessagingException
     * @throws IOException
     */
    public static Message sendMessage(Gmail service,
                                      String userId,
                                      MimeMessage emailContent)
            throws MessagingException, IOException {
        Message message = createMessageWithEmail(emailContent);
        message = service.users().messages().send(userId, message).execute();

        System.out.println("Message id: " + message.getId());
        System.out.println(message.toPrettyString());
        return message;
    }

    /**
     * Create a MimeMessage using the parameters provided.
     *
     * @param to Email address of the receiver.
     * @param from Email address of the sender, the mailbox account.
     * @param subject Subject of the email.
     * @param bodyText Body text of the email.
     * @param file Path to the file to be attached.
     * @return MimeMessage to be used to send email.
     * @throws MessagingException
     */
    public static MimeMessage createEmailWithAttachment(String to,
                                                        String from,
                                                        String subject,
                                                        String bodyText,
                                                        File file)
            throws MessagingException, IOException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);

        email.setFrom(new InternetAddress(from));
        email.addRecipient(javax.mail.Message.RecipientType.TO,
                new InternetAddress(to));
        email.setSubject(subject);

        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(bodyText, "text/plain");

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file);

        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());

        multipart.addBodyPart(mimeBodyPart);
        email.setContent(multipart);

        return email;
    }
}
