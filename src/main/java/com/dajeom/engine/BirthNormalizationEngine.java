package com.dajeom.engine;

import com.dajeom.config.SajuCalculationPolicy;
import com.dajeom.dto.SajuAnalysisRequest;
import com.dajeom.model.NormalizedBirthData;
import com.dajeom.type.CalendarType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 프론트 요청값을 명리 계산용 데이터로 변환한다.
 */
@Component
public class BirthNormalizationEngine {

    public NormalizedBirthData normalize(
            SajuAnalysisRequest request
    ) {
        validateRequest(request);

        LocalDateTime birthDateTime =
                LocalDateTime.of(
                        request.birthDate(),
                        request.birthTime()
                );

        return new NormalizedBirthData(
                request.calendarType(),
                request.birthDate(),
                request.birthTime(),
                birthDateTime,
                request.gender(),
                Boolean.TRUE.equals(
                        request.leapMonth()
                ),
                SajuCalculationPolicy.DEFAULT_ZONE_ID
        );
    }

    private void validateRequest(
            SajuAnalysisRequest request
    ) {
        /*
         * 1차에서는 양력만 지원한다.
         */
        if (request.calendarType()
                == CalendarType.LUNAR) {

            throw new IllegalArgumentException(
                    "1차 명리 계산 엔진은 "
                            + "양력만 지원합니다."
            );
        }

        /*
         * 양력에는 윤달을 적용할 수 없다.
         */
        if (Boolean.TRUE.equals(
                request.leapMonth()
        )) {
            throw new IllegalArgumentException(
                    "양력 입력에는 "
                            + "윤달을 적용할 수 없습니다."
            );
        }

        int birthYear =
                request.birthDate().getYear();

        if (birthYear
                < SajuCalculationPolicy.MIN_SUPPORTED_YEAR
                || birthYear
                > SajuCalculationPolicy.MAX_SUPPORTED_YEAR) {

            throw new IllegalArgumentException(
                    "지원하는 출생연도는 "
                            + SajuCalculationPolicy
                            .MIN_SUPPORTED_YEAR
                            + "년부터 "
                            + SajuCalculationPolicy
                            .MAX_SUPPORTED_YEAR
                            + "년까지입니다."
            );
        }
    }
}