package com.dajeom.dto;

import java.util.List;

public record TarotReadingResponse(

        String opening,

        List<CardReading> cardReadings,

        String summary,

        String advice

) {

    public record CardReading(

            String position,

            String cardName,

            boolean reversed,

            String keyword,

            String interpretation

    ) {
    }
}