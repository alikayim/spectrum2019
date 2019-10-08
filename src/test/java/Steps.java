import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.DescribedAs.describedAs;
import static org.hamcrest.Matchers.*;

public class Steps {
    WebDriver webDriver;
    Logger logger = LoggerFactory.getLogger(Steps.class);
    ChromeOptions chromeOptions;
    FirefoxOptions firefoxOptions;


    @Given("^I open \"([^\"]*)\" page$")
    public void openWebPage(String pageKey) throws IOException, ParseException, InterruptedException {
        String driverType = getPropertyKey("driver.type");
        String headless = getPropertyKey("headless");

        String url = getPropertyKey("web.url");
        if (driverType.equals("CHROME")) {
            if (headless.equals("true")) {
                chromeOptions = new ChromeOptions();
                chromeOptions.addArguments("--headless");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver(chromeOptions);

            } else {
                WebDriverManager.chromedriver().setup();
                webDriver = new ChromeDriver();
            }

        } else if (driverType.equals("FIREFOX")) {
            if (headless.equals("true")) {
                firefoxOptions = new FirefoxOptions();
                firefoxOptions.setHeadless(true);
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
            } else {
                WebDriverManager.firefoxdriver().setup();
                webDriver = new FirefoxDriver();
            }

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
        waiForElement(getElement(elementKey)).click();
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


    @And("I select \"([^\"]*)\" in (\\w+(?: \\w+)*)")
    public void selectElementInBox(String selectValue, String selectKey) throws IOException, ParseException {
        Select selectBox = new Select(getElement(selectKey));
        selectBox.selectByValue(selectValue);
    }

    @And("I maximize window")
    public void maximizeWindow() {
        webDriver.manage().window().maximize();
    }

    @And("I click {string}")
    public void clickText(String text) {
        webDriver.findElement(By.partialLinkText(text)).click();
    }

    public WebElement waiForElement(WebElement element) {
        return new WebDriverWait(webDriver, getElementLoadTimeOut()).until(ExpectedConditions.elementToBeClickable(element));

    }

    @And("I fill:")
    public void fillDataMap(Map<String, String> dataMap) {
        for (Map.Entry<String, String> item : dataMap.entrySet()) {
            try {
                fill(item.getKey(), item.getValue());
            } catch (Exception e) {
                throw new RuntimeException("Exception while filling " + item.getKey(), e);
            }
        }
    }

    public void fill(String key, String value) throws IOException, ParseException {
        clearInput(key);
        getElement(key).sendKeys(value);
    }

    public void clearInput(String elementKey) throws IOException, ParseException {
        getElement(elementKey).clear();
    }

    @And("I login with user (\\w+(?: \\w+)*)")
    public void login(String userKey) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("src/test/resources/config/users.json"));
        JSONObject jsonObject = (JSONObject) obj;
        JSONObject returnObject = (JSONObject) jsonObject.get(userKey);
        String username = returnObject.get("username").toString();
        String password = returnObject.get("password").toString();
        clickText("Sign In");
        fill("loginMail", username);
        fill("loginPass", password);
        clickElement("loginButton");
    }

    @And("I select cheapest of (\\w+(?: \\w+)*), (\\w+(?: \\w+)*), (\\w+(?: \\w+)*)")
    public void compareProductsPrice(String product1, String product2, String product3) throws IOException, ParseException {
        float productsPrice[] = new float[3];
        String productDetail = "//*[@id='product-comparison']/tbody[1]/tr/td/div[2]/span/span/span[2]";
        productsPrice[0] = Float.parseFloat(getElement(product1).getAttribute("data-price-amount"));
        productsPrice[1] = Float.parseFloat(getElement(product2).getAttribute("data-price-amount"));
        productsPrice[2] = Float.parseFloat(getElement(product3).getAttribute("data-price-amount"));
        Arrays.sort(productsPrice);
        WebElement element;
        List<WebElement> webElements = webDriver.findElements(By.xpath(productDetail));
        for (int i = 0; i < webElements.size(); i++) {
            if ((Float.parseFloat(webElements.get(i).getAttribute("data-price-amount")) == productsPrice[0])) {
                element = webElements.get(i);
                int y = i + 1;
                webDriver.findElement(By.xpath("//*[@id=\"product-comparison\"]/tbody[1]/tr/td[" + y + "]/div[3]/div[2]/a")).click();
            }
        }
    }

    @And("I create a user")
    public void createUser() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Random random = new Random();
        clickText("Create an Account");
        fill("createFirstName", "Test");
        fill("createLastName", "User");
        fill("createMail", "test" + Integer.toString(random.nextInt()) + "@test.com");
        fill("createPass", "Test1234");
        fill("confirmPass", "Test1234");
        clickElement("createButton");

    }


    @And("I check final amount is lower than {string}")
    public void checkAmount(String amountValue) throws IOException, ParseException {
        String amountText = getElement("amount").getText();
        amountText = amountText.substring(1);

        float amount = Float.parseFloat(amountText);
        float amountExpected = Float.parseFloat(amountValue);
        logger.info("Total Amount : " + amount);
        assertThat(amountExpected, greaterThan(amount));

    }

}
