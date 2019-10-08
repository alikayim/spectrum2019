import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.DescribedAs.describedAs;
import static org.hamcrest.Matchers.*;

public class Steps {
    WebDriver webDriver;
    Logger logger = LoggerFactory.getLogger(Steps.class);


    @Given("^I open \"([^\"]*)\" page$")
    public void openWebPage(String pageKey) throws IOException, ParseException, InterruptedException {
        String driverType = getPropertyKey("driver.type");
        String url = getPropertyKey("web.url");
        if (driverType.equals("CHROME")) {
            WebDriverManager.chromedriver().setup();
            webDriver = new ChromeDriver();
        } else if (driverType.equals("FIREFOX")) {
            WebDriverManager.firefoxdriver().setup();
            webDriver = new FirefoxDriver();
        } else {
            logger.error("Invalid driver type");
        }
        webDriver.manage().timeouts().implicitlyWait(getPageLoadTimeOut(), TimeUnit.SECONDS);
        webDriver.get(url);
    }

    @And("I select {string}, {string}, {string} category")
    public void selectCategory(String gender, String category, String product) throws IOException, ParseException {
        //TODO
    }

    @And("I select (\\w+(?: \\w+)*) element")
    public void selectElement(String elementKey) throws IOException, ParseException {
        Actions action = new Actions(webDriver);
        action.moveToElement(getElement(elementKey)).perform();
    }

    @And("I click (\\w+(?: \\w+)*) element")
    public void clickElement(String elementKey) throws IOException, ParseException {
        getElement(elementKey).click();
    }


    public String getPropertyKey(String propertyKey) {
        Properties properties = new Properties();
        try (InputStream input = new FileInputStream("src/test/resources/config/config.properties")) {
            properties.load(input);
        } catch (FileNotFoundException e) {
            logger.error("Cannot read properties.Exception : " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Cannot read properties.Exception : " + e.getMessage());
        }
        return properties.getProperty(propertyKey);
    }

    public WebElement getElement(String elementKey) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("src/test/resources/config/elements.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject returnObject = (JSONObject) jsonObject.get("elements");
        String elementValue = returnObject.get(elementKey).toString();

        if (webDriver.findElements(By.id(elementValue)).size() == 1) {
            return webDriver.findElement(By.id(elementValue));
        } else if (webDriver.findElements(By.xpath(elementValue)).size() == 1) {
            return webDriver.findElement(By.xpath(elementValue));
        } else if (webDriver.findElements(By.cssSelector(elementValue)).size() == 1) {
            return webDriver.findElement(By.cssSelector(elementValue));
        } else {
            logger.error("Invalid element key");
            return null;
        }


    }

    public WebElement findVisibleElement(String elementName, By locator) {
        new WebDriverWait(webDriver, getElementLoadTimeOut()).until(ExpectedConditions.visibilityOfElementLocated(locator));
        List<WebElement> elements = webDriver.findElements(locator);
        if (elements.size() > 1) {
            int numElements = elements.size();
            elements = elements.stream().filter(WebElement::isDisplayed).collect(Collectors.toList());
            assertThat(elements, describedAs("Visible ones of the %0 elements returned by %1 for %2",
                    hasSize(1), numElements, locator, elementName));
        } else if (elements.size() == 1) {
            assertThat(elements.get(0).isDisplayed(), describedAs(
                    "Visibility of the element returned by %0 for [%1]", is(true),
                    locator, elementName));
        } else {
            assertThat("not found", describedAs("Element returned by %0 for %1",
                    is(true), elementName));
        }
        return elements.get(0);
    }


    public int getPageLoadTimeOut() {
        return Integer.parseInt(getPropertyKey("driver.pageLoadTimeout"));
    }

    public int getElementLoadTimeOut() {
        return Integer.parseInt(getPropertyKey("driver.elementLoadTimeout"));
    }

    public String evaluateQuotedString(String string) {
        String unquotedString = string.substring(1, string.length() - 1) // remove begin/end "
                .replaceAll("\"\"", "\""); // replace "" with "
        return unquotedString;
    }


    @And("I select (\\w+(?: \\w+)*) in (\\w+(?: \\w+)*)")
    public void selectElementInBox(String selectValue, String selectKey) throws IOException, ParseException {
        Select selectBox = new Select(getElement(selectKey));
        selectBox.selectByVisibleText(selectValue);
    }

}
