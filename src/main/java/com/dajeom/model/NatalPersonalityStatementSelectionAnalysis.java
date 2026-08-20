package com.dajeom.model;

import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementSelectionRole;

import java.util.List;
import java.util.Objects;

/**
 * Personality Statement Selection
 * 전체 Aggregate.
 *
 * =================================================
 *
 * 여기까지 오면:
 *
 * 어떤 Fact가
 *
 * - 중심 설명인지
 * - 보조 설명인지
 * - 배경 Context인지
 * - 내부 Reference인지
 *
 * 가 결정된다.
 *
 * =================================================
 *
 * 여전히:
 *
 * 사용자용 한국어 문장은
 * 생성하지 않는다.
 */
public record NatalPersonalityStatementSelectionAnalysis(

        NatalPersonalityStatementFactAnalysis
        statementFactAnalysis,

        List<NatalPersonalityStatementSelection>
        selections,

        boolean completeChart

) {

    public NatalPersonalityStatementSelectionAnalysis {

        Objects.requireNonNull(
                statementFactAnalysis,
                "Personality Statement Fact Analysis는 필수입니다."
        );

        selections =
                selections == null
                        ? List.of()
                        : List.copyOf(
                        selections
                );

        if (
                statementFactAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "Statement Fact Analysis와 "
                            + "Selection Analysis의 "
                            + "원국 완전성 상태가 일치하지 않습니다."
            );
        }

        /*
         * 모든 Fact가 정확히 하나의
         * Selection을 가져야 한다.
         */
        if (
                selections.size()
                        != statementFactAnalysis
                        .facts()
                        .size()
        ) {

            throw new IllegalArgumentException(
                    "모든 Personality Statement Fact는 "
                            + "정확히 하나의 Selection을 가져야 합니다."
            );
        }

        for (
                NatalPersonalityStatementSelection selection
                : selections
        ) {

            if (
                    selection.completeChart()
                            != completeChart
            ) {

                throw new IllegalArgumentException(
                        "Personality Selection의 "
                                + "원국 완전성 상태가 일치하지 않습니다."
                );
            }
        }
    }

    /**
     * 최종 설명의 중심 Fact.
     */
    public List<NatalPersonalityStatementSelection>
    primarySelections() {

        return selections
                .stream()
                .filter(
                        NatalPersonalityStatementSelection
                                ::primary
                )
                .toList();
    }

    /**
     * 중심 설명을 보강하는 Fact.
     */
    public List<NatalPersonalityStatementSelection>
    supportingSelections() {

        return selections
                .stream()
                .filter(
                        NatalPersonalityStatementSelection
                                ::supporting
                )
                .toList();
    }

    /**
     * 실제 사용자 출력 후보.
     *
     * PRIMARY를 먼저 반환하고
     * 그 뒤 SUPPORTING을 반환한다.
     *
     * 점수 정렬이 아니라
     * 역할 순서다.
     */
    public List<NatalPersonalityStatementSelection>
    directOutputSelections() {

        return java.util.stream.Stream
                .concat(
                        primarySelections()
                                .stream(),

                        supportingSelections()
                                .stream()
                )
                .toList();
    }

    /**
     * 최종 문장에는 직접 사용하지 않지만
     * 해석 Context로 전달할 Selection.
     */
    public List<NatalPersonalityStatementSelection>
    backgroundContexts() {

        return selections
                .stream()
                .filter(
                        NatalPersonalityStatementSelection
                                ::backgroundContext
                )
                .toList();
    }

    /**
     * 내부에만 남는 근거.
     */
    public List<NatalPersonalityStatementSelection>
    internalReferences() {

        return selections
                .stream()
                .filter(
                        NatalPersonalityStatementSelection
                                ::internalReference
                )
                .toList();
    }

    /**
     * 조건/상충/중재 설명을
     * 같이 해야 하는 직접 출력 Selection.
     */
    public List<NatalPersonalityStatementSelection>
    qualifiedDirectSelections() {

        return directOutputSelections()
                .stream()
                .filter(
                        NatalPersonalityStatementSelection
                                ::qualificationRequired
                )
                .toList();
    }

    /**
     * 특정 기능축에 관련된
     * 전체 Selection.
     *
     * 내부 Reference까지 포함한다.
     */
    public List<NatalPersonalityStatementSelection>
    selectionsInvolving(

            NatalTenGodFunctionType functionType

    ) {

        if (
                functionType == null
        ) {

            return List.of();
        }

        return selections
                .stream()
                .filter(
                        selection ->
                                selection
                                        .involvedFunctions()
                                        .contains(
                                                functionType
                                        )
                )
                .toList();
    }

    /**
     * 특정 Role 조회.
     */
    public List<NatalPersonalityStatementSelection>
    selectionsOfRole(

            PersonalityStatementSelectionRole role

    ) {

        if (
                role == null
        ) {

            return List.of();
        }

        return selections
                .stream()
                .filter(
                        selection ->
                                selection.role()
                                        == role
                )
                .toList();
    }
}