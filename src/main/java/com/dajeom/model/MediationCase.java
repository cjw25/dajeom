package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.MediationStatus;

import java.util.Objects;

/**
 * 오행 상극 관계 하나에 대한
 * 통관 분석 결과.
 *
 * 예:
 *
 * 木克土
 *
 * 火가 존재하면:
 *
 * 木生火
 * 火生土
 *
 * 형태로 두 오행 사이를 연결할 수 있다.
 */
public record MediationCase(

        /**
         * 극을 하는 오행.
         */
        FiveElement controllerElement,

        /**
         * 극을 받는 오행.
         */
        FiveElement controlledElement,

        /**
         * 두 오행 사이를 연결하는
         * 통관 오행.
         */
        FiveElement mediatorElement,

        double controllerPercentage,

        double controlledPercentage,

        double mediatorPercentage,

        MediationStatus status,

        AnalysisConfidence confidence

) {

    public MediationCase {

        Objects.requireNonNull(
                controllerElement,
                "극하는 오행은 필수입니다."
        );

        Objects.requireNonNull(
                controlledElement,
                "극을 받는 오행은 필수입니다."
        );

        Objects.requireNonNull(
                mediatorElement,
                "통관 오행은 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "통관 상태는 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "분석 신뢰도는 필수입니다."
        );

        validatePercentage(
                controllerPercentage
        );

        validatePercentage(
                controlledPercentage
        );

        validatePercentage(
                mediatorPercentage
        );
    }

    private static void validatePercentage(
            double percentage
    ) {

        if (
                percentage < 0.0
                        || percentage > 100.0
                        || Double.isNaN(
                        percentage
                )
                        || Double.isInfinite(
                        percentage
                )
        ) {

            throw new IllegalArgumentException(
                    "오행 비율은 0부터 100 사이의 유한한 값이어야 합니다."
            );
        }
    }
}