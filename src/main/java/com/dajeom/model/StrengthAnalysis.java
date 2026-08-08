package com.dajeom.model;

import com.dajeom.type.FiveElement;
import com.dajeom.type.StrengthLevel;

import java.util.List;
import java.util.Objects;

/**
 * 일간의 기본 강약 분석 결과.
 *
 * 내부 명리 분석용 모델이다.
 *
 * 최종 사용자에게는
 * "신강", "신약"이라는 용어를 그대로
 * 보여주기보다 Consultation 단계에서
 * 쉬운 문장으로 변환한다.
 */
public record StrengthAnalysis(

        /**
         * 일간의 오행.
         */
        FiveElement dayMasterElement,

        /**
         * 일간을 생해주는 인성 계열 오행.
         */
        FiveElement resourceElement,

        /**
         * 일간을 직접 도와주는 오행.
         *
         * 일간 자체 오행 + 인성 오행.
         */
        List<FiveElement> supportingElements,

        /**
         * 일간의 힘을 사용하거나
         * 제어하는 나머지 오행.
         */
        List<FiveElement> challengingElements,

        /**
         * 일간을 지원하는 세력 점수.
         */
        double supportScore,

        /**
         * 일간을 지원하지 않는 세력 점수.
         */
        double challengeScore,

        /**
         * 전체 세력 중 지원 세력의 비율.
         *
         * 0 ~ 100.
         */
        double supportRatio,

        /**
         * 최종 기본 강약 단계.
         */
        StrengthLevel level,

        /**
         * 시주까지 포함해서 분석했는지.
         */
        boolean hourPillarIncluded,

        /**
         * 사용한 강약 판정 Rule 버전.
         */
        String ruleVersion

) {

    public StrengthAnalysis {

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행은 필수입니다."
        );

        Objects.requireNonNull(
                resourceElement,
                "인성 오행은 필수입니다."
        );

        Objects.requireNonNull(
                supportingElements,
                "지원 오행 목록은 필수입니다."
        );

        Objects.requireNonNull(
                challengingElements,
                "비지원 오행 목록은 필수입니다."
        );

        Objects.requireNonNull(
                level,
                "강약 단계는 필수입니다."
        );

        Objects.requireNonNull(
                ruleVersion,
                "강약 판정 Rule 버전은 필수입니다."
        );

        if (
                supportScore < 0.0
                        || challengeScore < 0.0
        ) {

            throw new IllegalArgumentException(
                    "강약 분석 점수는 음수일 수 없습니다."
            );
        }

        if (
                supportRatio < 0.0
                        || supportRatio > 100.0
        ) {

            throw new IllegalArgumentException(
                    "지원 세력 비율은 0부터 100 사이여야 합니다."
            );
        }

        supportingElements =
                List.copyOf(
                        supportingElements
                );

        challengingElements =
                List.copyOf(
                        challengingElements
                );
    }

    public boolean isStrong() {

        return level
                == StrengthLevel.STRONG
                || level
                == StrengthLevel.VERY_STRONG;
    }

    public boolean isWeak() {

        return level
                == StrengthLevel.WEAK
                || level
                == StrengthLevel.VERY_WEAK;
    }

    public boolean isBalanced() {

        return level
                == StrengthLevel.BALANCED;
    }
}