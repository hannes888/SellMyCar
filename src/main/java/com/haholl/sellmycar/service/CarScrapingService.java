package com.haholl.sellmycar.service;

import com.haholl.sellmycar.controller.Car;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CarScrapingService {


    public static List<Car> scrapeCarDataWithSelenium(Car car) {
        String queryUrl = "https://www.autoscout24.com/lst/{0}/{1}?atype=C&cy=D%2CA%2CB%2CE%2CF%2CI%2CL%2CNL&desc=0&fregfrom={2}&fregto={3}&powertype=kw&search_id=1&sort=standard&source=detailsearch&ustate=N%2CU";
        String formattedQuery = MessageFormat.format(queryUrl,
                car.getMake().toString().toLowerCase(),
                car.getModel().toLowerCase(),
                car.getYear() - 1,
                Math.min(car.getYear() + 1, 2024)).replace(",", "");

        List<Car> scrapedCars = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(formattedQuery);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@data-testid='regular-price']")));

            // Find the price elements using XPath
            List<WebElement> priceElements = driver.findElements(By.xpath("//p[@class='Price_price__APlgs PriceAndSeals_current_price__ykUpx' and @data-testid='regular-price']"));

            // Check if any elements were found and print the result
            if (!priceElements.isEmpty()) {
                for (WebElement element : priceElements) {
                    System.out.println("Car Price: " + element.getText());
                }
            } else {
                System.out.println("No car prices found.");
            }

            System.out.println("Formatted URL: " + formattedQuery);


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }

        return scrapedCars;
    }

    public static void main(String[] args) {
        Car car = new Car(Car.Make.AUDI, 1, 1000, "S3", 2010);
        scrapeCarDataWithSelenium(car);
    }
}