package com.dajeom.dto;

import java.util.List;

public record TarotReadingRequest(

        String category,

        String question,

        List<SelectedCard> cards

) {

    public record SelectedCard(

            int id,

            String name,

            boolean reversed

    ) {
    }
}