package com.dajeom.analyzer;

import com.dajeom.model.ElementCandidateMatch;
import com.dajeom.model.InterpretationEvidence;
import com.dajeom.model.LuckBranchInteraction;
import com.dajeom.model.LuckInteractionAnalysis;
import com.dajeom.model.LuckPillarSource;
import com.dajeom.model.LuckStemInteraction;
import com.dajeom.model.LuckTenGodInfluence;
import com.dajeom.model.NatalChartAdvancedAnalysis;
import com.dajeom.rule.InterpretationSignalRule;
import com.dajeom.type.AnalysisConfidence;
import com.dajeom.type.ConsultationCategory;
import com.dajeom.type.InterpretationEvidenceType;
import com.dajeom.type.LuckSourceType;
import com.dajeom.type.PillarPosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 대운/세운 상호작용 결과를
 * 상담용 해석 근거로 변환한다.
 *
 * 아직 사용자 문장을 생성하지 않는다.
 *
 * =================================================
 *
 * 현재 정밀화 구조에서는
 * UsefulGodAnalysis 전체를 하나의
 * confidence 값으로 축약하지 않는다.
 *
 * 용신 선택 상태는:
 *
 * - PRIMARY 존재 여부
 * - 복수 PRIMARY
 * - DEFERRED 후보
 * - 추가 검토 필요
 *
 * 등의 구조적 Evidence로 보존된다.
 *
 * 따라서 이 Analyzer에서는
 * 구형 UsefulGodAnalysis.confidence()를 사용하지 않는다.
 */
@Component
public class LuckInterpretationSignalAnalyzer {

    public List<InterpretationEvidence> analyze(
            NatalChartAdvancedAnalysis natalChart,
            LuckInteractionAnalysis luckInteraction
    ) {

        Objects.requireNonNull(
                natalChart,
                "고차 원국 분석 결과는 필수입니다."
        );

        Objects.requireNonNull(
                luckInteraction,
                "운 상호작용 분석 결과는 필수입니다."
        );

        List<InterpretationEvidence> result =
                new ArrayList<>();

        addTenGodEvidence(
                luckInteraction,
                result
        );

        addLuckRelationEvidence(
                luckInteraction,
                result
        );

        addUsefulElementEvidence(
                luckInteraction,
                result
        );

        addDecadeTransitionEvidence(
                luckInteraction,
                result
        );

        return List.copyOf(
                result
        );
    }

    private void addTenGodEvidence(
            LuckInteractionAnalysis luckInteraction,
            List<InterpretationEvidence> result
    ) {

        for (
                LuckTenGodInfluence influence
                : luckInteraction.tenGodInfluences()
        ) {

            /*
             * 천간 십성.
             */
            addTenGod(
                    result,
                    influence.source(),
                    influence.stemTenGod(),
                    "STEM"
            );

            /*
             * 지지 정기 십성.
             */
            addTenGod(
                    result,
                    influence.source(),
                    influence.branchMainTenGod(),
                    "BRANCH_MAIN"
            );
        }
    }

