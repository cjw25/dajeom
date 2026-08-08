package com.dajeom.analyzer;

import com.dajeom.model.BranchRelation;
import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.model.RelationAnalysis;
import com.dajeom.model.StemRelation;
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
 * 11개 상담 분야에 사용할 수 있는
 * 근거 데이터로 변환한다.
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

        addStrengthEvidence(
                natalChart,
                result
        );

        addStructureEvidence(
                natalChart,
                result
        );

        addTemperatureEvidence(
                natalChart,
                result
        );

        addNatalRelations(
                natalChart,
                result
        );

        return List.copyOf(
                result
        );
    }

    /**
     * 신강신약은 좋고 나쁨이 아니라
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
     * 격국 후보는 직업/사회활동 해석의
     * 기본 근거로 보관한다.
     *
     * 특정 격국 자체를 길흉으로 판단하지 않는다.
     */
    private void addStructureEvidence(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        result.add(
                new InterpretationEvidence(
                        ConsultationCategory.CAREER,
                        InterpretationEvidenceType.STRUCTURE,
                        "STRUCTURE_"
                                + natalChart
                                .structureAnalysis()
                                .type()
                                .name(),
                        "원국 구조: "
                                + natalChart
                                .structureAnalysis()
                                .type()
                                .getKoreanName(),
                        0.0,
                        0.0,
                        natalChart
                                .structureAnalysis()
                                .confidence()
                )
        );
    }

    /**
     * 조후는 건강을 단정하는 자료로
     * 사용하지 않는다.
     *
     * 건강 관리 경향 상담에서
     * 생활 리듬이나 균형 관리 정도를
     * 설명하기 위한 참고 근거로만 사용한다.
     */
    private void addTemperatureEvidence(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        boolean adjustmentRequired =
                natalChart
                        .temperatureAnalysis()
                        .adjustmentRequired();

        result.add(
                new InterpretationEvidence(
                        ConsultationCategory.HEALTH_TENDENCY,
                        InterpretationEvidenceType.TEMPERATURE,
                        adjustmentRequired
                                ? "TEMPERATURE_ADJUSTMENT_REQUIRED"
                                : "TEMPERATURE_BALANCED",
                        "조후 상태: "
                                + natalChart
                                .temperatureAnalysis()
                                .temperatureLevel()
                                .getKoreanName()
                                + " / "
                                + natalChart
                                .temperatureAnalysis()
                                .moistureLevel()
                                .getKoreanName(),
                        adjustmentRequired
                                ? 0.0
                                : 0.3,
                        adjustmentRequired
                                ? 0.6
                                : 0.0,
                        natalChart
                                .temperatureAnalysis()
                                .confidence()
                )
        );
    }

    private void addNatalRelations(
            NatalChartAdvancedAnalysis natalChart,
            List<InterpretationEvidence> result
    ) {

        RelationAnalysis relationAnalysis =
                natalChart
                        .coreAnalysis()
                        .relationAnalysis();

        for (
                StemRelation relation
                : relationAnalysis.stemRelations()
        ) {

            List<PillarPosition> positions =
                    List.of(
                            relation.firstPosition(),
                            relation.secondPosition()
                    );

            List<ConsultationCategory> categories =
                    InterpretationSignalRule
                            .categoriesForPositions(
                                    positions
                            );

            for (
                    ConsultationCategory category
                    : categories
            ) {

                /*
                 * 천간합은 무조건 좋은 것으로
                 * 판단하지 않고 복합적인 관계 근거로 둔다.
                 */
                result.add(
                        new InterpretationEvidence(
                                category,
                                InterpretationEvidenceType
                                        .NATAL_RELATION,
                                "NATAL_STEM_COMBINATION",
                                relation
                                        .firstStem()
                                        .getChineseCharacter()
                                        + relation
                                        .secondStem()
                                        .getChineseCharacter()
                                        + " 천간합",
                                0.4,
                                0.2,
                                AnalysisConfidence.MEDIUM
                        )
                );
            }
        }

        for (
                BranchRelation relation
                : relationAnalysis.branchRelations()
        ) {

            List<ConsultationCategory> categories =
                    InterpretationSignalRule
                            .categoriesForPositions(
                                    relation.positions()
                            );

            InterpretationSignalRule.RelationWeight
                    weight =
                    InterpretationSignalRule
                            .relationWeight(
                                    relation.type()
                            );

            for (
                    ConsultationCategory category
                    : categories
            ) {

                result.add(
                        new InterpretationEvidence(
                                category,
                                InterpretationEvidenceType
                                        .NATAL_RELATION,
                                "NATAL_BRANCH_"
                                        + relation
                                        .type()
                                        .name(),
                                "원국 지지 관계: "
                                        + relation
                                        .type()
                                        .getKoreanName(),
                                weight.supportWeight(),
                                weight.cautionWeight(),
                                AnalysisConfidence.MEDIUM
                        )
                );
            }
        }
    }
}