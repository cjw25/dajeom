package com.dajeom.dto;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

/**
 * API 공통 오류 응답.
 *
 * =================================================
 * 공식 Frontend 계약
 * =================================================
 *
 * status
 *
 * =
 * HTTP Status Code
 *
 * error
 *
 * =
 * HTTP Reason Phrase
 *
 * errorCode
 *
 * =
 * 프론트가 오류 종류를 판정할 때 사용하는
 * 공식 Machine-Readable Code
 *
 * message
 *
 * =
 * 사용자에게 표시 가능한 안전한 안내문
 *
 * details
 *
 * =
 * 필요한 경우에만 제공하는
 * 안전한 추가 정보
 *
 * =================================================
 * 중요
 * =================================================
 *
 * 프론트에서는 message/details 문자열로
 * 오류 종류를 추측하지 않는다.
 *
 * errorCode를 기준으로 분기한다.
 */
public record ApiErrorResponse(

        ZonedDateTime timestamp,

        int status,

        String error,

        ApiErrorCode errorCode,

        String message,

        String path,

        List<String> details

) {

    public ApiErrorResponse {

        Objects.requireNonNull(
                timestamp,
                "ApiErrorResponse timestamp는 필수입니다."
        );

        if (
                status < 400
                        ||
                        status > 599
        ) {

            throw new IllegalArgumentException(
                    "ApiErrorResponse status는 400~599 범위여야 합니다."
            );
        }

        error =
                requireText(
                        error,
                        "ApiErrorResponse error는 필수입니다."
                );

        Objects.requireNonNull(
                errorCode,
                "ApiErrorResponse errorCode는 필수입니다."
        );

        message =
                requireText(
                        message,
                        "ApiErrorResponse message는 필수입니다."
                );

        path =
                requireText(
                        path,
                        "ApiErrorResponse path는 필수입니다."
                );

        details =
                details == null
                        ? List.of()
                        : details
                        .stream()
                        .filter(
                                Objects::nonNull
                        )
                        .map(
                                String::trim
                        )
                        .filter(
                                detail ->
                                        !detail.isBlank()
                        )
                        .toList();
    }

    private static String requireText(
            String value,
            String message
    ) {

        Objects.requireNonNull(
                value,
                message
        );

        String normalized =
                value.trim();

        if (
                normalized.isBlank()
        ) {

            throw new IllegalArgumentException(
                    message
            );
        }

        return normalized;
    }
}