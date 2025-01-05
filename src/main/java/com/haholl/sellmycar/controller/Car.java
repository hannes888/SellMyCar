package com.haholl.sellmycar.controller;

import lombok.Data;

import java.util.Optional;

@Data
public class Car {
    public enum Make {
        PORSCHE,
        AUDI,
        VOLKSWAGEN,
        SKODA,
        TOYOTA,
        HONDA,
        MAZDA
    }

    private final Make make;
    private final double price;
    private final Integer mileage;
    private final String model;
    private final int year;
}
