package com.dajeom.analyzer;

import com.dajeom.model.ClimateAdjustmentNeedAnalysis;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.ClimateEnvironmentAnalysis;
import com.dajeom.model.ClimateEnvironmentEvidence;
import com.dajeom.model.DayMasterElementRelationAnalysis;
import com.dajeom.model.DayMasterStrengthAnalysis;
import com.dajeom.model.ElementStrengthAnalysis;
import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.PathologyEvidence;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.StructureStatusEvidence;
import com.dajeom.rule.PathologyRule;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.PathologySeverity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-4.
 *
 * 원국 안에서 구조적인 병(病) Evidence를 탐지한다.
 *
 * 여기서 병(病)은 의학적 질병이 아니다.
 *
 * 명리 구조의:
 *
 * - 오행 편중
 * - 신약 일간의 과도한 설기
 * - 신약 일간의 재성 부담
 * - 신약 일간의 관살 압박
 * - 신강 일간의 생조 과다
 * - 극단 한난조습
 * - 조후 보정 충돌
 * - 주격 파격
 * - 주격 불안정
 *
 * 을 의미한다.
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 1.
 * 가장 많은 오행 하나를
 *    무조건 병으로 지정하지 않는다.
 *
 * 2.
 * 오행 과다와
 *    일간이 그 오행을 감당하지 못하는 문제를
 *    서로 다른 Evidence로 보존한다.
 *
 * 3.
 * 조후 불균형과
 *    격국 파격 역시 별개로 보존한다.
 *
 * 4.
 * 병 구조가 여러 개 존재해도
 *    하나의 총점으로 합산하지 않는다.
 *
 * 5.
 * 약(藥)은 아직 결정하지 않는다.
 *    그 책임은 6-5다.
 */
@Component
public class PathologyAnalyzer {

    private final PathologyRule
            pathologyRule;

    public PathologyAnalyzer(
            PathologyRule pathologyRule
    ) {

        this.pathologyRule =
                Objects.requireNonNull(
                        pathologyRule,
                        "PathologyRule이 필요합니다."
                );
    }

    /**
     * 5차 StructureAnalysis와
     * 6-3 조후 오행 후보 결과를 이용한다.
     */
    public PathologyAnalysis analyze(

            StructureAnalysis structure,

            ClimateElementCandidateAnalysis climateCandidates

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidates,
                "조후 오행 후보 분석이 필요합니다."
        );

        validateConsistency(
                structure,
                climateCandidates
        );

        DayMasterStrengthAnalysis strength =
                structure
                        .dayMasterStrengthAnalysis();

        ElementStrengthAnalysis elementStrength =
                strength
                        .elementStrengthAnalysis();

        ClimateAdjustmentNeedAnalysis climateNeed =
                climateCandidates
                        .adjustmentNeedAnalysis();

        ClimateEnvironmentAnalysis climate =
                climateNeed
                        .climateEnvironmentAnalysis();

        ClimateEnvironmentEvidence climateEvidence =
                climate
                        .evidence();

        List<PathologyEvidence> evidences =
                new ArrayList<>();

        /*
         * ================================================
         * 1.
         * 오행 과다집중
         * ================================================
         */
        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int share =
                    elementStrength
                            .elementPowerAnalysis()
                            .shareBasisPointsOf(
                                    element
                            );

            PathologySeverity severity =
                    pathologyRule
                            .determineElementConcentrationSeverity(
                                    share
                            );

