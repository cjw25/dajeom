package com.dajeom.model;

import java.util.Objects;

/**
 * 원국의 정밀 고차 분석 결과.
 *
 * =================================================
 *
 * 현재 통합하는 분석 계층:
 *
 * 기본 원국 분석
 *
 * +
 *
 * 정밀화 3차
 * ElementStrengthAnalysis
 *
 * +
 *
 * 정밀화 4차
 * DayMasterStrengthAnalysis
 *
 * +
 *
 * 정밀화 5차
 * StructureAnalysis
 *
 * +
 *
 * 정밀화 6차
 * ClimateHealthAnalysis
 *
 * +
 *
 * 정밀화 7차
 * UsefulGodAnalysis
 *
 * +
 *
 * 정밀화 8차
 * RelationAnalysis
 *
 * +
 *
 * 정밀화 9차
 * CombinationTransformationAnalysis
 *
 * =================================================
 *
 * 주의:
 *
 * RelationAnalysis는 coreAnalysis 내부에 이미 포함되어 있으므로
 * 별도 record component로 중복 보관하지 않는다.
 *
 * ElementStrengthAnalysis 역시
 * DayMasterStrengthAnalysis 내부에 포함되어 있으므로
 * 중복 보관하지 않는다.
 *
 * 이 모델에서는 정밀화 이전의:
 *
 * - TemperatureAnalysis
 * - MediationAnalysis
 *
 * 를 사용하지 않는다.
 *
 * 조후·병약·통관은 6차 ClimateHealthAnalysis가 담당한다.
 */
public record NatalChartAdvancedAnalysis(

        /**
         * 기본 원국 분석.
         *
         * FourPillars 및
         * 정밀화 8차 RelationAnalysis를 포함한다.
         */
        NatalChartAnalysis coreAnalysis,

        /**
         * 정밀화 4차 신강·신약 분석.
         *
         * 내부에 정밀화 3차
         * ElementStrengthAnalysis도 포함한다.
         */
        DayMasterStrengthAnalysis dayMasterStrengthAnalysis,

        /**
         * 정밀화 5차 격국 분석.
         */
        StructureAnalysis structureAnalysis,

        /**
         * 정밀화 6차
         * 조후·병약·통관 통합 분석.
         */
        ClimateHealthAnalysis climateHealthAnalysis,

        /**
         * 정밀화 7차
         * 용신·희신·기신 통합 분석.
         */
        UsefulGodAnalysis usefulGodAnalysis,

        /**
         * 정밀화 9차
         * 합화 성립 및 효과 분석.
         */
        CombinationTransformationAnalysis
        combinationTransformationAnalysis

) {

    public NatalChartAdvancedAnalysis {

        Objects.requireNonNull(
                coreAnalysis,
                "원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                dayMasterStrengthAnalysis,
                "정밀화 신강·신약 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "격국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                climateHealthAnalysis,
                "조후·병약·통관 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                usefulGodAnalysis,
                "용신 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                combinationTransformationAnalysis,
                "합화 분석 결과는 필수입니다."
        );

        validateDayMasterConsistency(
                coreAnalysis,
                dayMasterStrengthAnalysis,
                structureAnalysis,
                climateHealthAnalysis,
                usefulGodAnalysis,
                combinationTransformationAnalysis
        );
    }

    /**
     * 각 정밀화 단계가
     * 동일한 일간을 기준으로 계산되었는지 확인한다.
     */
    private static void validateDayMasterConsistency(

            NatalChartAnalysis coreAnalysis,

            DayMasterStrengthAnalysis
                    dayMasterStrengthAnalysis,

            StructureAnalysis structureAnalysis,

            ClimateHealthAnalysis
                    climateHealthAnalysis,

            UsefulGodAnalysis usefulGodAnalysis,

            CombinationTransformationAnalysis
                    combinationTransformationAnalysis

    ) {

        if (
                coreAnalysis.dayMaster()
                        != dayMasterStrengthAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "기본 원국 분석과 신강·신약 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                coreAnalysis.dayMaster()
                        != structureAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "기본 원국 분석과 격국 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                coreAnalysis.dayMaster()
                        != climateHealthAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "기본 원국 분석과 조후·병약·통관 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                coreAnalysis.dayMaster()
                        != usefulGodAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "기본 원국 분석과 용신 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                coreAnalysis.dayMaster()
                        != combinationTransformationAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "기본 원국 분석과 합화 분석의 일간이 일치하지 않습니다."
            );
        }
    }

    /**
     * 사주 네 기둥.
     */
    public FourPillars fourPillars() {

        return coreAnalysis.fourPillars();
    }

    /**
     * 정밀화 3차 오행 세력 분석.
     */
    public ElementStrengthAnalysis elementStrengthAnalysis() {

        return dayMasterStrengthAnalysis
                .elementStrengthAnalysis();
    }

    /**
     * 정밀화 8차 관계 분석.
     */
    public RelationAnalysis relationAnalysis() {

        return coreAnalysis
                .relationAnalysis();
    }

    /**
     * 시주까지 존재하는 완전한 원국인지.
     */
    public boolean hasHourPillar() {

        return coreAnalysis.hasHourPillar();
    }
}