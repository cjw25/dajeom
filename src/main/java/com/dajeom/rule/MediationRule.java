package com.dajeom.rule;

import com.dajeom.type.FiveElement;
import com.dajeom.type.MediationStatus;

import java.util.Objects;

/**
 * 오행 통관 V1 Rule.
 *
 * 양쪽 오행이 모두 일정 수준 이상 존재할 때
 * 실제 충돌 가능성이 있는 관계로 본다.
 */
public final class MediationRule {

    public static final String RULE_VERSION =
            "DAJEOM_MEDIATION_V1";

    /**
     * 두 오행이 실제 충돌 구조로
     * 분석되기 위한 최소 세력 비율.
     */
    public static final double
            CONFLICT_MIN_PERCENTAGE =
            15.0;

    /**
     * 통관 오행이 충분히 작용한다고 보는 기준.
     */
    public static final double
            ACTIVE_MEDIATOR_MIN_PERCENTAGE =
            12.0;

    /**
     * 통관 오행이 존재하지만
     * 충분하지 않은 상태의 최소 기준.
     */
    public static final double
            POSSIBLE_MEDIATOR_MIN_PERCENTAGE =
            4.0;

    private MediationRule() {

        throw new IllegalStateException(
                "MediationRule은 생성할 수 없습니다."
        );
    }

    /**
     * 특정 오행이 극하는 오행.
     */
    public static FiveElement controlledElementOf(
            FiveElement controller
    ) {

        Objects.requireNonNull(
                controller,
                "극하는 오행은 필수입니다."
        );

        return switch (controller) {

            case WOOD ->
                    FiveElement.EARTH;

            case FIRE ->
                    FiveElement.METAL;

            case EARTH ->
                    FiveElement.WATER;

            case METAL ->
                    FiveElement.WOOD;

            case WATER ->
                    FiveElement.FIRE;
        };
    }

    /**
     * 상극 관계를 이어주는 통관 오행.
     *
     * 木克土 -> 火
     * 火克金 -> 土
     * 土克水 -> 金
     * 金克木 -> 水
     * 水克火 -> 木
     */
    public static FiveElement mediatorElementOf(
            FiveElement controller
    ) {

        Objects.requireNonNull(
                controller,
                "극하는 오행은 필수입니다."
        );

        return switch (controller) {

            case WOOD ->
                    FiveElement.FIRE;

            case FIRE ->
                    FiveElement.EARTH;

            case EARTH ->
                    FiveElement.METAL;

            case METAL ->
                    FiveElement.WATER;

            case WATER ->
                    FiveElement.WOOD;
        };
    }

    public static boolean isConflictRelevant(
            double controllerPercentage,
            double controlledPercentage
    ) {

        return controllerPercentage
                >= CONFLICT_MIN_PERCENTAGE
                && controlledPercentage
                >= CONFLICT_MIN_PERCENTAGE;
    }

    public static MediationStatus classify(
            double mediatorPercentage
    ) {

        if (
                mediatorPercentage
                        >= ACTIVE_MEDIATOR_MIN_PERCENTAGE
        ) {

            return MediationStatus.ACTIVE;
        }

        if (
                mediatorPercentage
                        >= POSSIBLE_MEDIATOR_MIN_PERCENTAGE
        ) {

            return MediationStatus.POSSIBLE;
        }

        return MediationStatus.ABSENT;
    }
}