    private void addTenGod(
            List<InterpretationEvidence> result,
            LuckPillarSource source,
            com.dajeom.type.TenGod tenGod,
            String part
    ) {

        List<
                InterpretationSignalRule.CategoryWeight
                > weights =
                InterpretationSignalRule
                        .weightsForTenGod(
                                tenGod
                        );

        for (
                InterpretationSignalRule.CategoryWeight weight
                : weights
        ) {

            result.add(
                    new InterpretationEvidence(
                            weight.category(),
                            InterpretationEvidenceType.LUCK_TEN_GOD,
                            source
                                    .sourceType()
                                    .name()
                                    + "_"
                                    + part
                                    + "_TEN_GOD_"
                                    + tenGod.name(),
                            source.displayName()
                                    + " "
                                    + part
                                    + " 십성: "
                                    + tenGod.getKoreanName(),
                            weight.supportWeight(),
                            weight.cautionWeight(),
                            AnalysisConfidence.MEDIUM
                    )
            );
        }

        /*
         * 세운 십성은 현재와 올해 흐름에도
         * 별도 근거로 넣는다.
         */
        if (
                source.sourceType()
                        == LuckSourceType.ANNUAL_LUCK
        ) {

            double support =
                    weights
                            .stream()
                            .mapToDouble(
                                    InterpretationSignalRule
                                            .CategoryWeight
                                            ::supportWeight
                            )
                            .average()
                            .orElse(0.0);

            double caution =
                    weights
                            .stream()
                            .mapToDouble(
                                    InterpretationSignalRule
                                            .CategoryWeight
                                            ::cautionWeight
                            )
                            .average()
                            .orElse(0.0);

            result.add(
                    new InterpretationEvidence(
                            ConsultationCategory
                                    .CURRENT_YEAR_LUCK,
                            InterpretationEvidenceType.LUCK_TEN_GOD,
                            "ANNUAL_"
                                    + part
                                    + "_TEN_GOD_"
                                    + tenGod.name(),
                            "세운 "
                                    + part
                                    + " 십성: "
                                    + tenGod.getKoreanName(),
                            support,
                            caution,
                            AnalysisConfidence.MEDIUM
                    )
            );
        }

        /*
         * 대운은 장기 흐름이므로
         * 앞으로의 흐름에도 근거로 넣는다.
         */
        if (
                source.sourceType()
                        == LuckSourceType.DECADE_LUCK
        ) {

            double support =
                    weights
                            .stream()
                            .mapToDouble(
                                    InterpretationSignalRule
                                            .CategoryWeight
                                            ::supportWeight
                            )
                            .average()
                            .orElse(0.0);

            double caution =
                    weights
                            .stream()
                            .mapToDouble(
                                    InterpretationSignalRule
                                            .CategoryWeight
                                            ::cautionWeight
                            )
                            .average()
                            .orElse(0.0);

            result.add(
                    new InterpretationEvidence(
                            ConsultationCategory.FUTURE_LUCK,
                            InterpretationEvidenceType.LUCK_TEN_GOD,
                            "DECADE_"
                                    + part
                                    + "_TEN_GOD_"
                                    + tenGod.name(),
                            "대운 "
                                    + part
                                    + " 십성: "
                                    + tenGod.getKoreanName(),
                            support,
                            caution,
                            AnalysisConfidence.MEDIUM
                    )
            );
        }
    }

