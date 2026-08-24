package com.dajeom.engine;

import com.dajeom.model.NatalExactTenGodProfile;
import com.dajeom.model.NatalPersonalityExactTenGodFact;
import com.dajeom.model.NatalPersonalityExactTenGodSelection;
import com.dajeom.model.NatalPersonalityFinding;
import com.dajeom.model.NatalPersonalityFindingAnalysis;
import com.dajeom.model.NatalPersonalityFindingSynthesisAnalysis;
import com.dajeom.model.NatalPersonalityStatementFact;
import com.dajeom.model.NatalPersonalityStatementFactAnalysis;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.PersonalityJudgmentGroundingLevel;
import com.dajeom.type.PersonalityStatementExpressionMode;
import com.dajeom.type.PersonalityStatementFactType;
import com.dajeom.type.PersonalityStatementSelectionRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Personality Statement Fact 생성 Engine.
 *
 * =================================================
 * 현재 공식 구조
 * =================================================
 *
 * 정확한 10십성
 *
 * ->
 *
 * Exact TenGod Statement Fact
 *
 * =================================================
 *
 * 기존 Finding
 *
 * ->
 *
 * Legacy / Pattern Statement Fact
 *
 * =================================================
 *
 * 중요한 원칙
 * =================================================
 *
 * 1.
 * 정확한 10십성을 먼저 보존한다.
 *
 * 2.
 * 비겁/식상/재성/관살/인성 5축으로
 * 정확한 십성을 대체하지 않는다.
 *
 * 3.
 * 전통 Cross Pattern은
 * 기존 Finding으로 별도 유지한다.
 *
 * 4.
 * 둘 이상의 독립 Finding을 다시 합쳐
 * Synthesis Fact를 만들지 않는다.
 */
@Component
public class NatalPersonalityStatementFactAnalysisEngine {

    /**
     * =================================================
     * Legacy 호환 실행
     * =================================================
     *
     * 공식 Personality Pipeline에서는
     * Exact TenGod Selection을 받는
     * overload를 사용한다.
     */
    public NatalPersonalityStatementFactAnalysis analyze(

            NatalPersonalityFindingSynthesisAnalysis
                    synthesisAnalysis

    ) {

        Objects.requireNonNull(
                synthesisAnalysis,
                "Personality Finding Synthesis Analysis는 필수입니다."
        );

        return createAnalysis(

                synthesisAnalysis,

                Optional.empty(),

                List.of()
        );
    }

    /**
     * =================================================
     * 공식 실행
     * =================================================
     */
    public NatalPersonalityStatementFactAnalysis analyze(

            NatalPersonalityFindingSynthesisAnalysis
                    synthesisAnalysis,

            NatalPersonalityExactTenGodSelection
                    exactTenGodSelection

    ) {

        Objects.requireNonNull(
                synthesisAnalysis,
                "Personality Finding Synthesis Analysis는 필수입니다."
        );

        Objects.requireNonNull(
                exactTenGodSelection,
                "Personality Exact TenGod Selection은 필수입니다."
        );

        if (
                synthesisAnalysis
                        .completeChart()
                        != exactTenGodSelection
                        .completeChart()
        ) {

            throw new IllegalArgumentException(
                    "Synthesis Analysis와 Exact TenGod Selection의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        List<NatalPersonalityExactTenGodFact> exactFacts =
                createExactTenGodFacts(
                        exactTenGodSelection
                );

        return createAnalysis(

                synthesisAnalysis,

                Optional.of(
                        exactTenGodSelection
                ),

                exactFacts
        );
    }

    private NatalPersonalityStatementFactAnalysis
    createAnalysis(

            NatalPersonalityFindingSynthesisAnalysis
                    synthesisAnalysis,

            Optional<NatalPersonalityExactTenGodSelection>
                    exactTenGodSelection,

            List<NatalPersonalityExactTenGodFact>
                    exactTenGodFacts

    ) {

        /*
         * =================================================
         * 2차 Synthesis 하드 차단
         * =================================================
         */

        if (
                !synthesisAnalysis
                        .syntheses()
                        .isEmpty()
        ) {

            throw new IllegalStateException(
                    "현재 Statement Fact 단계에서는 "
                            + "2차 Personality Synthesis를 소비할 수 없습니다."
            );
        }

        NatalPersonalityFindingAnalysis findingAnalysis =
                synthesisAnalysis
                        .findingAnalysis();

        List<NatalPersonalityStatementFact> facts =
                new ArrayList<>();

        /*
         * =================================================
         * 기존 Finding은 각각 독립 Fact로 보존
         * =================================================
         */

        for (
                NatalPersonalityFinding finding
                : findingAnalysis.findings()
        ) {

            facts.add(
                    createFindingFact(
                            finding
                    )
            );
        }

        return new NatalPersonalityStatementFactAnalysis(

                synthesisAnalysis,

                exactTenGodSelection,

                exactTenGodFacts,

                facts,

                synthesisAnalysis
                        .completeChart()
        );
    }

    /*
     * =================================================
     * Exact TenGod Fact
     * =================================================
     */

    private List<NatalPersonalityExactTenGodFact>
    createExactTenGodFacts(

            NatalPersonalityExactTenGodSelection selection

    ) {

        List<NatalPersonalityExactTenGodFact> result =
                new ArrayList<>();

        /*
         * =================================================
         * PRIMARY
         * =================================================
         */

        appendExactTenGodFacts(

                result,

                selection
                        .primaryProfiles(),

                PersonalityStatementSelectionRole
                        .PRIMARY,

                selection.completeChart()
        );

        /*
         * =================================================
         * SUPPORTING
         * =================================================
         */

        appendExactTenGodFacts(

                result,

                selection
                        .supportingProfiles(),

                PersonalityStatementSelectionRole
                        .SUPPORTING,

                selection.completeChart()
        );

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        appendExactTenGodFacts(

                result,

                selection
                        .referenceProfiles(),

                PersonalityStatementSelectionRole
                        .INTERNAL_REFERENCE,

                selection.completeChart()
        );

        return List.copyOf(
                result
        );
    }

    private void appendExactTenGodFacts(

            List<NatalPersonalityExactTenGodFact> target,

            List<NatalExactTenGodProfile> profiles,

            PersonalityStatementSelectionRole role,

            boolean completeChart

    ) {

        for (
                NatalExactTenGodProfile profile
                : profiles
        ) {

            target.add(
                    new NatalPersonalityExactTenGodFact(

                            profile,

                            role,

                            completeChart
                    )
            );
        }
    }

    /*
     * =================================================
     * 기존 Finding Fact
     * =================================================
     */

    private NatalPersonalityStatementFact
    createFindingFact(

            NatalPersonalityFinding finding

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

        /*
         * =================================================
         * absorbedByDirectSynthesis
         *
         * 현재 항상 false.
         *
         * 2차 Synthesis가 공식 실행에서
         * 비활성화되었기 때문이다.
         * =================================================
         */

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

                false,

                List.of(
                        finding
                ),

                List.of(),

                finding.completeChart()
        );
    }

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

    /*
     * =================================================
     * Grounding
     * =================================================
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