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

    public static String toEmailAddress;
    
    /**
     * Sets up the Chrome and wait drivers.
     * Used for all three scenarios
     */
    @Given("I have a gmail account")
    public void setupSeleniumDriverForTests() {
        setupSeleniumDrivers();
    }

    
    /**
     * Logs into a Gmail account
     * Used in all three scenarios
     * @param username obtained from Mytest.feature
     * @param password obtained from Mytest.feature
     */
    @When("I login with username (.*) and password (.*)")
    public void gmailLoginUsernamePassword(String username, String password) {

    	//makes the driver access the Gmail link
        chrome.get("https://mail.google.com");
        
        //finds the text box to enter the username
        WebElement emailElement = chrome.findElement(By.id("identifierId"));
        emailElement.click();
        
        //enters the username given in Mytest.feature
        emailElement.sendKeys(username);
        
        //clicks the "Next" button
        chrome.findElement(By.id("identifierNext")).click();
        
        //finds the text box to enter the password
        WebElement passwordElement = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.name("password")));
        
        //checks if the the located text box is for the password
        String label = passwordElement.getAttribute("aria-label");
        if(!label.equals("Enter your password")) {
            fail("Could not find password box");
        }
        
        //enters the password given in Mytest.feature
        passwordElement.sendKeys(password);
        
        //clicks the "Next" button
        chrome.findElement(By.id("passwordNext")).click();

    }

    /**
     * Enters the recipient's email address and the subject of the email draft
     * Used in all three scenarios
     * @param emailAddress used from the "to" column under the examples of the normal scenario in Mytest.feature
     * @param subject used from the "subject" column under the examples of the normal scenario in Mytest.feature
     */
    @And("I want to send an email to (.*) with subject (.*)")
    public void setEmailAddressAndSubject(String emailAddress, String subject) {
        
    	Steps.toEmailAddress = emailAddress;	//setting a static variable for use in other methods
    	
    	//finds the compose button and clicks it to initiate a draft
    	WebElement composeButton = wait.until(ExpectedConditions.presenceOfElementLocated(
    			By.xpath("//*[@role='button' and text()='Compose']")));
        composeButton.click();

        //finds the text box for the recipient's email address and enters it
        WebElement recipientAddressElement = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//textarea[@name='to' and @aria-label='To']")));
        recipientAddressElement.sendKeys(emailAddress);

        //finds the text box for the subject line and enters it
        WebElement subjectElement = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//input[@name='subjectbox' and @aria-label='Subject']")));
        subjectElement.sendKeys(subject);

        //finds the text box for the email body and writes a message
        WebElement bodyElement = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//div[@aria-label='Message Body']")));
        bodyElement.sendKeys("Email sent by selenium");
    }

    /**
     * Attaches an image file to an email draft
     * Used in the Normal and Error scenarios
     * @param imageName the name of the image file to be attached; obtained from Mytest.feature
     * @throws IOException caught in the attachFile method
     */
    @And("I attach an image with name (.*)")
    public void attachImage(String imageName) throws IOException {

    	//attaches a file to the email draft
    	attachFile(imageName);

        //TODO
        wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//input[@name='attach' and @type='hidden']")));
    
    }

    /**
     * Uploads the large image file to Google Drive and attaches a link to the email
     * Used in the Alternate scenario
     * @param imageName the name of the image file to be attached; obtained from Mytest.feature
     * @throws IOException caught in the attachFile method
     */
    @And("I attach a large image with name (.*)")
    public void attachLargeImage(String imageName) throws IOException {
        
    	//attaches a file to the email draft
    	attachFile(imageName);

    	//waits till the Google Drive link for the attachment appears in the body of the email
        WebElement googleDriveLink = waitUpload.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//a[@aria-label='InvalidImage.jpg']")));
        
        //checks if the link pointed to by the attachment is for Google Drive
        String href = googleDriveLink.getAttribute("href");
        if(!href.startsWith("https://drive.google.com")) {
            fail("Attachment link is not a google drive link");
        }
        
    }

    /**
     * Clicks the Send button
     * Used in all three scenarios
     */
    @And("I send the email")
    public void sendEmail() {
        
    	//finds the Send button
    	WebElement sendButton = wait.until(ExpectedConditions.presenceOfElementLocated(
    			By.xpath("//div[@role='button' and @aria-label='Send \u202A(Ctrl-Enter)\u202C']")));
        
    	////////////////////////////////////////REPETITIVE?////////////////////////////////////////////
    	String role = sendButton.getAttribute("role");
        if(!role.equals("button")) {
            fail("Send button not clickable");
        }
        sendButton.click();
    }

    
    //TODO
    /**
     * 
     * Used in the Alternate scenario
     */
    @And("I allow share access to the google drive link")
    public void giveAccess() {
        chrome.switchTo().frame(13);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("span")));
        WebElement accessLink = chrome.findElements(By.tagName("span")).get(2);
        accessLink.click();
        chrome.switchTo().defaultContent();
    }

    /**
     * Logs out of the Gmail account
     * Used in the Normal and Alternate scenarios
     */
    @Then("The email with the attachment should be sent successfully")
    public void closeChrome() {
    	
    	//logs out of the email was sent successfully
    	if(checkIfEmailSent()) {
            logout();
            
            //deletes the cookies of the Chrome browser
            chrome.manage().deleteAllCookies();
        } else {
            fail("Email was not sent successfully");
        }
    	
    }

    /**
     * Checks if an Error message was displayed upon clicking Send
     * Used in the Error scenario
     */
    @Then("I should see an error and the mail will not be sent")
    public void checkError() {

    	//The expected error message for an invalid recipient email address
    	String comparison = "The address \"" + Steps.toEmailAddress + "\" in the \"To\" field was not recognised. "
    			+ "Please make sure that all addresses are properly formed.";

    	//waits for the error box to pop up
        WebElement errorDiv = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//div[@role='alertdialog']")));
        
        //finds the Ok button on the error message and clicks it
        WebElement okButton = errorDiv.findElement(By.xpath("//button[@name='ok']"));
        okButton.click();
        
        //finds the error heading
        WebElement errorHeading = errorDiv.findElement(By.xpath("//span[@role='heading' and text()='Error']"));
        
        //finds the error message
        WebElement errorMessage = errorDiv.findElements(By.tagName("div")).get(1);

        //checks if the heading of the message is as expected
        if(!errorHeading.getText().equals("Error")) {
            fail("Error was not seen");
        }

        //checks if the output error message is as expected
        if(!errorMessage.getText().equals(comparison)) {
            fail("Error was not seen");
        }

        //closes the Chrome browser after the error
        chrome.close();

    }

    /**
     * logs out of the Gmail account
     */
    private void logout() {
    	
    	//finds the account icon and clicks it
        WebElement accountLabel = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//a[@aria-label='Google Account: Selenium Test  \n" +
                "(selenium662@gmail.com)']")));
        accountLabel.click();
        
        //finds the sign out button in the resulting pop up and clicks it
        WebElement signoutButton = wait.until(ExpectedConditions.presenceOfElementLocated(
        		By.xpath("//a[text()='Sign out']")));
        signoutButton.click();
        
    }

    /**
     * checks if the email was sent successfully
     * @return true if a success message was seen after sending the email
     */
    private boolean checkIfEmailSent() {
        
    	try {
        	
        	//waits until a success message appears
            wait.until(ExpectedConditions.presenceOfElementLocated(
            		By.xpath("//span[@class='bAq' and text()='Message sent.']")));
        
        } catch(Exception e) {
            return false;	//if no success message is seen after the defined timeout
        }
        
        return true;	//if a success message is spotted
    
    }
    
    /**
     * Attaches a file to the email draft
     * @param imageName name if the image file to be attached; obtained from Mytest.feature
     * @throws IOException if the specified command to execute the Autoit script cannot be run
     */
    private void attachFile(String imageName) throws IOException {
    	
    	//finds the button to attach a file and clicks it
    	WebElement attachFileElement = wait.until(ExpectedConditions.presenceOfElementLocated(
    			By.xpath("//div[@command='Files' and @aria-label='Attach files']")));
        attachFileElement.click();

        //command containing the location to the autoit script and to the file to be attached
        String cmd = "Images/AttachFile.exe Images/" + imageName;
        Runtime.getRuntime().exec(cmd);
        
    }

    /**
     * Sets up the selenium drivers for the Chrome browser, wait 
     * and waitUpload (used in the alternate scenario for uploading the large image file)
     */
    private void setupSeleniumDrivers() {
    	
        if(chrome == null) {
            System.out.println("Setting up chrome driver");
            System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
            chrome = new ChromeDriver();
            wait = new WebDriverWait(chrome, 15);	//15 second timeout
            waitUpload = new WebDriverWait(chrome, 100);	//100 second timeout
        }
        
    }
    
}