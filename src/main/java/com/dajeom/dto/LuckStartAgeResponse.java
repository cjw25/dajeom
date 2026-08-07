package com.dajeom.dto;

import com.dajeom.model.LuckStartAge;

public record LuckStartAgeResponse(
        int years,
        int months,
        int days,
        double decimalYears,
        String displayText
) {

    public static LuckStartAgeResponse from(
            LuckStartAge age
    ) {
        String displayText =
                age.years()
                        + "년 "
                        + age.months()
                        + "개월 "
                        + age.days()
                        + "일";

        return new LuckStartAgeResponse(
                age.years(),
                age.months(),
                age.days(),
                age.decimalYears(),
                displayText
        );
    }
}