    private void addLuckRelationEvidence(
            LuckInteractionAnalysis luckInteraction,
            List<InterpretationEvidence> result
    ) {

        /*
         * 천간합.
         */
        for (
                LuckStemInteraction interaction
                : luckInteraction.stemInteractions()
        ) {

            List<ConsultationCategory> categories =
                    categoriesForLuckSources(
                            List.of(
                                    interaction.firstSource(),
                                    interaction.secondSource()
                            )
                    );

            for (
                    ConsultationCategory category
                    : categories
            ) {

                result.add(
                        new InterpretationEvidence(
                                category,
                                InterpretationEvidenceType
                                        .LUCK_RELATION,
                                "LUCK_STEM_COMBINATION",
                                interaction
                                        .firstSource()
                                        .displayName()
                                        + " ↔ "
                                        + interaction
                                        .secondSource()
                                        .displayName()
                                        + " 천간합",
                                0.5,
                                0.2,
                                AnalysisConfidence.MEDIUM
                        )
                );
            }
        }

        /*
         * 지지 관계.
         */
        for (
                LuckBranchInteraction interaction
                : luckInteraction.branchInteractions()
        ) {

            InterpretationSignalRule.RelationWeight weight =
                    InterpretationSignalRule
                            .relationWeight(
                                    interaction.type()
                            );

            List<ConsultationCategory> categories =
                    categoriesForLuckSources(
                            interaction.sources()
                    );

            for (
                    ConsultationCategory category
                    : categories
            ) {

                result.add(
                        new InterpretationEvidence(
                                category,
                                InterpretationEvidenceType
                                        .LUCK_RELATION,
                                "LUCK_BRANCH_"
                                        + interaction
                                        .type()
                                        .name(),
                                "운 지지 관계: "
                                        + interaction
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

    /**
     * 관계에 포함된 원국 위치를 상담 분야에 연결하고,
     * 세운이 포함되면 현재운,
     * 대운이 포함되면 미래운을 추가한다.
     */
    private List<ConsultationCategory>
    categoriesForLuckSources(
            List<LuckPillarSource> sources
    ) {

        Set<ConsultationCategory> result =
                new LinkedHashSet<>();

        List<PillarPosition> natalPositions =
                sources
                        .stream()
                        .filter(
                                LuckPillarSource::isNatal
                        )
                        .map(
                                LuckPillarSource::natalPosition
                        )
                        .toList();

        result.addAll(
                InterpretationSignalRule
                        .categoriesForPositions(
                                natalPositions
                        )
        );

        boolean annualIncluded =
                sources
                        .stream()
                        .anyMatch(
                                source ->
                                        source.sourceType()
                                                == LuckSourceType
                                                .ANNUAL_LUCK
                        );

        boolean decadeIncluded =
                sources
                        .stream()
                        .anyMatch(
                                source ->
                                        source.sourceType()
                                                == LuckSourceType
                                                .DECADE_LUCK
                        );

        if (
                annualIncluded
        ) {

            result.add(
                    ConsultationCategory
                            .CURRENT_YEAR_LUCK
            );
        }

        if (
                decadeIncluded
        ) {

            result.add(
                    ConsultationCategory
                            .FUTURE_LUCK
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 대운/세운 유입 오행과
     * 현재 용신 체계의 관계를
     * InterpretationEvidence로 변환한다.
     *
     * =================================================
     *
     * 현재 7차 정밀화 구조에서는
     * UsefulGodAnalysis 전체에 대한
     * 단일 confidence 값을 만들지 않는다.
     *
     * 따라서:
     *
     * UsefulGodAnalysis.confidence()
     *
     * 같은 구형 API를 사용하지 않는다.
     *
     * 이 단계의 Evidence Confidence는
     * 다른 운 상호작용 Evidence와 동일하게
     * MEDIUM으로 유지한다.
     *
     * 용신 자체의 불확실성은
     * UsefulGodSelectionAnalysis /
     * UsefulGodRelationAnalysis에
     * 구조적으로 보존되어 있다.
     */
    private void addUsefulElementEvidence(
            LuckInteractionAnalysis luckInteraction,
            List<InterpretationEvidence> result
    ) {

        AnalysisConfidence confidence =
                AnalysisConfidence.MEDIUM;

        for (
                ElementCandidateMatch match
                : luckInteraction.elementCandidateMatches()
        ) {

            double supportWeight =
                    calculateSupportWeight(
                            match
                    );

            double cautionWeight =
                    match.gishinCandidate()
                            ? 1.0
                            : 0.0;

            ConsultationCategory mainCategory =
                    match
                            .source()
                            .sourceType()
                            == LuckSourceType.ANNUAL_LUCK
                            ? ConsultationCategory
                            .CURRENT_YEAR_LUCK
                            : ConsultationCategory
                            .FUTURE_LUCK;

            result.add(
                    new InterpretationEvidence(
                            mainCategory,
                            InterpretationEvidenceType
                                    .USEFUL_ELEMENT_MATCH,
                            match
                                    .source()
                                    .sourceType()
                                    .name()
                                    + "_"
                                    + match
                                    .pillarPart()
                                    .name()
                                    + "_ELEMENT_"
                                    + match
                                    .element()
                                    .name(),
                            match
                                    .source()
                                    .displayName()
                                    + " "
                                    + match
                                    .pillarPart()
                                    .getKoreanName()
                                    + " 오행: "
                                    + match
                                    .element()
                                    .getKoreanName(),
                            supportWeight,
                            cautionWeight,
                            confidence
                    )
            );
        }
    }

    private double calculateSupportWeight(
            ElementCandidateMatch match
    ) {

        double score =
                0.0;

        if (
                match.yongshinCandidate()
        ) {

            score += 1.0;
        }

        if (
                match.huishinCandidate()
        ) {

            score += 0.7;
        }

        if (
                match.temperatureCandidate()
        ) {

            score += 0.6;
        }

        if (
                match.mediationCandidate()
        ) {

            score += 0.6;
        }

        return score;
    }

    private void addDecadeTransitionEvidence(
            LuckInteractionAnalysis luckInteraction,
            List<InterpretationEvidence> result
    ) {

        if (
                !luckInteraction
                        .decadeTransitionDuringAnnualLuck()
        ) {

            return;
        }

        /*
         * 대운 교체를 무조건 나쁜 것으로 보지 않는다.
         *
         * 다만 생활의 큰 흐름이 바뀌는 시점이라는
         * 의미에서 지원/주의를 함께 부여한다.
         */
        result.add(
                new InterpretationEvidence(
                        ConsultationCategory
                                .CURRENT_YEAR_LUCK,
                        InterpretationEvidenceType
                                .DECADE_TRANSITION,
                        "DECADE_TRANSITION_DURING_YEAR",
                        "해당 세운 기간 중 대운 교체 발생",
                        0.5,
                        0.7,
                        AnalysisConfidence.MEDIUM
                )
        );

        result.add(
                new InterpretationEvidence(
                        ConsultationCategory.FUTURE_LUCK,
                        InterpretationEvidenceType
                                .DECADE_TRANSITION,
                        "DECADE_TRANSITION_FUTURE",
                        "새로운 대운 시작",
                        0.6,
                        0.4,
                        AnalysisConfidence.MEDIUM
                )
        );
    }
}