            addEvidenceIfExists(
                    evidences,
                    PathologyPatternType
                            .ELEMENT_OVERCONCENTRATION,
                    severity,
                    List.of(
                            element
                    ),
                    share,
                    PathologyRule
                            .ELEMENT_CONCENTRATION_THRESHOLD
            );
        }

        /*
         * ================================================
         * 2.
         * 신약 일간의 식상 설기 과다
         * ================================================
         */
        FiveElement outputElement =
                strength
                        .elementRelationAnalysis()
                        .outputElement();

        int outputShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                outputElement
                        );

        int outputPressure =
                pathologyRule
                        .calculateEffectivePressureBasisPoints(
                                DayMasterElementRole.OUTPUT,
                                outputShare
                        );

        PathologySeverity outputSeverity =
                pathologyRule
                        .determineWeakDayMasterPressureSeverity(
                                strength.level(),
                                outputPressure
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .OUTPUT_DRAIN_OVERLOAD,
                outputSeverity,
                List.of(
                        outputElement
                ),
                outputPressure,
                PathologyRule
                        .DAY_MASTER_PRESSURE_THRESHOLD
        );

        /*
         * ================================================
         * 3.
         * 신약 일간의 재성 부담
         * ================================================
         */
        FiveElement wealthElement =
                strength
                        .elementRelationAnalysis()
                        .wealthElement();

        int wealthShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                wealthElement
                        );

        int wealthPressure =
                pathologyRule
                        .calculateEffectivePressureBasisPoints(
                                DayMasterElementRole.WEALTH,
                                wealthShare
                        );

        PathologySeverity wealthSeverity =
                pathologyRule
                        .determineWeakDayMasterPressureSeverity(
                                strength.level(),
                                wealthPressure
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .WEALTH_EXERTION_OVERLOAD,
                wealthSeverity,
                List.of(
                        wealthElement
                ),
                wealthPressure,
                PathologyRule
                        .DAY_MASTER_PRESSURE_THRESHOLD
        );

        /*
         * ================================================
         * 4.
         * 신약 일간의 관살 압박
         * ================================================
         */
        FiveElement officerElement =
                strength
                        .elementRelationAnalysis()
                        .officerElement();

        int officerShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                officerElement
                        );

        int officerPressure =
                pathologyRule
                        .calculateEffectivePressureBasisPoints(
                                DayMasterElementRole.OFFICER,
                                officerShare
                        );

        PathologySeverity officerSeverity =
                pathologyRule
                        .determineWeakDayMasterPressureSeverity(
                                strength.level(),
                                officerPressure
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .OFFICER_CONTROL_OVERLOAD,
                officerSeverity,
                List.of(
                        officerElement
                ),
                officerPressure,
                PathologyRule
                        .DAY_MASTER_PRESSURE_THRESHOLD
        );

        /*
         * ================================================
         * 5.
         * 신강 일간의 비겁·인성 생조 과다
         * ================================================
         */
        FiveElement companionElement =
                strength
                        .elementRelationAnalysis()
                        .companionElement();

        FiveElement resourceElement =
                strength
                        .elementRelationAnalysis()
                        .resourceElement();

        int companionShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                companionElement
                        );

        int resourceShare =
                elementStrength
                        .elementPowerAnalysis()
                        .shareBasisPointsOf(
                                resourceElement
                        );

        int combinedSupportShare =
                Math.min(
                        10_000,
                        companionShare
                                + resourceShare
                );

        PathologySeverity supportSeverity =
                pathologyRule
                        .determineSupportOverconcentrationSeverity(
                                strength.level(),
                                combinedSupportShare
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .SUPPORT_OVERCONCENTRATION,
                supportSeverity,
                distinctElements(
                        companionElement,
                        resourceElement
                ),
                combinedSupportShare,
                PathologyRule
                        .SUPPORT_OVERCONCENTRATION_THRESHOLD
        );

        /*
         * ================================================
         * 6.
         * 극단 한기
         * ================================================
         */
        if (
                climate
                        .temperatureState()
                        .severeCold()
        ) {

            PathologySeverity severity =
                    pathologyRule
                            .determineExtremeClimateSeverity(
                                    climateNeed
                                            .temperatureNeed()
                            );

            addEvidenceIfExists(
                    evidences,
                    PathologyPatternType
                            .EXTREME_COLD,
                    severity,
                    List.of(),
                    Math.abs(
                            climateEvidence
                                    .finalTemperatureBasisPoints()
                    ),
                    6_000
            );
        }

        /*
         * ================================================
         * 7.
         * 극단 열기
         * ================================================
         */
        if (
                climate
                        .temperatureState()
                        .severeHeat()
        ) {

            PathologySeverity severity =
                    pathologyRule
                            .determineExtremeClimateSeverity(
                                    climateNeed
                                            .temperatureNeed()
                            );

            addEvidenceIfExists(
                    evidences,
                    PathologyPatternType
                            .EXTREME_HEAT,
                    severity,
                    List.of(),
                    Math.abs(
                            climateEvidence
                                    .finalTemperatureBasisPoints()
                    ),
                    6_000
            );
        }

        /*
         * ================================================
         * 8.
         * 극단 건조
         * ================================================
         */
        if (
                climate
                        .moistureState()
                        .severeDry()
        ) {

            PathologySeverity severity =
                    pathologyRule
                            .determineExtremeClimateSeverity(
                                    climateNeed
                                            .moistureNeed()
                            );

            addEvidenceIfExists(
                    evidences,
                    PathologyPatternType
                            .EXTREME_DRY,
                    severity,
                    List.of(),
                    Math.abs(
                            climateEvidence
                                    .finalMoistureBasisPoints()
                    ),
                    5_000
            );
        }

        /*
         * ================================================
         * 9.
         * 극단 습기
         * ================================================
         */
        if (
                climate
                        .moistureState()
                        .severeWet()
        ) {

            PathologySeverity severity =
                    pathologyRule
                            .determineExtremeClimateSeverity(
                                    climateNeed
                                            .moistureNeed()
                            );

            addEvidenceIfExists(
                    evidences,
                    PathologyPatternType
                            .EXTREME_WET,
                    severity,
                    List.of(),
                    Math.abs(
                            climateEvidence
                                    .finalMoistureBasisPoints()
                    ),
                    5_000
            );
        }

        /*
         * ================================================
         * 10.
         * 조후 보정 충돌
         *
         * 寒燥 / 熱濕
         * ================================================
         */
        PathologySeverity climateConflictSeverity =
                pathologyRule
                        .determineClimateCorrectionConflictSeverity(
                                climateNeed
                                        .correctionConflict(),
                                climateNeed
                                        .overallNeed()
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .CLIMATE_CORRECTION_CONFLICT,
                climateConflictSeverity,
                List.of(),
                0,
                0
        );

        /*
         * ================================================
         * 11.
         * 주격 파격 손상
         * ================================================
         */
        StructureStatusEvidence statusEvidence =
                structure
                        .statusEvidence();

        PathologySeverity structureDamageSeverity =
                pathologyRule
                        .determinePrimaryStructureDamageSeverity(
                                statusEvidence
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .PRIMARY_STRUCTURE_DAMAGE,
                structureDamageSeverity,
                List.of(),
                0,
                0
        );

        /*
         * ================================================
         * 12.
         * 주격 불안정
         * ================================================
         */
        PathologySeverity instabilitySeverity =
                pathologyRule
                        .determinePrimaryStructureInstabilitySeverity(
                                structure.status()
                        );

        addEvidenceIfExists(
                evidences,
                PathologyPatternType
                        .PRIMARY_STRUCTURE_INSTABILITY,
                instabilitySeverity,
                List.of(),
                0,
                0
        );

        /*
         * ================================================
         * 출력 정렬
         * ================================================
         *
         * 심각도 높은 병 구조부터 표시한다.
         *
         * 동일 심각도에서는
         * category / pattern enum 순으로
         * 안정적인 정렬을 유지한다.
         *
         * 점수 합산은 하지 않는다.
         */
        evidences.sort(
                Comparator
                        .comparingInt(
                                (
                                        PathologyEvidence evidence
                                ) ->
                                        evidence
                                                .severity()
                                                .rank()
                        )
                        .reversed()

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .category()
                                                .ordinal()
                        )

                        .thenComparingInt(
                                evidence ->
                                        evidence
                                                .patternType()
                                                .ordinal()
                        )
        );

        PathologySeverity overallSeverity =
                evidences
                        .stream()
                        .map(
                                PathologyEvidence
                                        ::severity
                        )
                        .max(
                                Comparator.comparingInt(
                                        PathologySeverity
                                                ::rank
                                )
                        )
                        .orElse(
                                PathologySeverity.NONE
                        );

        return new PathologyAnalysis(
                structure.dayMaster(),
                structure.monthBranch(),
                evidences,
                overallSeverity,
                structure.completeChart()
        );
    }

    private void addEvidenceIfExists(

            List<PathologyEvidence> evidences,

            PathologyPatternType patternType,

            PathologySeverity severity,

            List<FiveElement> relatedElements,

            int magnitudeBasisPoints,

            int thresholdBasisPoints

    ) {

        if (
                severity == null
                        || severity.none()
        ) {

            return;
        }

        evidences.add(
                new PathologyEvidence(
                        patternType,
                        severity,
                        relatedElements,
                        magnitudeBasisPoints,
                        thresholdBasisPoints
                )
        );
    }

    private List<FiveElement> distinctElements(

            FiveElement first,

            FiveElement second

    ) {

        Objects.requireNonNull(
                first,
                "첫 번째 오행이 필요합니다."
        );

        Objects.requireNonNull(
                second,
                "두 번째 오행이 필요합니다."
        );

        if (
                first == second
        ) {

            return List.of(
                    first
            );
        }

        return List.of(
                first,
                second
        );
    }

    private void validateConsistency(

            StructureAnalysis structure,

            ClimateElementCandidateAnalysis climateCandidates

    ) {

        if (
                structure.dayMaster()
                        != climateCandidates.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 조후 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                structure.monthBranch()
                        != climateCandidates.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 조후 분석의 월지가 일치하지 않습니다."
            );
        }

        if (
                structure.completeChart()
                        != climateCandidates.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "격국 분석과 조후 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}