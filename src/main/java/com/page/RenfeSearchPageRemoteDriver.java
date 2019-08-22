package com.page;

import net.serenitybdd.core.annotations.findby.By;
import net.serenitybdd.core.pages.WebElementFacade;
import net.thucydides.core.annotations.DefaultUrl;
import net.thucydides.core.annotations.WhenPageOpens;
import org.junit.Assert;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@DefaultUrl("http://www.renfe.com/")
public class RenfeSearchPageRemoteDriver {

//    // ATTRIBUTES
//    @CacheLookup
//    // ORIGIN/DESTINATION INPUT FIELDS
//    @FindBy(id = "IdOrigen")
//    private WebElementFacade originInput;
//    @FindBy(id = "IdDestino")
//    private WebElementFacade destinationInput;
//
//    // ORIGIN/DESTINATION SELECTIONS
//    //@FindBy(xpath = "//*[@id='ui-id-1']")
//    @FindBy(id = "ui-id-1")
//    private WebElementFacade originSelection;
//    //@FindBy(xpath = "//*[@id='ui-id-2']")
//    @FindBy(id = "ui-id-2")
//    private WebElementFacade destinationSelection;
//
//    // DEPARTURE/RETURN DATES INPUT FIELDS
//    @FindBy(id = "__fechaIdaVisual")
//    private WebElementFacade departureDateInput;
//    @FindBy(id = "__fechaVueltaVisual")
//    private WebElementFacade returnDateInput;
//
//    //PASSENGERS INPUT FIELDS
//    @FindBy(id = "__numAdultos")
//    private WebElementFacade numAdultsInput;
//    @FindBy(id = "__numNinos")
//    private WebElementFacade numChildrenInput;
//    @FindBy(id = "__numBebe")
//    private WebElementFacade numBabyInput;
//
//    //BUY BUTTON
//    @FindBy(xpath = "//*[@id=\"datosBusqueda\"]/button")
//    private WebElementFacade buyButton;


    private WebDriver driver = null;
    private Date currentDate;
    private String datePattern = "dd/MM/yyyy";
    private Calendar departureDay;

    public RenfeSearchPageRemoteDriver() throws MalformedURLException {

        String baseUrl = "http://www.renfe.com/";
        String nodeUrl = "http://10.0.75.1:4444/wd/hub";
        DesiredCapabilities capability = null;

        if (System.getenv("BROWSER").equals("chrome")) {
            capability = DesiredCapabilities.chrome();
            capability.setBrowserName("chrome");
        }
        else if (System.getenv("BROWSER").equals("chrome")) {
            capability = DesiredCapabilities.firefox();
            capability.setBrowserName("firefox");
        }
        capability.setPlatform(Platform.WIN10);
        capability.setCapability("applicationName", System.getenv("NODE"));

        driver = new RemoteWebDriver(new URL(nodeUrl), capability);
        driver.get(baseUrl);
        driver.manage().window().maximize();
        currentDate = new Date();
    }

    @WhenPageOpens
    public void waitOpen() { }

    public void selectOrigin(String station) {
        driver.findElement(By.id("IdOrigen")).sendKeys(station);
        selectStation(station, driver.findElement(By.id("ui-id-1")));
    }

    public void selectDestination(String station) {
        driver.findElement(By.id("IdDestino")).sendKeys(station);
        selectStation(station, driver.findElement(By.id("ui-id-2")));
    }

    public void selectStation(String station, WebElement stationInput) {
        List<WebElement> stationsWEList = stationInput.findElements(By.tagName("li"));
        for (int i = 0; i < stationsWEList.size(); i++) {
            if(stationsWEList.get(i).getText().equals(station)){
                stationsWEList.get(i).click();
            }
        }

    }

    public void selectDepartureDate(int daysAfterToday) {
        Calendar day = Calendar.getInstance();
        day.setTime(currentDate);
        day.add(Calendar.DATE, daysAfterToday);

        departureDay = day; //set to global variable to calculate return date later

        SimpleDateFormat f = new SimpleDateFormat(datePattern);
        String departureDate = f.format(day.getTime());
        driver.findElement(By.id("__fechaIdaVisual")).sendKeys(departureDate);
    }

    public void selectReturnDate(int daysAfterDeparture) {
        Assert.assertNotNull("departure date is null", departureDay);

        departureDay.add(Calendar.DATE, daysAfterDeparture);
        SimpleDateFormat f = new SimpleDateFormat(datePattern);
        String returnDate = f.format(departureDay.getTime());
        driver.findElement(By.id("__fechaVueltaVisual")).sendKeys(returnDate);
    }

    public void selectPassengers(int adults, int children, int childrenUnder4) {
        driver.findElement(By.id("__numAdultos")).sendKeys(Integer.toString(adults));
        driver.findElement(By.id("__numNinos")).sendKeys(Integer.toString(children));
        driver.findElement(By.id("__numBebe")).sendKeys(Integer.toString(childrenUnder4));
    }

    public void purchase() {
        try {
            driver.wait(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.findElement(By.xpath("//*[@id=\"datosBusqueda\"]/button")).click();
    }
}
