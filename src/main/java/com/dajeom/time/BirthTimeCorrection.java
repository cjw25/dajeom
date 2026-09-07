package com.dajeom.time;

import java.time.LocalDateTime;
import java.util.Objects;

public record BirthTimeCorrection(
        LocalDateTime originalDateTime,
        int longitudeCorrectionMinutes,
        int daylightSavingCorrectionMinutes,
        LocalDateTime correctedDateTime
) {

    public BirthTimeCorrection {

        Objects.requireNonNull(
                originalDateTime,
                "원본 출생일시가 필요합니다."
        );

        Objects.requireNonNull(
                correctedDateTime,
                "보정된 출생일시가 필요합니다."
        );
    }

    public int totalCorrectionMinutes() {
        return longitudeCorrectionMinutes
                + daylightSavingCorrectionMinutes;
    }

    public boolean dateChanged() {

        return !originalDateTime
                .toLocalDate()
                .equals(
                        correctedDateTime.toLocalDate()
                );
    }

    public boolean timeChanged() {

        return !originalDateTime
                .toLocalTime()
                .equals(
                        correctedDateTime.toLocalTime()
                );
    }
}