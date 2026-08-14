package com.dajeom.analyzer;

import com.dajeom.model.DayMasterElementRelation;
import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 4차-1.
 *
 * 3차에서 완성된 오행 세력을
 * 일간 관점의 십성 그룹으로 변환한다.
 *
 * 규칙:
 *
 * 일간과 같은 오행
 * -> 비겁
 *
 * 일간을 생하는 오행
 * -> 인성
 *
 * 일간이 생하는 오행
 * -> 식상
 *
 * 일간이 극하는 오행
 * -> 재성
 *
 * 일간을 극하는 오행
 * -> 관살
 *
 * 이 단계에서는:
 *
 * - 신강
 * - 신약
 * - 중화
 *
 * 를 판정하지 않는다.
 */
@Component
public class DayMasterElementRelationAnalyzer {

    public DayMasterElementRelationAnalysis analyze(
            ElementStrengthAnalysis elementStrength
    ) {

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석이 필요합니다."
        );

        HeavenlyStem dayMaster =
                elementStrength
                        .dayMaster();

        FiveElement dayMasterElement =
                elementStrength
                        .dayMasterElement();

        List<DayMasterElementRelation> relations =
                new ArrayList<>();

        Map<
                FiveElement,
                DayMasterElementRelation
                > relationsByElement =
                new EnumMap<>(
                        FiveElement.class
                );

        Map<
                DayMasterElementRole,
                FiveElement
                > elementByRole =
                new EnumMap<>(
                        DayMasterElementRole.class
                );

        for (
                FiveElement targetElement
                : FiveElement.values()
        ) {

            DayMasterElementRole role =
                    determineRole(
                            dayMasterElement,
                            targetElement
                    );

            int elementPower =
                    elementStrength
                            .powerOf(
                                    targetElement
                            );

            int shareBasisPoints =
                    elementStrength
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    targetElement
                            );

            DayMasterElementRelation relation =
                    new DayMasterElementRelation(
                            dayMaster,
                            dayMasterElement,
                            targetElement,
                            role,
                            elementPower,
                            shareBasisPoints
                    );

            relations.add(
                    relation
            );

            relationsByElement.put(
                    targetElement,
                    relation
            );

            FiveElement previous =
                    elementByRole.put(
                            role,
                            targetElement
                    );

            /*
             * 오행 생극 체계상
             * 하나의 일간 기준으로 각 역할은
             * 정확히 하나의 오행에만 대응한다.
             */
            if (
                    previous != null
            ) {

                throw new IllegalStateException(
                        "동일한 일간 역할에 둘 이상의 오행이 배정되었습니다: "
                                + role
                );
            }
        }

        return new DayMasterElementRelationAnalysis(
                dayMaster,
                dayMasterElement,
                relations,
                relationsByElement,
                elementByRole
        );
    }

    private DayMasterElementRole determineRole(

            FiveElement dayMasterElement,

            FiveElement targetElement

    ) {

        /*
         * 같은 오행.
         *
         * 비겁.
         */
        if (
                dayMasterElement
                        == targetElement
        ) {

            return DayMasterElementRole
                    .COMPANION;
        }

        /*
         * target이 일간을 생한다.
         *
         * 인성.
         *
         * 예:
         *
         * 木生火
         *
         * 火 일간에게
         * 木 = 인성.
         */
        if (
                produces(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterElementRole
                    .RESOURCE;
        }

        /*
         * 일간이 target을 생한다.
         *
         * 식상.
         *
         * 예:
         *
         * 火生土
         *
         * 火 일간에게
         * 土 = 식상.
         */
        if (
                produces(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterElementRole
                    .OUTPUT;
        }

        /*
         * 일간이 target을 극한다.
         *
         * 재성.
         *
         * 예:
         *
         * 火克金
         *
         * 火 일간에게
         * 金 = 재성.
         */
        if (
                controls(
                        dayMasterElement,
                        targetElement
                )
        ) {

            return DayMasterElementRole
                    .WEALTH;
        }

        /*
         * target이 일간을 극한다.
         *
         * 관살.
         *
         * 예:
         *
         * 水克火
         *
         * 火 일간에게
         * 水 = 관살.
         */
        if (
                controls(
                        targetElement,
                        dayMasterElement
                )
        ) {

            return DayMasterElementRole
                    .OFFICER;
        }

        throw new IllegalStateException(
                "일간과 대상 오행의 관계를 판정할 수 없습니다: "
                        + dayMasterElement
                        + " / "
                        + targetElement
        );
    }

    /**
     * 오행 상생.
     */
    private boolean produces(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

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

    /**
     * 오행 상극.
     */
    private boolean controls(

            FiveElement source,

            FiveElement target

    ) {

        return switch (
                source
                ) {

            case WOOD ->
                    target
                            == FiveElement.EARTH;

            case FIRE ->
                    target
                            == FiveElement.METAL;

            case EARTH ->
                    target
                            == FiveElement.WATER;

            case METAL ->
                    target
                            == FiveElement.WOOD;

            case WATER ->
                    target
                            == FiveElement.FIRE;
        };
    }
}