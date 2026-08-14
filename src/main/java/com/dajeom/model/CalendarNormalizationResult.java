package com.dajeom.model;

import com.dajeom.type.CalendarType;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 사용자가 입력한 양력/음력 날짜를
 * 명리 계산용 양력 날짜로 정규화한 결과.
 *
 * 핵심 원칙
 *
 * 1. 사용자가 어떤 달력으로 입력했는지 보존한다.
 * 2. 원래 입력 날짜를 보존한다.
 * 3. 윤달 여부를 보존한다.
 * 4. 명리 계산은 solarDate를 기준으로 진행한다.
 * 5. 음력 변환 결과도 함께 보존하여 검증 가능하게 한다.
 */
public record CalendarNormalizationResult(

        CalendarType inputCalendarType,

        LocalDate inputDate,

        boolean inputLeapMonth,

        LocalDate solarDate,

        LocalDate lunarDate,

        boolean lunarLeapMonth

) {

    public CalendarNormalizationResult {

        Objects.requireNonNull(
                inputCalendarType,
                "입력 달력 구분이 필요합니다."
        );

        Objects.requireNonNull(
                inputDate,
                "입력 생년월일이 필요합니다."
        );

        Objects.requireNonNull(
                solarDate,
                "정규화된 양력 생년월일이 필요합니다."
        );

        Objects.requireNonNull(
                lunarDate,
                "대응하는 음력 생년월일이 필요합니다."
        );

        /*
         * 양력 입력에서 윤달=true가 들어오는
         * 모순된 상태를 허용하지 않는다.
         */
        if (
                inputCalendarType == CalendarType.SOLAR
                        && inputLeapMonth
        ) {
            throw new IllegalArgumentException(
                    "양력 입력에는 윤달을 지정할 수 없습니다."
            );
        }
    }

    /**
     * 실제 명리 계산에 사용할 날짜.
     *
     * 년주·월주·일주·시주의 날짜 기반 계산은
     * 이 값을 사용한다.
     */
    public LocalDate calculationDate() {

        return solarDate;
    }

    /**
     * 사용자가 음력으로 입력했는지 확인.
     */
    public boolean lunarInput() {

        return inputCalendarType
                == CalendarType.LUNAR;
    }

    /**
     * 사용자가 양력으로 입력했는지 확인.
     */
    public boolean solarInput() {

        return inputCalendarType
                == CalendarType.SOLAR;
    }

    /**
     * 사용자 입력이 윤달 음력이었는지 확인.
     */
    public boolean intercalationInput() {

        return lunarInput()
                && inputLeapMonth;
    }
}