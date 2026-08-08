package com.dajeom.analyzer;

import com.dajeom.model.CombinationTransformationAnalysis;
import com.dajeom.model.MediationAnalysis;
import com.dajeom.model.MediationCase;
import com.dajeom.model.NatalChartAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.model.TemperatureAnalysis;
import com.dajeom.model.UsefulGodAnalysis;
import com.dajeom.rule.UsefulGodRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.FiveElement;
import com.dajeom.type.StrengthLevel;
import com.dajeom.type.UsefulGodMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 용신/희신/기신 후보 분석기.
 *
 * 현재 고려:
 *
 * - 억부
 * - 조후
 * - 통관
 * - 격국
 * - 합화 가능성
 *
 * 서로 다른 방법이 다른 오행을 요구하더라도
 * 하나를 임의로 삭제하지 않는다.
 */
@Component
public class UsefulGodAnalyzer {

    public UsefulGodAnalysis analyze(
            NatalChartAnalysis natalChart,
            StructureAnalysis structureAnalysis,
            TemperatureAnalysis temperatureAnalysis,
            CombinationTransformationAnalysis
                    combinationAnalysis,
            MediationAnalysis mediationAnalysis
    ) {

        Objects.requireNonNull(
                natalChart,
                "원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                structureAnalysis,
                "격국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                temperatureAnalysis,
                "조후 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                combinationAnalysis,
                "합화 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                mediationAnalysis,
                "통관 분석 결과는 필수입니다."
        );

        FiveElement dayMasterElement =
                natalChart
                        .dayMaster()
                        .getElement();

        StrengthLevel strengthLevel =
                natalChart
                        .strengthAnalysis()
                        .level();

        UsefulGodRule.CandidateSet candidateSet =
                UsefulGodRule.createCandidates(
                        dayMasterElement,
                        strengthLevel
                );

        List<FiveElement> temperatureCandidates =
                temperatureAnalysis
                        .balancingCandidates();

        List<FiveElement> mediationCandidates =
                extractMediationCandidates(
                        mediationAnalysis
                );

        boolean methodAgreement =
                hasAgreement(
                        candidateSet
                                .yongshinCandidates(),
                        temperatureCandidates
                );

        boolean mediationAgreement =
                hasAgreement(
                        candidateSet
                                .yongshinCandidates(),
                        mediationCandidates
                );

        boolean relationAdjustmentRequired =
                combinationAnalysis
                        .requiresElementAdjustment();

        boolean temperatureAdjustmentRequired =
                temperatureAnalysis
                        .adjustmentRequired();

        boolean mediationAdjustmentRequired =
                mediationAnalysis
                        .hasConflict();

        AnalysisConfidence confidence =
                determineConfidence(
                        natalChart,
                        structureAnalysis,
                        temperatureAnalysis,
                        strengthLevel,
                        relationAdjustmentRequired,
                        methodAgreement,
                        mediationAgreement
                );

        /*
         * 아직 대운/세운과
         * 일부 고급 원국 판정이 남아 있으므로
         * 최종 확정하지 않는다.
         */
        boolean finalized =
                false;

        return new UsefulGodAnalysis(
                UsefulGodMethod.SUPPRESSION_SUPPORT,
                strengthLevel,
                candidateSet.yongshinCandidates(),
                candidateSet.huishinCandidates(),
                candidateSet.gishinCandidates(),
                temperatureCandidates,
                mediationCandidates,
                methodAgreement,
                mediationAgreement,
                confidence,
                relationAdjustmentRequired,
                temperatureAdjustmentRequired,
                mediationAdjustmentRequired,
                finalized,
                UsefulGodRule.RULE_VERSION
        );
    }

    /**
     * 통관 후보는 충돌 구조가 존재하는 경우
     * 그 사이를 연결하는 오행으로 본다.
     *
     * 현재 원국에 충분히 있든 부족하든
     * 통관 관점의 의미가 있으므로 후보로 보관한다.
     */
    private List<FiveElement>
    extractMediationCandidates(
            MediationAnalysis mediationAnalysis
    ) {

        return mediationAnalysis
                .cases()
                .stream()
                .map(
                        MediationCase::mediatorElement
                )
                .distinct()
                .toList();
    }

    private boolean hasAgreement(
            List<FiveElement> first,
            List<FiveElement> second
    ) {

        if (
                first.isEmpty()
                        || second.isEmpty()
        ) {

            return false;
        }

        return first
                .stream()
                .anyMatch(
                        second::contains
                );
    }

    private AnalysisConfidence
    determineConfidence(
            NatalChartAnalysis natalChart,
            StructureAnalysis structureAnalysis,
            TemperatureAnalysis temperatureAnalysis,
            StrengthLevel strengthLevel,
            boolean relationAdjustmentRequired,
            boolean methodAgreement,
            boolean mediationAgreement
    ) {

        if (
                !natalChart.hasHourPillar()
        ) {

            return AnalysisConfidence.LOW;
        }

        if (
                relationAdjustmentRequired
        ) {

            /*
             * 합화에 의해 실제 오행 구조가
             * 변할 가능성이 있는데 아직
             * 점수 자체를 재계산하지 않았기 때문에
             * 보수적으로 LOW.
             */
            return AnalysisConfidence.LOW;
        }

        if (
                strengthLevel
                        == StrengthLevel.BALANCED
        ) {

            return AnalysisConfidence.LOW;
        }

        if (
                structureAnalysis.confidence()
                        == AnalysisConfidence.LOW
        ) {

            return AnalysisConfidence.LOW;
        }

        if (
                temperatureAnalysis.confidence()
                        == AnalysisConfidence.LOW
        ) {

            return AnalysisConfidence.LOW;
        }

        /*
         * 억부와 조후 또는 억부와 통관이
         * 같은 오행 방향을 가리킨다면
         * 현재 후보의 근거가 강해진다.
         */
        if (
                methodAgreement
                        || mediationAgreement
        ) {

            return AnalysisConfidence.HIGH;
        }

        return AnalysisConfidence.MEDIUM;
    }
}