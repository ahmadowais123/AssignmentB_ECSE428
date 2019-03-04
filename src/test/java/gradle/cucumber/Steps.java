package gradle.cucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class Steps {

    public static WebDriver webDriver;
    public static final String CHROME_DRIVER_PATH = "Driver/chromedriver.exe";

    @Given("^I open chrome and navigate to gmail$")
    public void openChromeAndGoToGmail() throws Exception {

        setupSeleniumDriver();

        webDriver.get("http://www.google.com/xhtml");
        Thread.sleep(5000);  // Let the user actually see something!
        WebElement searchBox = webDriver.findElement(By.name("q"));
        searchBox.sendKeys("ChromeDriver");
        searchBox.submit();
        Thread.sleep(5000);  // Let the user actually see something!
        webDriver.quit();
    }

    private void setupSeleniumDriver() {
        if(webDriver == null) {
            System.out.println("Setting up chrome driver");
            System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_PATH);
            webDriver = new ChromeDriver();
        }
    }
}
