package com.dajeom.analyzer;

import com.dajeom.model.ClimateHealthAnalysis;
import com.dajeom.model.EarthlyBranchPairRelation;
import com.dajeom.model.HeavenlyStemPairRelation;
import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.InterpretationSignalRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationEvidenceType;
import com.dajeom.type.PillarPosition;
import com.dajeom.type.StrengthLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 원국 자체에서
 * 상담용 해석 근거를 생성한다.
 *
 * 사용자 문장을 만드는 Analyzer가 아니다.
 *
 * 명리 계산 결과를
 * 상담 분야에 사용할 수 있는
 * InterpretationEvidence로 변환한다.
 *
 * =================================================
 *
 * 현재 정밀화 구조:
 *
 * 신강·신약
 * +
 * 5차 StructureAnalysis
 * +
 * 6차 ClimateHealthAnalysis
 * +
 * 8차 RelationAnalysis
 *
 * 를 사용한다.
 *
 * =================================================
 *
 * 더 이상 사용하지 않는 구형 모델:
 *
 * - StructureAnalysis.type()
 * - StructureAnalysis.confidence()
 * - TemperatureAnalysis
 * - StemRelation
 * - BranchRelation
 *
 * =================================================
 *
 * 관계 분석은 8차의:
 *
 * - HeavenlyStemPairRelation
 * - EarthlyBranchPairRelation
 *
 * 관계망을 사용한다.
 */
@Component
public class NatalInterpretationSignalAnalyzer {

