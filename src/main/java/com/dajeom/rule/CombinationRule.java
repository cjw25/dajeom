package com.dajeom.rule;

import com.dajeom.type.CombinationScope;
import com.dajeom.type.FiveElement;
import com.dajeom.type.TransformationStatus;

import java.util.Objects;

/**
 * 합화 가능성 V1 Rule.
 *
 * 합 관계가 있다는 이유만으로
 * 자동 합화시키지 않는다.
 *
 * 현재 V1에서는:
 *
 * - 합 종류
 * - 합화 대상 오행의 기존 세력
 * - 월령의 생조 여부
 * - 충에 의한 방해
 *
 * 를 이용해 보수적으로 판정한다.
 *
 * 실제 오행을 다른 오행으로 직접 치환하는
 * 기능은 이 Rule의 역할이 아니다.
 */
public final class CombinationRule {

    public static final String RULE_VERSION =
            "DAJEOM_COMBINATION_V1";

    private static final double
            STEM_ESTABLISHED_MIN =
            20.0;

    private static final double
            SIX_COMBINATION_ESTABLISHED_MIN =
            22.0;

    private static final double
            THREE_HARMONY_ESTABLISHED_MIN =
            18.0;

    private CombinationRule() {

        throw new IllegalStateException(
                "CombinationRule은 생성할 수 없습니다."
        );
    }

    /**
     * 월령 오행이 합화 대상 오행과 같거나,
     * 대상 오행을 생하는 경우
     * 월령의 지원이 있는 것으로 본다.
     */
    public static boolean isMonthSupported(
            FiveElement monthElement,
            FiveElement targetElement
    ) {

        Objects.requireNonNull(
                monthElement,
                "월령 오행은 필수입니다."
        );

        Objects.requireNonNull(
                targetElement,
                "합화 대상 오행은 필수입니다."
        );

        return monthElement
                == targetElement
                || produces(
                monthElement,
                targetElement
        );
    }

    public static TransformationStatus classify(
            CombinationScope scope,
            double targetElementPercentage,
            boolean monthSupported,
            boolean disruptedByClash
    ) {

        Objects.requireNonNull(
                scope,
                "합 종류는 필수입니다."
        );

        if (
                targetElementPercentage < 0.0
                        || targetElementPercentage > 100.0
        ) {

            throw new IllegalArgumentException(
                    "합화 대상 오행 비율은 0부터 100 사이여야 합니다."
            );
        }

        /*
         * 합에 참여하는 지지가 강하게 충을 받고 있다면
         * V1에서는 합화 성립으로 보지 않는다.
         */
        if (
                disruptedByClash
        ) {

            return TransformationStatus.NOT_ESTABLISHED;
        }

        double minimum =
                minimumEstablishedPercentage(
                        scope
                );

        /*
         * 월령의 생조가 있고
         * 대상 오행의 실제 세력도 충분한 경우에만
         * 성립으로 본다.
         */
        if (
                monthSupported
                        && targetElementPercentage
                        >= minimum
        ) {

            return TransformationStatus.ESTABLISHED;
        }

        /*
         * 월령 지원은 있지만 세력이 부족하거나,
         * 대상 오행 자체는 상당한 세력이 있는 경우에는
         * 단정하지 않고 추가 판정 상태로 둔다.
         */
        if (
                monthSupported
                        || targetElementPercentage
                        >= minimum + 5.0
        ) {

            return TransformationStatus.UNDETERMINED;
        }

        return TransformationStatus.NOT_ESTABLISHED;
    }

    private static double
    minimumEstablishedPercentage(
            CombinationScope scope
    ) {

        return switch (scope) {

            case STEM ->
                    STEM_ESTABLISHED_MIN;

            case SIX_COMBINATION ->
                    SIX_COMBINATION_ESTABLISHED_MIN;

            case THREE_HARMONY ->
                    THREE_HARMONY_ESTABLISHED_MIN;
        };
    }

    private static boolean produces(
            FiveElement source,
            FiveElement target
    ) {

        return switch (source) {

            case WOOD ->
                    target
                            == FiveElement.FIRE;

            case FIRE ->
                    target
                            == FiveElement.EARTH;

            case EARTH ->
                    target
                            == FiveElement.METAL;

            case METAL ->
                    target
                            == FiveElement.WATER;

            case WATER ->
                    target
                            == FiveElement.WOOD;
        };
    }
}