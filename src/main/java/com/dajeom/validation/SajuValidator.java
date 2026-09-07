package com.dajeom.validation;

import com.dajeom.dto.SajuRequest;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SajuValidator {

    public void validate(
            SajuRequest request
    ) {

        Objects.requireNonNull(
                request,
                "사주 계산 요청이 필요합니다."
        );

        if (request.calendarType() == null) {

            throw new IllegalArgumentException(
                    "양력/음력 구분이 필요합니다."
            );
        }

        if (request.birthDate() == null
                || request.birthDate().isBlank()) {

            throw new IllegalArgumentException(
                    "생년월일이 필요합니다."
            );
        }

        if (request.gender() == null) {

            throw new IllegalArgumentException(
                    "성별이 필요합니다."
            );
        }

        /*
         * 윤달은 음력에만 존재한다.
         */
        if (request.calendarType().isSolar()
                && request.leapMonth()) {

            throw new IllegalArgumentException(
                    "양력에는 윤달을 지정할 수 없습니다."
            );
        }
    }
}