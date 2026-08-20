package com.dajeom.engine;

import com.dajeom.model.NatalPersonalityFinding;
import com.dajeom.model.NatalPersonalityFindingAnalysis;
import com.dajeom.model.NatalPersonalityFindingSynthesis;
import com.dajeom.model.NatalPersonalityFindingSynthesisAnalysis;
import com.dajeom.model.NatalPersonalityStatementFact;
import com.dajeom.model.NatalPersonalityStatementFactAnalysis;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.PersonalityFindingOriginType;
import com.dajeom.type.PersonalityFindingTheme;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityStatementExpressionMode;
import com.dajeom.type.PersonalityStatementFactType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Personality Finding
 *
 * +
 *
 * Personality Synthesis
 *
 * 를
 *
 * 최종 사용자 설명 직전의
 * Statement Fact로 변환한다.
 *
 * =================================================
 *
 * 핵심 원칙:
 *
 * 1.
 * Finding을 삭제하지 않는다.
 *
 * =================================================
 *
 * 2.
 * 강한 Synthesis가 존재하면
 * 그 Synthesis에 포함된 개별 Cross Finding은
 *
 * absorbedByDirectSynthesis = true
 *
 * 로 표시한다.
 *
 * =================================================
 *
 * 3.
 * 따라서:
 *
 * 내부 Evidence는 모두 보존하면서
 * 사용자 출력의 반복만 막는다.
 *
 * =================================================
 *
 * 4.
 * Function Finding은
 * Synthesis Source에 포함됐다는 이유로
 * 자동 흡수하지 않는다.
 *
 * 이유:
 *
 * 기능 자체에 대한 설명과
 * 기능 간 상호작용 설명은
 * 서로 다른 의미이기 때문이다.
 *
 * =================================================
 *
 * 5.
 * 새 점수 없음.
 *
 * 6.
 * 다수결 없음.
 *
 * 7.
 * 문자열 Parsing 없음.
 *
 * 8.
 * 한국어 성격문장 없음.
 */
@Component
public class NatalPersonalityStatementFactAnalysisEngine {

    public NatalPersonalityStatementFactAnalysis analyze(

            NatalPersonalityFindingSynthesisAnalysis
                    synthesisAnalysis

    ) {

        Objects.requireNonNull(
                synthesisAnalysis,
                "Personality Finding Synthesis Analysis는 필수입니다."
        );

        NatalPersonalityFindingAnalysis findingAnalysis =
                synthesisAnalysis
                        .findingAnalysis();

        List<NatalPersonalityStatementFact> facts =
                new ArrayList<>();

        /*
         * =================================================
         * 사용자에게 직접 표현 가능한 Synthesis가
         * 어떤 Finding Theme을 흡수하는지 먼저 계산한다.
         *
         * 주의:
         *
         * 이것은 Evidence 삭제 목록이 아니라
         * 출력 중복 방지 목록이다.
         * =================================================
         */

        Set<PersonalityFindingTheme>
                themesAbsorbedByDirectSynthesis =
                collectThemesAbsorbedByDirectSynthesis(
                        synthesisAnalysis
                );

        /*
         * =================================================
         * 1.
         * 모든 Finding을 Statement Fact로 변환
         * =================================================
         */

        for (
                NatalPersonalityFinding finding
                : findingAnalysis.findings()
        ) {

            boolean absorbed =
                    shouldAbsorbFinding(

                            finding,

                            themesAbsorbedByDirectSynthesis
                    );

            facts.add(
                    createFindingFact(
                            finding,
                            absorbed
                    )
            );
        }

        /*
         * =================================================
         * 2.
         * 모든 Synthesis를 Statement Fact로 변환
         *
         * REFERENCE/CONTEXTUAL Synthesis도
         * 삭제하지 않는다.
         * =================================================
         */

        for (
                NatalPersonalityFindingSynthesis synthesis
                : synthesisAnalysis.syntheses()
        ) {

            facts.add(
                    createSynthesisFact(
                            synthesis
                    )
            );
        }

        return new NatalPersonalityStatementFactAnalysis(

                synthesisAnalysis,

                facts,

                synthesisAnalysis.completeChart()
        );
    }

    /**
     * 직접 표현 가능한 Synthesis가
     * 사용하는 Cross Finding Theme 수집.
     */
    private Set<PersonalityFindingTheme>
    collectThemesAbsorbedByDirectSynthesis(

            NatalPersonalityFindingSynthesisAnalysis
                    synthesisAnalysis

    ) {

        Set<PersonalityFindingTheme> result =
                new LinkedHashSet<>();

        for (
                NatalPersonalityFindingSynthesis synthesis
                : synthesisAnalysis
                .directlyAssertableSyntheses()
        ) {

            for (
                    NatalPersonalityFinding finding
                    : synthesis.sourceFindings()
            ) {

                /*
                 * Cross Pattern만 흡수 대상으로 한다.
                 *
                 * Function 자체 Finding은
                 * 복합작동 설명과 의미가 다르므로
                 * 유지한다.
                 */
                if (
                        finding.originType()
                                == PersonalityFindingOriginType
                                .CROSS_PATTERN
                ) {

                    result.add(
                            finding.theme()
                    );
                }
            }
        }

        return Set.copyOf(
                result
        );
    }

