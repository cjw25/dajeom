package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PillarPosition;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 사주 원국의 기초 분석 결과.
 *
 * 이 모델은 아직 명리학적인 최종 판정을 하지 않는다.
 *
 * 포함:
 *
 * - FourPillars
 * - 일간
 * - 각 기둥의 십성
 * - 각 지지의 지장간
 * - 지장간별 십성
 * - 기본 오행 분포
 *
 * 추후 Analyzer들이 이 모델을 기반으로:
 *
 * - 신강·신약
 * - 합충형파해
 * - 격국
 * - 용신
 *
 * 등을 분석한다.
 */
public record NatalChartBaseAnalysis(

        FourPillars fourPillars,

        HeavenlyStem dayMaster,

        List<PillarAnalysis> pillars,

        ElementDistribution elementDistribution

) {

    public NatalChartBaseAnalysis {

        Objects.requireNonNull(
                fourPillars,
                "사주 원국은 필수입니다."
        );

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                pillars,
                "기둥 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                elementDistribution,
                "오행 분포는 필수입니다."
        );

        pillars =
                List.copyOf(
                        pillars
                );
    }

    /**
     * 위치를 기준으로 기둥 분석 결과를 찾는다.
     */
    public Optional<PillarAnalysis> findPillar(
            PillarPosition position
    ) {

        Objects.requireNonNull(
                position,
                "기둥 위치는 필수입니다."
        );

        return pillars
                .stream()
                .filter(
                        pillarAnalysis ->
                                pillarAnalysis.position()
                                        == position
                )
                .findFirst();
    }

    /**
     * 출생시간을 알고 있어
     * 시주 분석까지 포함되어 있는지 확인한다.
     */
    public boolean hasHourPillar() {

        return fourPillars.hasHourPillar();
    }
}