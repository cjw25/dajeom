package com.dajeom.rule;

import com.dajeom.type.FiveElement;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StrengthLevel;

import java.util.List;
import java.util.Objects;

/**
 * 오행 세력 및 일간 강약 분석에 사용하는 Rule.
 *
 * V1에서는 명리 분석 구조를 안정적으로 만들기 위한
 * 명시적인 기본 가중치를 사용한다.
 *
 * 향후 실제 사례 검증 및 명리 기준 조정이 필요할 경우
 * 이 클래스의 Rule을 수정하거나
 * 새로운 Rule 버전을 추가한다.
 *
 * Analyzer는 가중치 자체를 알지 않는다.
 */
public final class StrengthRule {

    public static final String RULE_VERSION =
            "DAJEOM_STRENGTH_V1";

    /**
     * 천간/지지 표면 오행 1글자의 기본 점수.
     */
    public static final double
            VISIBLE_ELEMENT_WEIGHT =
            1.0;

    /**
     * 지장간 여기 가중치.
     */
    public static final double
            HIDDEN_RESIDUAL_WEIGHT =
            0.20;

    /**
     * 지장간 중기 가중치.
     */
    public static final double
            HIDDEN_MIDDLE_WEIGHT =
            0.35;

    /**
     * 지장간 정기 가중치.
     */
    public static final double
            HIDDEN_MAIN_WEIGHT =
            0.60;

    /**
     * 월지의 계절적 영향력을
     * 별도로 반영하는 추가 가중치.
     *
     * 월지는 원국 전체의 계절적 환경을
     * 판단하는 데 중요한 위치이므로
     * 일반 지지보다 추가적인 세력을 부여한다.
     */
    public static final double
            MONTH_COMMAND_BONUS =
            1.50;

    /**
     * 매우 신강 판정 기준.
     */
    public static final double
            VERY_STRONG_THRESHOLD =
            65.0;

    /**
     * 신강 판정 기준.
     */
    public static final double
            STRONG_THRESHOLD =
            55.0;

    /**
     * 중화 하한.
     */
    public static final double
            BALANCED_THRESHOLD =
            45.0;

    /**
     * 신약 하한.
     */
    public static final double
            WEAK_THRESHOLD =
            35.0;

    private StrengthRule() {

        throw new IllegalStateException(
                "StrengthRule은 생성할 수 없습니다."
        );
    }

    /**
     * 지장간 역할에 따른 가중치.
     */
    public static double hiddenStemWeight(
            HiddenStemRole role
    ) {

        Objects.requireNonNull(
                role,
                "지장간 역할은 필수입니다."
        );

        return switch (role) {

            case RESIDUAL ->
                    HIDDEN_RESIDUAL_WEIGHT;

            case MIDDLE ->
                    HIDDEN_MIDDLE_WEIGHT;

            case MAIN ->
                    HIDDEN_MAIN_WEIGHT;
        };
    }

    /**
     * 특정 오행을 생해주는 오행.
     *
     * 목 ← 수
     * 화 ← 목
     * 토 ← 화
     * 금 ← 토
     * 수 ← 금
     */
    public static FiveElement resourceElementOf(
            FiveElement element
    ) {

        Objects.requireNonNull(
                element,
                "오행은 필수입니다."
        );

        return switch (element) {

            case WOOD ->
                    FiveElement.WATER;

            case FIRE ->
                    FiveElement.WOOD;

            case EARTH ->
                    FiveElement.FIRE;

            case METAL ->
                    FiveElement.EARTH;

            case WATER ->
                    FiveElement.METAL;
        };
    }

    /**
     * 일간을 직접 지원하는 오행.
     *
     * 비겁:
     * 같은 오행
     *
     * 인성:
     * 일간을 생하는 오행
     */
    public static List<FiveElement>
    supportingElementsOf(
            FiveElement dayMasterElement
    ) {

        Objects.requireNonNull(
                dayMasterElement,
                "일간 오행은 필수입니다."
        );

        return List.of(
                dayMasterElement,
                resourceElementOf(
                        dayMasterElement
                )
        );
    }

    /**
     * 지원 오행을 제외한 나머지 오행.
     */
    public static List<FiveElement>
    challengingElementsOf(
            FiveElement dayMasterElement
    ) {

        List<FiveElement> supporting =
                supportingElementsOf(
                        dayMasterElement
                );

        return List.of(
                        FiveElement.values()
                )
                .stream()
                .filter(
                        element ->
                                !supporting.contains(
                                        element
                                )
                )
                .toList();
    }

    /**
     * 지원 세력 비율을 이용해
     * 기본 강약 단계를 판정한다.
     */
    public static StrengthLevel classify(
            double supportRatio
    ) {

        if (
                Double.isNaN(
                        supportRatio
                )
                        || Double.isInfinite(
                        supportRatio
                )
                        || supportRatio < 0.0
                        || supportRatio > 100.0
        ) {

            throw new IllegalArgumentException(
                    "지원 세력 비율은 0부터 100 사이여야 합니다."
            );
        }

        if (
                supportRatio
                        >= VERY_STRONG_THRESHOLD
        ) {

            return StrengthLevel.VERY_STRONG;
        }

        if (
                supportRatio
                        >= STRONG_THRESHOLD
        ) {

            return StrengthLevel.STRONG;
        }

        if (
                supportRatio
                        >= BALANCED_THRESHOLD
        ) {

            return StrengthLevel.BALANCED;
        }

        if (
                supportRatio
                        >= WEAK_THRESHOLD
        ) {

            return StrengthLevel.WEAK;
        }

        return StrengthLevel.VERY_WEAK;
    }
}