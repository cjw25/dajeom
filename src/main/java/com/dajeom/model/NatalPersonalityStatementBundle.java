package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementBundleKind;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 서로 구조적으로 연결된
 * Personality Statement Selection 묶음.
 *
 * =================================================
 *
 * Bundle을 연결하는 기준:
 *
 * 직접 출력 Selection들이
 * 동일 십성 기능축을 공유하는가.
 *
 * =================================================
 *
 * 예:
 *
 * Synthesis
 *
 * EXPRESSION
 * ↔
 * RESPONSIBILITY
 *
 * +
 *
 * PROCESSING
 * →
 * EXPRESSION
 *
 * 와
 *
 * EXPRESSION Function
 * RESPONSIBILITY Function
 * PROCESSING Function
 *
 * 은 같은 기능축을 공유하므로
 * 하나의 Bundle에 들어갈 수 있다.
 *
 * =================================================
 *
 * relatedInternalReferences
 *
 * 약한 Finding 또는
 * 상위 Synthesis에 흡수된 Finding 중
 *
 * 이 Bundle의 기능축/상황과
 * 실제로 연결되는 내부 Reference.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * relatedInternalReferences는
 *
 * - Bundle의 Grounding을 올리지 않는다.
 * - Bundle끼리 연결하지 않는다.
 * - 단독 사용자 문장이 되지 않는다.
 *
 * 단지 후속 문장 생성에서
 * 보조적인 내부 검증 Context로만 사용한다.
 */
