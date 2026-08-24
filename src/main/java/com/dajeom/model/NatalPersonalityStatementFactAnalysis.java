package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementFactType;
import com.dajeom.type.PersonalityStatementSelectionRole;
import com.dajeom.type.StructureTenGodType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Personality Statement Fact 전체 Aggregate.
 *
 * =================================================
 * 현재 공식 구조
 * =================================================
 *
 * 정확한 10십성
 *
 * ->
 *
 * exactTenGodFacts
 *
 * =================================================
 *
 * 기존 Finding / Pattern
 *
 * ->
 *
 * facts
 *
 * =================================================
 *
 * 중요한 우선순위
 * =================================================
 *
 * 사용자 Personality에서
 * 십성 자체의 공식 기준은:
 *
 * exactTenGodFacts
 *
 * 이다.
 *
 * 기존:
 *
 * SELF_ASSERTION
 * EXPRESSION
 * RESOURCE_REALITY
 * RESPONSIBILITY
 * PROCESSING
 *
 * 5개 기능축은
 * 전통 Pattern 판정 및 기존 구조 호환을 위해
 * facts 쪽에 남길 수 있지만,
 *
 * 정확한 10십성을 대체하는
 * 사용자 Meaning의 공식 원본이 아니다.
 */
public record NatalPersonalityStatementFactAnalysis(

        NatalPersonalityFindingSynthesisAnalysis
        synthesisAnalysis,

        Optional<NatalPersonalityExactTenGodSelection>
        exactTenGodSelection,

        List<NatalPersonalityExactTenGodFact>
        exactTenGodFacts,

        List<NatalPersonalityStatementFact>
        facts,

        boolean completeChart

) {

    /**
     * =================================================
     * Legacy 생성자
     * =================================================
     *
     * 기존 코드 호환용.
     *
     * 공식 Personality Pipeline에서는
     * Exact TenGod Selection을 포함하는
     * canonical 생성자를 사용한다.
     */
    public NatalPersonalityStatementFactAnalysis(

            NatalPersonalityFindingSynthesisAnalysis
                    synthesisAnalysis,

            List<NatalPersonalityStatementFact>
                    facts,

            boolean completeChart

    ) {

        this(
                synthesisAnalysis,

                Optional.empty(),

                List.of(),

                facts,

                completeChart
        );
    }

    public NatalPersonalityStatementFactAnalysis {

        Objects.requireNonNull(
                synthesisAnalysis,
                "Personality Finding Synthesis Analysis는 필수입니다."
        );

        exactTenGodSelection =
                exactTenGodSelection == null

                        ? Optional.empty()

                        : exactTenGodSelection;

        exactTenGodFacts =
                exactTenGodFacts == null

                        ? List.of()

                        : List.copyOf(
                        exactTenGodFacts
                );

        facts =
                facts == null

                        ? List.of()

                        : List.copyOf(
                        facts
                );

        /*
         * =================================================
         * 원국 완전성
         * =================================================
         */

        if (
                synthesisAnalysis
                        .completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Synthesis Analysis와 Statement Fact Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Exact TenGod Selection 없이
         * Fact만 존재할 수 없음
         * =================================================
         */

        if (
                exactTenGodSelection.isEmpty()
                        &&
                        !exactTenGodFacts.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Selection 없이 "
                            + "Exact TenGod Fact를 보존할 수 없습니다."
            );
        }

        /*
         * =================================================
         * Exact TenGod 검증
         * =================================================
         */

        if (
                exactTenGodSelection.isPresent()
        ) {

            NatalPersonalityExactTenGodSelection selection =
                    exactTenGodSelection
                            .orElseThrow();

            if (
                    selection.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Selection과 "
                                + "Statement Fact Analysis의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }

            validateExactTenGodFacts(
                    selection,
                    exactTenGodFacts,
                    completeChart
            );
        }

        /*
         * =================================================
         * 기존 Finding Fact 검증
         * =================================================
         */

        for (
                NatalPersonalityStatementFact fact
                : facts
        ) {

            Objects.requireNonNull(
                    fact,
                    "Statement Fact는 null일 수 없습니다."
            );

            if (
                    fact.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Statement Fact의 원국 완전성 상태가 "
                                + "Analysis와 일치하지 않습니다."
                );
            }
        }
    }

    /*
     * =================================================
     * Exact TenGod
     * =================================================
     */

    public boolean hasExactTenGodFacts() {

        return !exactTenGodFacts
                .isEmpty();
    }

    /**
     * 실제 사용자 설명에 사용할 수 있는
     * 정확한 10십성 Fact.
     */
    public List<NatalPersonalityExactTenGodFact>
    directExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::directOutput
                )
                .toList();
    }

    public List<NatalPersonalityExactTenGodFact>
    primaryExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::primary
                )
                .toList();
    }

    public List<NatalPersonalityExactTenGodFact>
    supportingExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::supporting
                )
                .toList();
    }

    public List<NatalPersonalityExactTenGodFact>
    referenceExactTenGodFacts() {

        return exactTenGodFacts
                .stream()
                .filter(
                        NatalPersonalityExactTenGodFact
                                ::internalReference
                )
                .toList();
    }

    public Optional<NatalPersonalityExactTenGodFact>
    exactTenGodFactOf(

            StructureTenGodType tenGodType

    ) {

        if (
                tenGodType == null
        ) {

            return Optional.empty();
        }

        return exactTenGodFacts
                .stream()
                .filter(
                        fact ->
                                fact.tenGodType()
                                        == tenGodType
                )
                .findFirst();
    }

    /*
     * =================================================
     * 기존 Finding Facts
     * =================================================
     */

    /**
     * 기존 Finding 기반 사용자 설명 후보.
     *
     * 이 목록은 정확한 10십성보다
     * 우선하는 사용자 십성 Meaning이 아니다.
     */
    public List<NatalPersonalityStatementFact>
    userFacingFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::userFacingCandidate
                )
                .toList();
    }

    public List<NatalPersonalityStatementFact>
    strongUserFacingFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::strongUserFacingCandidate
                )
                .toList();
    }

    public List<NatalPersonalityStatementFact>
    qualifiedUserFacingFacts() {

        return userFacingFacts()
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::qualificationRequired
                )
                .toList();
    }

    public List<NatalPersonalityStatementFact>
    absorbedFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::absorbedByDirectSynthesis
                )
                .toList();
    }

    public List<NatalPersonalityStatementFact>
    internalFacts() {

        return facts
                .stream()
                .filter(
                        NatalPersonalityStatementFact
                                ::internalOnly
                )
                .toList();
    }

    public List<NatalPersonalityStatementFact>
    factsOfType(

            PersonalityStatementFactType factType

    ) {

        if (
                factType == null
        ) {

            return List.of();
        }

        return facts
                .stream()
                .filter(
                        fact ->
                                fact.factType()
                                        == factType
                )
                .toList();
    }

    public List<NatalPersonalityStatementFact>
    factsInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return facts
                .stream()
                .filter(
                        fact ->
                                fact
                                        .involvedFunctions()
                                        .contains(
                                                functionType
                                        )
                )
                .toList();
    }

    /*
     * =================================================
     * Exact TenGod Validation
     * =================================================
     */

    private static void validateExactTenGodFacts(

            NatalPersonalityExactTenGodSelection selection,

            List<NatalPersonalityExactTenGodFact> facts,

            boolean completeChart

    ) {

        Map<
                StructureTenGodType,
                NatalPersonalityExactTenGodFact
                > indexed =
                new EnumMap<>(
                        StructureTenGodType.class
                );

        for (
                NatalPersonalityExactTenGodFact fact
                : facts
        ) {

            Objects.requireNonNull(
                    fact,
                    "Exact TenGod Statement Fact는 null일 수 없습니다."
            );

            if (
                    fact.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Statement Fact의 "
                                + "원국 완전성 상태가 일치하지 않습니다: "
                                + fact.tenGodType()
                );
            }

            if (
                    indexed.put(
                            fact.tenGodType(),
                            fact
                    ) != null
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Statement Fact가 중복되었습니다: "
                                + fact.tenGodType()
                );
            }
        }

        /*
         * =================================================
         * PRIMARY
         * =================================================
         */

        validateExpectedGroup(

                selection
                        .primaryProfiles(),

                PersonalityStatementSelectionRole
                        .PRIMARY,

                indexed
        );

        /*
         * =================================================
         * SUPPORTING
         * =================================================
         */

        validateExpectedGroup(

                selection
                        .supportingProfiles(),

                PersonalityStatementSelectionRole
                        .SUPPORTING,

                indexed
        );

        /*
         * =================================================
         * REFERENCE
         * =================================================
         */

        validateExpectedGroup(

                selection
                        .referenceProfiles(),

                PersonalityStatementSelectionRole
                        .INTERNAL_REFERENCE,

                indexed
        );

        int expectedCount =
                selection
                        .primaryProfiles()
                        .size()

                        +
                        selection
                                .supportingProfiles()
                                .size()

                        +
                        selection
                                .referenceProfiles()
                                .size();

        if (
                indexed.size()
                        != expectedCount
        ) {

            throw new IllegalArgumentException(
                    "Exact TenGod Selection과 "
                            + "Statement Fact 수가 일치하지 않습니다."
            );
        }
    }

    private static void validateExpectedGroup(

            List<NatalExactTenGodProfile> profiles,

            PersonalityStatementSelectionRole role,

            Map<
                    StructureTenGodType,
                    NatalPersonalityExactTenGodFact
                    > indexed

    ) {

        for (
                NatalExactTenGodProfile profile
                : profiles
        ) {

            NatalPersonalityExactTenGodFact fact =
                    indexed.get(
                            profile.tenGodType()
                    );

            if (
                    fact == null
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Selection에 대응하는 "
                                + "Statement Fact가 없습니다: "
                                + profile.tenGodType()
                );
            }

            /*
             * =================================================
             * Profile Identity 유지
             * =================================================
             */

            if (
                    fact.sourceProfile()
                            != profile
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Statement Fact가 "
                                + "공식 Selection Profile Identity를 보존하지 않았습니다: "
                                + profile.tenGodType()
                );
            }

            if (
                    fact.role()
                            != role
            ) {

                throw new IllegalArgumentException(
                        "Exact TenGod Statement Fact Role이 "
                                + "Selection과 일치하지 않습니다: "
                                + profile.tenGodType()
                );
            }
        }
    }
}