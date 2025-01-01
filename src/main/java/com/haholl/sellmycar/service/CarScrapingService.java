package com.haholl.sellmycar.service;

import com.haholl.sellmycar.controller.Car;
import com.haholl.sellmycar.controller.CarResponseDto;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Slf4j
public class CarScrapingService {

    public CarResponseDto getCarStatistics(Car car) throws Exception {
        List<Integer> scrapedPrices = scrapeCarData(car);

        if (scrapedPrices.isEmpty()) {
            throw new NoSuchElementException("No statistics found for the car: " + car.getModel());
        }

        log.info(String.format("Found %d results for %s %s", scrapedPrices.size(), car.getMake(), car.getModel()));

        Collections.sort(scrapedPrices);

        int minPrice = scrapedPrices.getFirst();
        int maxPrice = scrapedPrices.getLast();
        int meanPrice = scrapedPrices.stream().mapToInt(Integer::intValue).sum() / scrapedPrices.size();
        int medianPrice = scrapedPrices.get(scrapedPrices.size() / 2);

        return new CarResponseDto(
                minPrice,
                maxPrice,
                medianPrice,
                meanPrice,
                0  // TODO Implement mileage fetching
        );
    }

    public List<Integer> scrapeCarData(Car car) throws Exception {
        String queryUrl = "https://www.autoscout24.com/lst/%s/%s?atype=C&cy=D%%2CA%%2CB%%2CE%%2CF%%2CI%%2CL%%2CNL&desc=0&fregfrom=%d&fregto=%d&powertype=kw&search_id=1&sort=standard&source=detailsearch&ustate=N%%2CU";
        String formattedQuery = String.format(queryUrl,
                car.getMake().toString().toLowerCase(),
                car.getModel().toLowerCase(),
                car.getYear() - 1,
                Math.min(car.getYear() + 1, 2024));

        List<Integer> scrapedPrices = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.get(formattedQuery);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@data-testid='regular-price']")));

            List<WebElement> priceElements = driver.findElements(By.xpath("//p[@class='Price_price__APlgs PriceAndSeals_current_price__ykUpx' and @data-testid='regular-price']"));

            // Clean up price formatting
            priceElements.forEach(price -> scrapedPrices.add(
                    Integer.valueOf(price.getText().replaceAll("[^0-9]", ""))));

        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            driver.quit();
        }

        if (scrapedPrices.isEmpty()) {
            throw new NoSuchElementException("No statistics found for the car: " + car.getModel());
        }

        return scrapedPrices;
    }
}
