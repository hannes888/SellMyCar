package com.haholl.sellmycar.controller;

import com.haholl.sellmycar.service.CarScrapingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/car")
public class CarController {

    private final CarScrapingService carScrapingService;

    @PostMapping
    public CarResponseDto getCarStatistics(@RequestBody Car car) throws Exception {
        return carScrapingService.getCarStatistics(car);
    }

    @GetMapping("/makes")
    public List<String> getCarMakes() {
        return Arrays.stream(Car.Make.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}
