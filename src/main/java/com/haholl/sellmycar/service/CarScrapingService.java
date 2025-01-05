package com.haholl.sellmycar.service;

import com.haholl.sellmycar.controller.Car;
import com.haholl.sellmycar.controller.CarResponseDto;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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
import java.util.Objects;

@Service
@Slf4j
public class CarScrapingService {
    private final List<Integer> mileages = new ArrayList<>(List.of(0, 2500, 5000, 10000, 20000, 30000, 40000,
            50000, 60000, 70000, 80000, 90000, 100000, 125000, 150000, 175000, 200000));

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
                meanPrice
        );
    }

    public List<Integer> scrapeCarData(Car car) throws Exception {
        String[] mileageParams = getMileageParams(car);

        String queryUrl = "https://www.autoscout24.com/lst/%s/%s?atype=C&cy=D%%2CA%%2CB%%2CE%%2CF%%2CI%%2CL%%2CNL&desc=0&fregfrom=%d&fregto=%d&%s%spage=1&powertype=kw&search_id=1&sort=standard&source=listpage_pagination&ustate=N%%2CU";
        String formattedQuery = String.format(queryUrl,
                car.getMake().toString().toLowerCase(),
                car.getModel().toLowerCase(),
                car.getYear() - 1,
                Math.min(car.getYear() + 1, 2024),
                mileageParams[0],
                mileageParams[1]);

        List<Integer> scrapedPrices = new ArrayList<>();

        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        WebDriver driver = new ChromeDriver(options);

        try {
            for (int i = 1; i < 10; i++) {
                formattedQuery = formattedQuery.replaceAll("page=[0-9]", "page=" + i);
                log.info(formattedQuery);
                driver.get(formattedQuery);

                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(3));
                try {
                    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//p[@data-testid='regular-price']")));
                } catch (TimeoutException e) {
                    log.info("Timeout occurred, no price elements found on page {}", i);
                    break;
                }

                List<WebElement> priceElements = driver.findElements(By.xpath("//p[@class='Price_price__APlgs PriceAndSeals_current_price__ykUpx' and @data-testid='regular-price']"));

                // Clean up price formatting
                priceElements.forEach(price -> scrapedPrices.add(
                        Integer.valueOf(price.getText().replaceAll("[^0-9]", ""))));
            }

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

    private String[] getMileageParams(Car car) {
        String startMileageAsParam = "";
        String endMileageAsParam = "";
        Integer startMileage = null;
        Integer endMileage = null;

        if (car.getMileage() != null && car.getMileage() >= 0) {

            if (car.getMileage() >= mileages.getLast()) {  // Mileage >= 200,000km
                startMileage = mileages.getLast();
            } else {
                for (int i = 0; i < mileages.size() - 1; i++) {
                    int current = mileages.get(i);
                    if (current < car.getMileage() && car.getMileage() < mileages.get(i + 1)) {
                        startMileage = current;
                        endMileage = mileages.get(i + 1);
                        break;
                    }
                }
            }

            if (startMileage != null) {
                startMileageAsParam = "kmfrom=" + startMileage + "&";
            }
            if (endMileage != null) {
                endMileageAsParam = "kmto=" + endMileage + "&";
            }
        }
        return new String[]{startMileageAsParam, endMileageAsParam};
    }
}