    public List<InterpretationEvidence> analyze(
            NatalChartAdvancedAnalysis natalChart
    ) {

        Objects.requireNonNull(
                natalChart,
                "고차 원국 분석 결과는 필수입니다."
        );

        List<InterpretationEvidence> result =
                new ArrayList<>();

        /*
         * =================================================
         * 1.
         * 신강·신약
         * =================================================
         */
        addStrengthEvidence(
                natalChart,
                result
        );

        /*
         * =================================================
         * 2.
         * 격국
         * =================================================
         */
        addStructureEvidence(
                natalChart,
                result
        );

        /*
         * =================================================
         * 3.
         * 조후·병약·통관
         * =================================================
         */
        addClimateHealthEvidence(
                natalChart,
                result
        );

        /*
         * =================================================
         * 4.
         * 원국 관계망
         * =================================================
         */
        addNatalRelations(
                natalChart,
                result
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 신강·신약은 좋고 나쁨이 아니라
     * 기본적인 에너지 운용 방식에 대한
     * 성향 근거로 사용한다.
     */
    private void addStrengthEvidence(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        StrengthLevel level =
                natalChart
                        .coreAnalysis()
                        .strengthAnalysis()
                        .level();

        result.add(
                new InterpretationEvidence(
                        ConsultationCategory.PERSONALITY,
                        InterpretationEvidenceType.STRENGTH,
                        "STRENGTH_"
                                + level.name(),
                        "일간 강약: "
                                + level.getKoreanName(),
                        0.0,
                        0.0,
                        AnalysisConfidence.MEDIUM
                )
        );
    }

    /**
     * =================================================
     * 5차 격국
     * =================================================
     *
     * 과거에는:
     *
     * StructureAnalysis.type()
     * StructureAnalysis.confidence()
     *
     * 로 하나의 타입과 신뢰도로 축약했다.
     *
     * 현재 5차에서는:
     *
     * - primaryCandidate
     * - secondaryCandidates
     * - StructureStatus
     * - damage
     * - purity
     * - special review
     *
     * 등을 구조적으로 보존한다.
     *
     * 따라서 여기서는
     * 실제 선택된 주격 이름과
     * 최종 StructureStatus를 사용한다.
     */
    private void addStructureEvidence(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        StructureAnalysis structureAnalysis =
                natalChart
                        .structureAnalysis();

        String structureCode =
                "STRUCTURE_"
                        + structureAnalysis
                        .status()
                        .name();

        String summary =
                "원국 구조: "
                        + structureAnalysis
                        .primaryNameKorean()
                        + " / "
                        + structureAnalysis
                        .statusKorean();

        result.add(
                new InterpretationEvidence(
                        ConsultationCategory.CAREER,
                        InterpretationEvidenceType.STRUCTURE,
                        structureCode,
                        summary,
                        0.0,
                        0.0,

                        /*
                         * 현재 StructureAnalysis는
                         * 단일 confidence로 축약하지 않는다.
                         *
                         * 성립/파격/순도/보조격 등의
                         * Evidence가 구조적으로 따로 존재하므로
                         * 이 변환 계층에서는 MEDIUM으로 유지한다.
                         */
                        AnalysisConfidence.MEDIUM
                )
        );
    }

    /**
     * =================================================
     * 6차 조후·병약·통관
     * =================================================
     *
     * 구형 TemperatureAnalysis를 사용하지 않는다.
     *
     * 현재:
     *
     * ClimateEnvironmentAnalysis
     * ClimateAdjustmentNeedAnalysis
     * ClimateElementCandidateAnalysis
     * PathologyAnalysis
     * RemedyAnalysis
     * PassageNeedAnalysis
     * PassageCandidateAnalysis
     * ClimateHealthIntegrationAnalysis
     * ClimateHealthCrossValidationAnalysis
     *
     * 를 거쳐 생성된
     * 최종 ClimateHealthAnalysis를 사용한다.
     *
     * =================================================
     *
     * 건강을 직접적으로 좋다/나쁘다고
     * 판정하는 Evidence로 사용하지 않는다.
     *
     * 생활 리듬,
     * 균형 관리,
     * 조후·병약·통관 상태를 설명하기 위한
     * 참고 근거로만 사용한다.
     */
    private void addClimateHealthEvidence(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        ClimateHealthAnalysis climateHealthAnalysis =
                natalChart
                        .climateHealthAnalysis();

        String statusName =
                climateHealthAnalysis
                        .status()
                        .name();

        result.add(
                new InterpretationEvidence(
                        ConsultationCategory.HEALTH_TENDENCY,

                        /*
                         * InterpretationEvidenceType의
                         * 기존 출력 계약은 유지한다.
                         *
                         * 내부 계산 Source만
                         * TemperatureAnalysis에서
                         * ClimateHealthAnalysis로 변경한다.
                         */
                        InterpretationEvidenceType.TEMPERATURE,

                        "CLIMATE_HEALTH_"
                                + statusName,

                        "조후·병약·통관 상태: "
                                + statusName,

                        /*
                         * 6차 최종 상태를
                         * 단순 길흉 점수로 변환하지 않는다.
                         */
                        0.0,
                        0.0,

                        /*
                         * ClimateHealthAnalysis 역시
                         * 구형 단일 confidence를 사용하지 않는다.
                         */
                        AnalysisConfidence.MEDIUM
                )
        );
    }

    /**
     * =================================================
     * 8차 원국 관계망
     * =================================================
     *
     * 구형:
     *
     * relationAnalysis.stemRelations()
     * relationAnalysis.branchRelations()
     *
     * 를 사용하지 않는다.
     *
     * 현재 8차 공식 Pair 관계망:
     *
     * heavenlyStemPairRelations()
     * earthlyBranchPairRelations()
     *
     * 를 사용한다.
     *
     * =================================================
     *
     * 8차 관계망은 하나의 Pair에
     * 여러 관계가 동시에 존재할 수 있다.
     *
     * 이 Interpretation 단계에서는
     * Pair 자체를 하나의 관계 근거로 보존하고,
     * 세부 합·충·형·파·해의 deterministic 판정은
     * 원본 RelationAnalysis에 그대로 남겨 둔다.
     */
    private void addNatalRelations(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        RelationAnalysis relationAnalysis =
                natalChart
                        .relationAnalysis();

        addHeavenlyStemRelations(
                relationAnalysis,
                result
        );

        addEarthlyBranchRelations(
                relationAnalysis,
                result
        );
    }

    /**
     * 천간 Pair 관계.
     */
    private void addHeavenlyStemRelations(
            RelationAnalysis relationAnalysis,
            List<InterpretationEvidence> result
    ) {

        for (
                HeavenlyStemPairRelation relation
                : relationAnalysis
                .heavenlyStemPairRelations()
        ) {

            PillarPosition firstPosition =
                    toPillarPosition(
                            relation
                                    .firstPosition()
                                    .name()
                    );

            PillarPosition secondPosition =
                    toPillarPosition(
                            relation
                                    .secondPosition()
                                    .name()
                    );

            List<PillarPosition> positions =
                    List.of(
                            firstPosition,
                            secondPosition
                    );

            List<ConsultationCategory> categories =
                    InterpretationSignalRule
                            .categoriesForPositions(
                                    positions
                            );

            String relationKey =
                    relation
                            .firstPosition()
                            .name()
                            + "_"
                            + relation
                            .secondPosition()
                            .name();

            String summary =
                    "원국 천간 관계: "
                            + relation
                            .firstPosition()
                            .name()
                            + " ↔ "
                            + relation
                            .secondPosition()
                            .name();

            for (
                    ConsultationCategory category
                    : categories
            ) {

                /*
                 * 8차 PairRelation은
                 * 합·충·극이 중첩될 수 있다.
                 *
                 * 여기서 다시 길흉을 결정하지 않고
                 * 관계 존재 자체를 복합 Evidence로 둔다.
                 */
                result.add(
                        new InterpretationEvidence(
                                category,
                                InterpretationEvidenceType
                                        .NATAL_RELATION,
                                "NATAL_STEM_RELATION_"
                                        + relationKey,
                                summary,
                                0.3,
                                0.3,
                                AnalysisConfidence.MEDIUM
                        )
                );
            }
        }
    }

    /**
     * 지지 Pair 관계.
     *
     * =================================================
     *
     * 현재 EarthlyBranchPairRelation에는
     * 하나의 Pair에:
     *
     * 육합
     * 삼합
     * 방합
     * 충
     * 형
     * 파
     * 해
     *
     * 등이 중첩될 수 있다.
     *
     * 따라서 구형 BranchRelation 하나로
     * 다시 축약하지 않는다.
     */
    private void addEarthlyBranchRelations(
            RelationAnalysis relationAnalysis,
            List<InterpretationEvidence> result
    ) {

        for (
                EarthlyBranchPairRelation relation
                : relationAnalysis
                .earthlyBranchPairRelations()
        ) {

            PillarPosition firstPosition =
                    toPillarPosition(
                            relation
                                    .firstPosition()
                                    .name()
                    );

            PillarPosition secondPosition =
                    toPillarPosition(
                            relation
                                    .secondPosition()
                                    .name()
                    );

            List<PillarPosition> positions =
                    List.of(
                            firstPosition,
                            secondPosition
                    );

            List<ConsultationCategory> categories =
                    InterpretationSignalRule
                            .categoriesForPositions(
                                    positions
                            );

            String relationKey =
                    relation
                            .firstPosition()
                            .name()
                            + "_"
                            + relation
                            .secondPosition()
                            .name();

            String summary =
                    "원국 지지 관계: "
                            + relation
                            .firstPosition()
                            .name()
                            + " ↔ "
                            + relation
                            .secondPosition()
                            .name();

            for (
                    ConsultationCategory category
                    : categories
            ) {

                /*
                 * 하나의 Pair 안에 긍정적 관계와
                 * 긴장 관계가 같이 존재할 수 있으므로
                 * 이 단계에서는 어느 한쪽으로
                 * 과도하게 단정하지 않는다.
                 */
                result.add(
                        new InterpretationEvidence(
                                category,
                                InterpretationEvidenceType
                                        .NATAL_RELATION,
                                "NATAL_BRANCH_RELATION_"
                                        + relationKey,
                                summary,
                                0.3,
                                0.3,
                                AnalysisConfidence.MEDIUM
                        )
                );
            }
        }
    }

    /**
     * =================================================
     * 정밀화 8차 위치 Enum
     * ->
     * 기존 Interpretation 계층 PillarPosition
     * =================================================
     *
     * 8차에서는:
     *
     * VisibleStemPosition
     * EarthlyBranchPosition
     *
     * 을 별도로 사용한다.
     *
     * InterpretationSignalRule은
     * PillarPosition을 사용하므로
     * YEAR / MONTH / DAY / HOUR 이름을 기준으로
     * 경계에서 변환한다.
     */
    private PillarPosition toPillarPosition(
            String positionName
    ) {

        Objects.requireNonNull(
                positionName,
                "궁위 이름은 필수입니다."
        );

        return PillarPosition.valueOf(
                positionName
        );
    }
}