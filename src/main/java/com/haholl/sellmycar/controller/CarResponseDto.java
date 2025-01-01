package com.haholl.sellmycar.controller;

import lombok.Data;

@Data
public class CarResponseDto {
    private final int minPrice;
    private final int maxPrice;
    private final int medianPrice;
    private final int meanPrice;
    private final int averageMileage;
}
