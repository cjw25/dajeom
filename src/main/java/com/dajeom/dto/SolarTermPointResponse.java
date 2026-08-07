package com.dajeom.dto;

import com.dajeom.model.SolarTermPoint;

import java.time.ZonedDateTime;

public record SolarTermPointResponse(
        String name,
        double longitude,
        ZonedDateTime dateTime,
        int monthIndex
) {

    public static SolarTermPointResponse from(
            SolarTermPoint point
    ) {
        return new SolarTermPointResponse(
                point.name(),
                point.longitude(),
                point.dateTime(),
                point.monthIndex()
        );
    }
}