public record NatalPersonalityStatementBundle(

        PersonalityStatementBundleKind bundleKind,

        List<NatalPersonalityStatementSelection>
        primarySelections,

        List<NatalPersonalityStatementSelection>
        supportingSelections,

        List<NatalPersonalityStatementSelection>
        relatedInternalReferences,

        List<NatalTenGodFunctionType>
        involvedFunctions,

        List<NatalResponseSituationType>
        situations,

        boolean qualificationRequired,

        boolean completeChart

) {

    public NatalPersonalityStatementBundle {

        Objects.requireNonNull(
                bundleKind,
                "Personality Statement Bundle Kind는 필수입니다."
        );

        primarySelections =
                primarySelections == null
                        ? List.of()
                        : List.copyOf(
                        primarySelections
                );

        supportingSelections =
                supportingSelections == null
                        ? List.of()
                        : List.copyOf(
                        supportingSelections
                );

        relatedInternalReferences =
                relatedInternalReferences == null
                        ? List.of()
                        : List.copyOf(
                        relatedInternalReferences
                );

        involvedFunctions =
                involvedFunctions == null
                        ? List.of()
                        : List.copyOf(
                        involvedFunctions
                );

        situations =
                situations == null
                        ? List.of()
                        : List.copyOf(
                        situations
                );

        /*
         * =================================================
         * Bundle은 최소 하나의
         * 직접 출력 Selection을 가져야 한다.
         * =================================================
         */

        if (
                primarySelections.isEmpty()
                        && supportingSelections.isEmpty()
        ) {

            throw new IllegalArgumentException(
                    "Personality Statement Bundle에는 "
                            + "최소 하나의 직접 출력 Selection이 필요합니다."
            );
        }

        /*
         * =================================================
         * Bundle Kind 검증
         * =================================================
         */

        switch (
                bundleKind
        ) {

            case PRIMARY_CONNECTED -> {

                if (
                        primarySelections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "PRIMARY_CONNECTED Bundle에는 "
                                    + "최소 하나의 PRIMARY Selection이 필요합니다."
                    );
                }
            }

            case SUPPORTING_CONNECTED -> {

                if (
                        !primarySelections.isEmpty()
                                || supportingSelections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "SUPPORTING_CONNECTED Bundle에는 "
                                    + "PRIMARY가 없어야 하며 "
                                    + "최소 하나의 SUPPORTING Selection이 "
                                    + "필요합니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * Selection Role 검증
         * =================================================
         */

        for (
                NatalPersonalityStatementSelection selection
                : primarySelections
        ) {

            if (
                    !selection.primary()
            ) {

                throw new IllegalArgumentException(
                        "primarySelections에는 "
                                + "PRIMARY Selection만 들어갈 수 있습니다."
                );
            }

            validateCompleteness(
                    selection,
                    completeChart
            );
        }

        for (
                NatalPersonalityStatementSelection selection
                : supportingSelections
        ) {

            if (
                    !selection.supporting()
            ) {

                throw new IllegalArgumentException(
                        "supportingSelections에는 "
                                + "SUPPORTING Selection만 들어갈 수 있습니다."
                );
            }

            validateCompleteness(
                    selection,
                    completeChart
            );
        }

        for (
                NatalPersonalityStatementSelection selection
                : relatedInternalReferences
        ) {

            if (
                    !selection.internalReference()
            ) {

                throw new IllegalArgumentException(
                        "relatedInternalReferences에는 "
                                + "INTERNAL_REFERENCE Selection만 "
                                + "들어갈 수 있습니다."
                );
            }

            validateCompleteness(
                    selection,
                    completeChart
            );
        }

        /*
         * =================================================
         * 같은 Selection 중복 금지
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> directSelections =
                new HashSet<>();

        for (
                NatalPersonalityStatementSelection selection
                : primarySelections
        ) {

            if (
                    !directSelections.add(
                            selection
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 PRIMARY Selection이 "
                                + "Bundle에 중복되었습니다."
                );
            }
        }

        for (
                NatalPersonalityStatementSelection selection
                : supportingSelections
        ) {

            if (
                    !directSelections.add(
                            selection
                    )
            ) {

                throw new IllegalArgumentException(
                        "동일 직접 출력 Selection이 "
                                + "Bundle에 중복되었습니다."
                );
            }
        }

        /*
         * =================================================
         * involvedFunctions 중복 금지
         * =================================================
         */

        Set<NatalTenGodFunctionType> functionSet =
                new LinkedHashSet<>();

        for (
                NatalTenGodFunctionType functionType
                : involvedFunctions
        ) {

            Objects.requireNonNull(
                    functionType,
                    "Bundle 기능축에는 null을 사용할 수 없습니다."
            );

            if (
                    !functionSet.add(
                            functionType
                    )
            ) {

                throw new IllegalArgumentException(
                        "Bundle 기능축이 중복되었습니다: "
                                + functionType
                );
            }
        }

        /*
         * =================================================
         * situations 중복 금지
         * =================================================
         */

        Set<NatalResponseSituationType> situationSet =
                new LinkedHashSet<>();

        for (
                NatalResponseSituationType situation
                : situations
        ) {

            Objects.requireNonNull(
                    situation,
                    "Bundle 상황에는 null을 사용할 수 없습니다."
            );

            if (
                    !situationSet.add(
                            situation
                    )
            ) {

                throw new IllegalArgumentException(
                        "Bundle 상황이 중복되었습니다: "
                                + situation
                );
            }
        }

        /*
         * =================================================
         * 실제 직접 Selection의 기능축과
         * Bundle 기능축이 정확히 일치하는지 검증.
         *
         * INTERNAL_REFERENCE 기능축은
         * Bundle 의미범위를 확장시키지 않는다.
         * =================================================
         */

        Set<NatalTenGodFunctionType> expectedFunctions =
                new LinkedHashSet<>();

        for (
                NatalPersonalityStatementSelection selection
                : primarySelections
        ) {

            expectedFunctions.addAll(
                    selection.involvedFunctions()
            );
        }

        for (
                NatalPersonalityStatementSelection selection
                : supportingSelections
        ) {

            expectedFunctions.addAll(
                    selection.involvedFunctions()
            );
        }

        if (
                !expectedFunctions.equals(
                        functionSet
                )
        ) {

            throw new IllegalArgumentException(
                    "Bundle 기능축은 "
                            + "직접 출력 Selection의 기능축과 "
                            + "정확히 일치해야 합니다."
            );
        }

        /*
         * =================================================
         * qualificationRequired 검증
         *
         * 직접 출력 Selection만 기준으로 한다.
         *
         * 약한 INTERNAL_REFERENCE 때문에
         * 강한 직접판정에 불필요한 조건을
         * 강제로 붙이지 않는다.
         * =================================================
         */

        boolean expectedQualification =
                primarySelections
                        .stream()
                        .anyMatch(
                                NatalPersonalityStatementSelection
                                        ::qualificationRequired
                        )

                        || supportingSelections
                        .stream()
                        .anyMatch(
                                NatalPersonalityStatementSelection
                                        ::qualificationRequired
                        );

        if (
                qualificationRequired
                        != expectedQualification
        ) {

            throw new IllegalArgumentException(
                    "Bundle qualificationRequired 상태가 "
                            + "직접 출력 Selection과 일치하지 않습니다."
            );
        }
    }

    private static void validateCompleteness(

            NatalPersonalityStatementSelection selection,

            boolean completeChart

    ) {

        Objects.requireNonNull(
                selection,
                "Bundle Selection에는 null을 사용할 수 없습니다."
        );

        if (
                selection.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Bundle과 Selection의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }

    /**
     * 실제 사용자 설명에 들어갈
     * 모든 직접 Selection.
     *
     * PRIMARY 먼저,
     * 그 뒤 SUPPORTING.
     */
    public List<NatalPersonalityStatementSelection>
    directSelections() {

        return java.util.stream.Stream
                .concat(
                        primarySelections.stream(),
                        supportingSelections.stream()
                )
                .toList();
    }

    public boolean primaryConnected() {

        return bundleKind
                == PersonalityStatementBundleKind
                .PRIMARY_CONNECTED;
    }

    /**
     * 강한 PRIMARY가 하나라도 존재하는가.
     */
    public boolean hasStrongPrimary() {

        return primarySelections
                .stream()
                .anyMatch(
                        selection ->
                                selection
                                        .fact()
                                        .strongUserFacingCandidate()
                );
    }

    /**
     * PRIMARY 여부와 상관없이
     * Bundle의 직접 Selection 중
     * 강한 Fact가 존재하는가.
     */
    public boolean hasStrongDirectEvidence() {

        return directSelections()
                .stream()
                .anyMatch(
                        selection ->
                                selection
                                        .fact()
                                        .strongUserFacingCandidate()
                );
    }

    /**
     * 약한/흡수된 내부 근거가
     * 이 Bundle과 구조적으로 연결되는가.
     */
    public boolean hasRelatedInternalReference() {

        return !relatedInternalReferences
                .isEmpty();
    }
}