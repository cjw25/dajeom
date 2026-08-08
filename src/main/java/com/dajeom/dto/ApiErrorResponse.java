package com.dajeom.dto;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * API 공통 오류 응답.
 */
public record ApiErrorResponse(

        ZonedDateTime timestamp,

        int status,

        String error,

        String message,

        String path,

        List<String> details

) {

    public ApiErrorResponse {

        details =
                details == null
                        ? List.of()
                        : List.copyOf(
                        details
                );
    }
}