    /**
     * 개별 Finding을
     * 사용자 출력에서 Synthesis에 흡수할지.
     */
    private boolean shouldAbsorbFinding(

            NatalPersonalityFinding finding,

            Set<PersonalityFindingTheme>
                    themesAbsorbedByDirectSynthesis

    ) {

        if (
                finding.originType()
                        != PersonalityFindingOriginType
                        .CROSS_PATTERN
        ) {

            return false;
        }

        /*
         * 직접 출력 가능한 Finding만
         * 실제 중복 출력 대상이다.
         *
         * REFERENCE/CONTEXTUAL은 어차피
         * 사용자 직접표현 대상이 아니므로
         * absorbed로 표시하지 않는다.
         */
        if (
                !finding.directAssertionAllowed()
        ) {

            return false;
        }

        return themesAbsorbedByDirectSynthesis
                .contains(
                        finding.theme()
                );
    }

    /**
     * =================================================
     * Finding → Statement Fact
     * =================================================
     */
    private NatalPersonalityStatementFact
    createFindingFact(

            NatalPersonalityFinding finding,

            boolean absorbed

    ) {

        PersonalityStatementFactType factType =
                mapFindingFactType(
                        finding
                );

        List<NatalResponseSituationType> situations =
                finding
                        .situationType()
                        .map(
                                List::of
                        )
                        .orElseGet(
                                List::of
                        );

        PersonalityStatementExpressionMode expressionMode =
                determineExpressionMode(

                        finding.groundingLevel(),

                        finding.directAssertionAllowed(),

                        finding.qualificationRequired()
                );

        return new NatalPersonalityStatementFact(

                factType,

                Optional.of(
                        finding.theme()
                ),

                Optional.empty(),

                finding.involvedFunctions(),

                situations,

                finding.groundingLevel(),

                expressionMode,

                absorbed,

                List.of(
                        finding
                ),

                List.of(),

                finding.completeChart()
        );
    }

    /**
     * Finding Origin
     * →
     * Statement Fact Type.
     */
    private PersonalityStatementFactType
    mapFindingFactType(

            NatalPersonalityFinding finding

    ) {

        return switch (
                finding.originType()
                ) {

            case CORE_SELF ->
                    PersonalityStatementFactType
                            .CORE_SELF;

            case FUNCTION ->
                    PersonalityStatementFactType
                            .FUNCTION;

            case CROSS_PATTERN ->
                    PersonalityStatementFactType
                            .CROSS_PATTERN;

            case INTERPERSONAL_CONTEXT ->
                    PersonalityStatementFactType
                            .INTERPERSONAL_CONTEXT;
        };
    }

    /**
     * =================================================
     * Synthesis → Statement Fact
     * =================================================
     */
    private NatalPersonalityStatementFact
    createSynthesisFact(

            NatalPersonalityFindingSynthesis synthesis

    ) {

        PersonalityStatementExpressionMode expressionMode =
                determineExpressionMode(

                        synthesis.groundingLevel(),

                        synthesis.directAssertionAllowed(),

                        synthesis.qualificationRequired()
                );

        List<NatalResponseSituationType> situations =
                collectSituations(
                        synthesis
                );

        return new NatalPersonalityStatementFact(

                PersonalityStatementFactType
                        .SYNTHESIS,

                Optional.empty(),

                Optional.of(
                        synthesis.synthesisType()
                ),

                synthesis.involvedFunctions(),

                situations,

                synthesis.groundingLevel(),

                expressionMode,

                false,

                synthesis.sourceFindings(),

                List.of(
                        synthesis
                ),

                synthesis.completeChart()
        );
    }

    /**
     * Synthesis Source Findings가
     * 가지고 있는 상황을
     * 중복 제거하여 보존.
     */
    private List<NatalResponseSituationType>
    collectSituations(

            NatalPersonalityFindingSynthesis synthesis

    ) {

        Set<NatalResponseSituationType> situations =
                new LinkedHashSet<>();

        for (
                NatalPersonalityFinding finding
                : synthesis.sourceFindings()
        ) {

            finding
                    .situationType()
                    .ifPresent(
                            situations::add
                    );
        }

        return List.copyOf(
                situations
        );
    }

    /**
     * =================================================
     * Grounding
     * +
     * direct
     * +
     * qualification
     *
     * →
     *
     * 최종 표현 Mode.
     *
     * =================================================
     *
     * 새 점수나 Threshold를
     * 사용하지 않는다.
     */
    private PersonalityStatementExpressionMode
    determineExpressionMode(

            PersonalityJudgmentGroundingLevel groundingLevel,

            boolean directAssertionAllowed,

            boolean qualificationRequired

    ) {

        Objects.requireNonNull(
                groundingLevel,
                "Grounding Level은 필수입니다."
        );

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        if (
                groundingLevel
                        == PersonalityJudgmentGroundingLevel
                        .REFERENCE
        ) {

            return PersonalityStatementExpressionMode
                    .INTERNAL_REFERENCE;
        }

        /*
         * =================================================
         * CONTEXTUAL
         * =================================================
         */

        if (
                !directAssertionAllowed
        ) {

            return PersonalityStatementExpressionMode
                    .CONTEXT_ONLY;
        }

        /*
         * =================================================
         * STRONGLY GROUNDED
         * =================================================
         */

        if (
                groundingLevel
                        == PersonalityJudgmentGroundingLevel
                        .STRONGLY_GROUNDED
        ) {

            return qualificationRequired

                    ? PersonalityStatementExpressionMode
                    .STRONG_DIRECT_WITH_QUALIFICATION

                    : PersonalityStatementExpressionMode
                    .STRONG_DIRECT;
        }

        /*
         * =================================================
         * GROUNDED
         * =================================================
         */

        return qualificationRequired

                ? PersonalityStatementExpressionMode
                .DIRECT_WITH_QUALIFICATION

                : PersonalityStatementExpressionMode
                .DIRECT;
    }
}