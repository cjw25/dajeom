package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementBundleKind;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Personality Statement Bundle 전체 Aggregate.
 *
 * =================================================
 *
 * 구성:
 *
 * Statement Selection Analysis
 *
 * +
 *
 * 서로 기능적으로 연결된
 * Statement Bundle들
 *
 * +
 *
 * 전역 Background Context
 *
 * =================================================
 *
 * Background Context는:
 *
 * - CoreSelf
 * - Interpersonal Context
 *
 * 처럼 각 Bundle마다 반복해서
 * 복사하지 않는다.
 *
 * =================================================
 *
 * 후속 문장 생성에서는:
 *
 * Bundle
 * +
 * globalBackgroundContexts
 *
 * 를 함께 사용한다.
 */
public record NatalPersonalityStatementBundleAnalysis(

        NatalPersonalityStatementSelectionAnalysis
        selectionAnalysis,

        List<NatalPersonalityStatementBundle>
        bundles,

        List<NatalPersonalityStatementSelection>
        globalBackgroundContexts,

        boolean completeChart

) {

    public NatalPersonalityStatementBundleAnalysis {

        Objects.requireNonNull(
                selectionAnalysis,
                "Personality Statement Selection Analysis는 필수입니다."
        );

        bundles =
                bundles == null
                        ? List.of()
                        : List.copyOf(
                        bundles
                );

        globalBackgroundContexts =
                globalBackgroundContexts == null
                        ? List.of()
                        : List.copyOf(
                        globalBackgroundContexts
                );

        if (
                selectionAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Selection Analysis와 "
                            + "Bundle Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Bundle 완전성
         * =================================================
         */

        for (
                NatalPersonalityStatementBundle bundle
                : bundles
        ) {

            if (
                    bundle.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality Bundle의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * Background Context 검증
         * =================================================
         */

        for (
                NatalPersonalityStatementSelection context
                : globalBackgroundContexts
        ) {

            if (
                    !context.backgroundContext()
            ) {

                throw new IllegalArgumentException(
                        "globalBackgroundContexts에는 "
                                + "BACKGROUND_CONTEXT Selection만 "
                                + "들어갈 수 있습니다."
                );
            }

            if (
                    context.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Background Context의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }
        }

        /*
         * =================================================
         * 가장 중요한 검증:
         *
         * 모든 직접 출력 Selection은
         * 정확히 하나의 Bundle에 들어가야 한다.
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> bundledDirect =
                new HashSet<>();

        for (
                NatalPersonalityStatementBundle bundle
                : bundles
        ) {

            for (
                    NatalPersonalityStatementSelection selection
                    : bundle.directSelections()
            ) {

                if (
                        !bundledDirect.add(
                                selection
                        )
                ) {

                    throw new IllegalArgumentException(
                            "동일 직접 출력 Selection이 "
                                    + "여러 Bundle에 중복 포함되었습니다."
                    );
                }
            }
        }

        Set<NatalPersonalityStatementSelection> expectedDirect =
                new HashSet<>(
                        selectionAnalysis
                                .directOutputSelections()
                );

        if (
                !bundledDirect.equals(
                        expectedDirect
                )
        ) {

            throw new IllegalArgumentException(
                    "Bundle의 직접 Selection 집합이 "
                            + "Selection Analysis의 "
                            + "직접 출력 집합과 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Background Context도
         * Selection Analysis 결과와
         * 정확히 일치해야 한다.
         * =================================================
         */

        Set<NatalPersonalityStatementSelection> actualContexts =
                new HashSet<>(
                        globalBackgroundContexts
                );

        Set<NatalPersonalityStatementSelection> expectedContexts =
                new HashSet<>(
                        selectionAnalysis
                                .backgroundContexts()
                );

        if (
                !actualContexts.equals(
                        expectedContexts
                )
        ) {

            throw new IllegalArgumentException(
                    "Bundle Analysis의 Background Context가 "
                            + "Selection Analysis와 일치하지 않습니다."
            );
        }
    }

    /**
     * PRIMARY가 포함된 중심 Bundle.
     */
    public List<NatalPersonalityStatementBundle>
    primaryBundles() {

        return bundles
                .stream()
                .filter(
                        NatalPersonalityStatementBundle
                                ::primaryConnected
                )
                .toList();
    }

    /**
     * PRIMARY 없이
     * SUPPORTING끼리만 연결된 Bundle.
     *
     * 기존 SUPPORTING Role을
     * 임의로 승격하지 않는다.
     */
    public List<NatalPersonalityStatementBundle>
    supportingOnlyBundles() {

        return bundles
                .stream()
                .filter(
                        bundle ->
                                bundle.bundleKind()
                                        == PersonalityStatementBundleKind
                                        .SUPPORTING_CONNECTED
                )
                .toList();
    }

    /**
     * 강한 PRIMARY를 가진 Bundle.
     */
    public List<NatalPersonalityStatementBundle>
    strongPrimaryBundles() {

        return bundles
                .stream()
                .filter(
                        NatalPersonalityStatementBundle
                                ::hasStrongPrimary
                )
                .toList();
    }

    /**
     * 조건/충돌/중재를
     * 함께 설명해야 하는 Bundle.
     */
    public List<NatalPersonalityStatementBundle>
    qualifiedBundles() {

        return bundles
                .stream()
                .filter(
                        NatalPersonalityStatementBundle
                                ::qualificationRequired
                )
                .toList();
    }

    /**
     * 특정 기능축이 참여하는 Bundle.
     */
    public List<NatalPersonalityStatementBundle>
    bundlesInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return bundles
                .stream()
                .filter(
                        bundle ->
                                bundle
                                        .involvedFunctions()
                                        .contains(
                                                functionType
                                        )
                )
                .toList();
    }
}