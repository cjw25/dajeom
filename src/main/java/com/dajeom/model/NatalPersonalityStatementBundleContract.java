package com.dajeom.model;

import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementBundleObligation;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Personality Statement Bundle 하나를
 * 최종 문장으로 변환할 때의
 * 명시적 Contract.
 *
 * =================================================
 *
 * 중요한 구조:
 *
 * mandatorySelections
 *
 * → 반드시 설명해야 하는 PRIMARY
 *
 * supportingSelections
 *
 * → 중심 설명을 보강하는 근거
 *
 * strongSelections
 *
 * → 강한 표현이 허용된 Selection
 *
 * internalReferenceSelections
 *
 * → 내부 참고만 가능한 근거
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * strongSelections에 포함됐다는 것은
 *
 * Bundle 전체를 강하게 단정하라는 뜻이 아니다.
 *
 * 해당 Selection에서 표현되는 의미만
 * 강하게 말할 수 있다는 뜻이다.
 *
 * =================================================
 *
 * 즉:
 *
 * STRONG 하나가 있다고
 * Bundle 전체를 STRONG으로
 * 확대해석하는 것을 막는다.
 */
public record NatalPersonalityStatementBundleContract(

        NatalPersonalityStatementBundle sourceBundle,

        PersonalityStatementBundleObligation obligation,

        List<NatalPersonalityStatementSelection>
        mandatorySelections,

        List<NatalPersonalityStatementSelection>
        supportingSelections,

        List<NatalPersonalityStatementSelection>
        strongSelections,

        List<NatalPersonalityStatementSelection>
        internalReferenceSelections,

        List<NatalTenGodFunctionType>
        involvedFunctions,

        List<NatalResponseSituationType>
        situations,

        boolean qualificationRequired,

        boolean completeChart

) {

    public NatalPersonalityStatementBundleContract {

        Objects.requireNonNull(
                sourceBundle,
                "Personality Statement Bundle은 필수입니다."
        );

        Objects.requireNonNull(
                obligation,
                "Personality Statement Bundle Obligation은 필수입니다."
        );

        mandatorySelections =
                mandatorySelections == null
                        ? List.of()
                        : List.copyOf(
                        mandatorySelections
                );

        supportingSelections =
                supportingSelections == null
                        ? List.of()
                        : List.copyOf(
                        supportingSelections
                );

        strongSelections =
                strongSelections == null
                        ? List.of()
                        : List.copyOf(
                        strongSelections
                );

        internalReferenceSelections =
                internalReferenceSelections == null
                        ? List.of()
                        : List.copyOf(
                        internalReferenceSelections
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
         * 원국 완전성
         * =================================================
         */

        if (
                sourceBundle.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Statement Bundle과 Contract의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Obligation 검증
         * =================================================
         */

        switch (
                obligation
        ) {

            case REQUIRED -> {

                if (
                        !sourceBundle.primaryConnected()
                ) {

                    throw new IllegalArgumentException(
                            "REQUIRED Contract는 "
                                    + "PRIMARY_CONNECTED Bundle에만 "
                                    + "사용할 수 있습니다."
                    );
                }

                if (
                        mandatorySelections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "REQUIRED Contract에는 "
                                    + "최소 하나의 Mandatory Selection이 "
                                    + "필요합니다."
                    );
                }
            }

            case OPTIONAL_SUPPORT -> {

                if (
                        sourceBundle.primaryConnected()
                ) {

                    throw new IllegalArgumentException(
                            "PRIMARY_CONNECTED Bundle을 "
                                    + "OPTIONAL_SUPPORT로 만들 수 없습니다."
                    );
                }

                if (
                        !mandatorySelections.isEmpty()
                ) {

                    throw new IllegalArgumentException(
                            "OPTIONAL_SUPPORT Contract에는 "
                                    + "Mandatory Selection을 "
                                    + "사용하지 않습니다."
                    );
                }
            }
        }

        /*
         * =================================================
         * Mandatory
         * =
         * Bundle Primary
         *
         * 정확히 일치해야 한다.
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> mandatorySet =
                new HashSet<>(
                        mandatorySelections
                );

        Set<NatalPersonalityStatementSelection> expectedMandatory =
                new HashSet<>(
                        sourceBundle.primarySelections()
                );

        if (
                !mandatorySet.equals(
                        expectedMandatory
                )
        ) {

            throw new IllegalArgumentException(
                    "Contract Mandatory Selection이 "
                            + "Bundle Primary Selection과 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Supporting
         * =
         * Bundle Supporting
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> supportingSet =
                new HashSet<>(
                        supportingSelections
                );

        Set<NatalPersonalityStatementSelection> expectedSupporting =
                new HashSet<>(
                        sourceBundle.supportingSelections()
                );

        if (
                !supportingSet.equals(
                        expectedSupporting
                )
        ) {

            throw new IllegalArgumentException(
                    "Contract Supporting Selection이 "
                            + "Bundle Supporting Selection과 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Internal Reference
         * =
         * Bundle 관련 내부근거
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> internalSet =
                new HashSet<>(
                        internalReferenceSelections
                );

        Set<NatalPersonalityStatementSelection> expectedInternal =
                new HashSet<>(
                        sourceBundle.relatedInternalReferences()
                );

        if (
                !internalSet.equals(
                        expectedInternal
                )
        ) {

            throw new IllegalArgumentException(
                    "Contract Internal Reference가 "
                            + "Bundle의 관련 내부근거와 "
                            + "일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Strong Selection 검증
         *
         * 반드시 Bundle Direct Selection의
         * 부분집합이어야 하고
         *
         * 실제 Strong Fact여야 한다.
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> directSet =
                new HashSet<>(
                        sourceBundle.directSelections()
                );

        for (
                NatalPersonalityStatementSelection selection
                : strongSelections
        ) {

            if (
                    !directSet.contains(
                            selection
                    )
            ) {

                throw new IllegalArgumentException(
                        "Strong Selection은 "
                                + "Bundle의 직접 Selection이어야 합니다."
                );
            }

            if (
                    !selection
                            .fact()
                            .strongUserFacingCandidate()
            ) {

                throw new IllegalArgumentException(
                        "Strong Selection에는 "
                                + "실제 Strong Fact만 들어갈 수 있습니다."
                );
            }
        }

        /*
         * =================================================
         * 기능축 정확성
         * =================================================
         */

        if (
                !new HashSet<>(
                        involvedFunctions
                )
                        .equals(
                                new HashSet<>(
                                        sourceBundle.involvedFunctions()
                                )
                        )
        ) {

            throw new IllegalArgumentException(
                    "Contract 기능축이 "
                            + "Source Bundle과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 상황 정확성
         * =================================================
         */

        if (
                !new HashSet<>(
                        situations
                )
                        .equals(
                                new HashSet<>(
                                        sourceBundle.situations()
                                )
                        )
        ) {

            throw new IllegalArgumentException(
                    "Contract 상황이 "
                            + "Source Bundle과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Qualification 정확성
         * =================================================
         */

        if (
                qualificationRequired
                        != sourceBundle
                        .qualificationRequired()
        ) {

            throw new IllegalArgumentException(
                    "Contract qualificationRequired가 "
                            + "Source Bundle과 일치하지 않습니다."
            );
        }
    }

    /**
     * 이 Bundle은 최종 설명에서
     * 반드시 다뤄야 하는가.
     */
    public boolean required() {

        return obligation
                == PersonalityStatementBundleObligation
                .REQUIRED;
    }

    /**
     * 강한 표현이 허용된
     * Selection이 실제 존재하는가.
     *
     * 이것은 Bundle 전체가
     * 강하다는 뜻은 아니다.
     */
    public boolean hasStrongSelection() {

        return !strongSelections
                .isEmpty();
    }

    /**
     * 내부 Reference가 존재하는가.
     */
    public boolean hasInternalReference() {

        return !internalReferenceSelections
                .isEmpty();
    }
}