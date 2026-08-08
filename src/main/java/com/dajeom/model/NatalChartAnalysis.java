package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.StrengthLevel;

import java.util.Objects;

/**
 * 사주 원국의 통합 분석 결과.
 *
 * 지금까지 계산된 원국 분석 결과를
 * 하나의 Domain Model로 통합한다.
 *
 * 포함:
 *
 * 1. 사주 네 기둥
 * 2. 일간
 * 3. 십성
 * 4. 지장간
 * 5. 기본 오행 분포
 * 6. 가중 오행 세력
 * 7. 일간 강약
 * 8. 천간 관계
 * 9. 지지 관계
 *
 * 아직 포함하지 않는 것:
 *
 * - 격국
 * - 용신
 * - 희신
 * - 기신
 * - 조후
 * - 대운
 * - 세운
 * - 최종 상담 해석
 *
 * 이 모델을 기반으로
 * 이후 고차원 Analyzer들이 동작한다.
 */
public record NatalChartAnalysis(

        /**
         * 원국의 기본 계산 결과.
         *
         * FourPillars,
         * 일간,
         * 십성,
         * 지장간,
         * 기본 오행 분포 포함.
         */
        NatalChartBaseAnalysis baseAnalysis,

        /**
         * 지장간과 월령 등의 가중치를
         * 반영한 오행 세력 분석.
         */
        ElementStrength elementStrength,

        /**
         * 일간의 기본 강약 분석.
         */
        StrengthAnalysis strengthAnalysis,

        /**
         * 천간 및 지지의 관계 분석.
         */
        RelationAnalysis relationAnalysis

) {

    public NatalChartAnalysis {

        Objects.requireNonNull(
                baseAnalysis,
                "원국 기초 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                elementStrength,
                "오행 세력 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                strengthAnalysis,
                "강약 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                relationAnalysis,
                "관계 분석 결과는 필수입니다."
        );
    }

    /**
     * 사주 네 기둥.
     */
    public FourPillars fourPillars() {

        return baseAnalysis.fourPillars();
    }

    /**
     * 일간.
     */
    public HeavenlyStem dayMaster() {

        return baseAnalysis.dayMaster();
    }

    /**
     * 출생시간을 알고 있어서
     * 시주까지 분석되었는지 확인한다.
     */
    public boolean hasHourPillar() {

        return baseAnalysis.hasHourPillar();
    }

    /**
     * 현재 기본 강약 단계.
     */
    public StrengthLevel strengthLevel() {

        return strengthAnalysis.level();
    }

    /**
     * 천간 관계가 하나라도 존재하는지 확인한다.
     */
    public boolean hasStemRelations() {

        return !relationAnalysis
                .stemRelations()
                .isEmpty();
    }

    /**
     * 지지 관계가 하나라도 존재하는지 확인한다.
     */
    public boolean hasBranchRelations() {

        return !relationAnalysis
                .branchRelations()
                .isEmpty();
    }

    /**
     * 원국에 천간/지지 관계가
     * 하나라도 존재하는지 확인한다.
     */
    public boolean hasRelations() {

        return hasStemRelations()
                || hasBranchRelations();
    }
}