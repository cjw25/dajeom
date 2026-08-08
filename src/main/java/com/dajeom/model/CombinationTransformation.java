package com.dajeom.model;

import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.CombinationScope;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.TransformationStatus;

import java.util.List;
import java.util.Objects;

/**
 * 하나의 합 관계에 대한
 * 합화 가능성 분석 결과.
 *
 * 합이 존재한다는 것과
 * 실제 합화가 이루어진다는 것은
 * 서로 다른 문제이므로 분리해서 관리한다.
 */
public record CombinationTransformation(

        CombinationScope scope,

        List<PillarPosition> positions,

        /**
         * 합화가 성립할 경우
         * 변화 대상으로 보는 오행.
         */
        FiveElement targetElement,

        TransformationStatus status,

        /**
         * 현재 원국에서 합화 대상 오행이
         * 차지하는 가중 세력 비율.
         */
        double targetElementPercentage,

        /**
         * 월령이 합화 대상 오행을
         * 직접 돕거나 생하는지 여부.
         */
        boolean monthSupported,

        /**
         * 합 관계에 참여하는 위치가
         * 충의 영향을 받고 있는지.
         */
        boolean disruptedByClash,

        AnalysisConfidence confidence,

        String ruleVersion

) {

    public CombinationTransformation {

        Objects.requireNonNull(
                scope,
                "합 종류는 필수입니다."
        );

        Objects.requireNonNull(
                positions,
                "합 위치 목록은 필수입니다."
        );

        Objects.requireNonNull(
                targetElement,
                "합화 대상 오행은 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "합화 상태는 필수입니다."
        );

        Objects.requireNonNull(
                confidence,
                "분석 신뢰도는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "Rule 버전은 필수입니다."
        );

        positions =
                List.copyOf(
                        positions
                );

        if (
                positions.size() < 2
                        || positions.size() > 3
        ) {

            throw new IllegalArgumentException(
                    "합 관계는 2개 또는 3개의 위치로 구성되어야 합니다."
            );
        }

        if (
                targetElementPercentage < 0.0
                        || targetElementPercentage > 100.0
        ) {

            throw new IllegalArgumentException(
                    "합화 대상 오행 비율은 0부터 100 사이여야 합니다."
            );
        }
    }

    public boolean isEstablished() {

        return status
                == TransformationStatus.ESTABLISHED;
    }

    public boolean isUndetermined() {

        return status
                == TransformationStatus.UNDETERMINED;
    }
}