package com.dajeom.dto;

import java.time.LocalDateTime;

public record ErrorResponse(

        boolean success,

        String message,

        LocalDateTime timestamp
) {

    public static ErrorResponse of(
            String message
    ) {
        return new ErrorResponse(
                false,
                message,
                LocalDateTime.now()
        );
    }
}