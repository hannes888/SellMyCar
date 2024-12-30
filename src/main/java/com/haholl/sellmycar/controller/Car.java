package com.haholl.sellmycar.controller;

import lombok.Data;

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
    private final int mileage;
    private final String model;
    private final int year;
}
