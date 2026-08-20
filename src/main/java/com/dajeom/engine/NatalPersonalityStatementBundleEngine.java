package com.dajeom.engine;

import com.dajeom.model.NatalPersonalityStatementBundle;
import com.dajeom.model.NatalPersonalityStatementBundleAnalysis;
import com.dajeom.model.NatalPersonalityStatementSelection;
import com.dajeom.model.NatalPersonalityStatementSelectionAnalysis;
import com.dajeom.type.NatalResponseSituationType;
import com.dajeom.type.NatalTenGodFunctionType;
import com.dajeom.type.PersonalityStatementBundleKind;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;

/**
 * 직접 출력 가능한 Personality Selection들을
 * 실제 기능 연결관계에 따라 Bundle로 묶는다.
 *
 * =================================================
 *
 * 연결 기준:
 *
 * 두 Selection이
 * 최소 하나의 NatalTenGodFunctionType을
 * 공유하는가.
 *
 * =================================================
 *
 * 예:
 *
 * A:
 * EXPRESSION + RESPONSIBILITY
 *
 * B:
 * RESPONSIBILITY + PROCESSING
 *
 * C:
 * PROCESSING
 *
 * 이면
 *
 * A ↔ B ↔ C
 *
 * 로 연결되므로
 * 하나의 Bundle이 된다.
 *
 * =================================================
 *
 * 이것은 점수나 유사도 계산이 아니다.
 *
 * 구조화된 기능축의
 * 실제 연결 여부만 사용한다.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * 1.
 * INTERNAL_REFERENCE는
 * Bundle을 연결하는 기준으로 사용하지 않는다.
 *
 * 약한 근거 하나 때문에
 * 서로 독립적인 직접판정을
 * 하나로 합치는 것을 방지한다.
 *
 * =================================================
 *
 * 2.
 * INTERNAL_REFERENCE는
 * Bundle이 완성된 뒤
 * 이미 존재하는 기능축/상황과
 * 겹칠 때만 관련 Reference로 붙인다.
 *
 * =================================================
 *
 * 3.
 * BACKGROUND_CONTEXT는
 * Bundle마다 반복 복사하지 않는다.
 *
 * Analysis의 globalBackgroundContexts로
 * 따로 보존한다.
 *
 * =================================================
 *
 * 4.
 * PRIMARY가 없는 SUPPORTING도
 * 삭제하지 않는다.
 *
 * SUPPORTING_CONNECTED Bundle로 보존한다.
 *
 * =================================================
 *
 * 5.
 * 모든 직접 출력 Selection은
 * 정확히 하나의 Bundle에 들어간다.
 */
@Component
public class NatalPersonalityStatementBundleEngine {

    public NatalPersonalityStatementBundleAnalysis analyze(

            NatalPersonalityStatementSelectionAnalysis
                    selectionAnalysis

    ) {

        Objects.requireNonNull(
                selectionAnalysis,
                "Personality Statement Selection Analysis는 필수입니다."
        );

        List<NatalPersonalityStatementSelection> directSelections =
                selectionAnalysis
                        .directOutputSelections();

        List<NatalPersonalityStatementBundle> bundles =
                createConnectedBundles(
                        selectionAnalysis,
                        directSelections
                );

        return new NatalPersonalityStatementBundleAnalysis(

                selectionAnalysis,

                bundles,

                selectionAnalysis.backgroundContexts(),

                selectionAnalysis.completeChart()
        );
    }

    /**
     * =================================================
     * 직접 출력 Selection의
     * Connected Component 계산
     * =================================================
     *
     * 그래프 연결 기준은
     * 오직 공유 기능축이다.
     */
    private List<NatalPersonalityStatementBundle>
    createConnectedBundles(

            NatalPersonalityStatementSelectionAnalysis
                    selectionAnalysis,

            List<NatalPersonalityStatementSelection>
                    directSelections

    ) {

        if (
                directSelections.isEmpty()
        ) {

            return List.of();
        }

        boolean[] visited =
                new boolean[
                        directSelections.size()
                        ];

        List<NatalPersonalityStatementBundle> result =
                new ArrayList<>();

        for (
                int start = 0;
                start < directSelections.size();
                start++
        ) {

            if (
                    visited[start]
            ) {

                continue;
            }

            List<NatalPersonalityStatementSelection> component =
                    new ArrayList<>();

            Queue<Integer> queue =
                    new ArrayDeque<>();

            queue.add(
                    start
            );

            visited[start] =
                    true;

            while (
                    !queue.isEmpty()
            ) {

                int currentIndex =
                        queue.remove();

                NatalPersonalityStatementSelection current =
                        directSelections.get(
                                currentIndex
                        );

                component.add(
                        current
                );

                for (
                        int candidateIndex = 0;
                        candidateIndex < directSelections.size();
                        candidateIndex++
                ) {

                    if (
                            visited[candidateIndex]
                    ) {

                        continue;
                    }

                    NatalPersonalityStatementSelection candidate =
                            directSelections.get(
                                    candidateIndex
                            );

                    if (
                            sharesFunction(
                                    current,
                                    candidate
                            )
                    ) {

                        visited[candidateIndex] =
                                true;

                        queue.add(
                                candidateIndex
                        );
                    }
                }
            }

            result.add(
                    createBundle(
                            selectionAnalysis,
                            component
                    )
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 두 직접 Selection이
     * 실제 기능축을 하나라도 공유하는가.
     */
    private boolean sharesFunction(

            NatalPersonalityStatementSelection left,

            NatalPersonalityStatementSelection right

    ) {

        for (
                NatalTenGodFunctionType functionType
                : left.involvedFunctions()
        ) {

            if (
                    right
                            .involvedFunctions()
                            .contains(
                                    functionType
                            )
            ) {

                return true;
            }
        }

        return false;
    }

    /**
     * =================================================
     * Connected Component
     * →
     * Statement Bundle
     * =================================================
     */
    private NatalPersonalityStatementBundle
    createBundle(

            NatalPersonalityStatementSelectionAnalysis
                    selectionAnalysis,

            List<NatalPersonalityStatementSelection>
                    component

    ) {

        List<NatalPersonalityStatementSelection> primarySelections =
                component
                        .stream()
                        .filter(
                                NatalPersonalityStatementSelection
                                        ::primary
                        )
                        .toList();

        List<NatalPersonalityStatementSelection> supportingSelections =
                component
                        .stream()
                        .filter(
                                NatalPersonalityStatementSelection
                                        ::supporting
                        )
                        .toList();

        PersonalityStatementBundleKind bundleKind =
                primarySelections.isEmpty()

                        ? PersonalityStatementBundleKind
                        .SUPPORTING_CONNECTED

                        : PersonalityStatementBundleKind
                        .PRIMARY_CONNECTED;

        List<NatalTenGodFunctionType> involvedFunctions =
                collectFunctions(
                        component
                );

        List<NatalResponseSituationType> situations =
                collectSituations(
                        component
                );

        List<NatalPersonalityStatementSelection>
                relatedInternalReferences =
                findRelatedInternalReferences(

                        selectionAnalysis,

                        involvedFunctions,

                        situations
                );

        boolean qualificationRequired =
                component
                        .stream()
                        .anyMatch(
                                NatalPersonalityStatementSelection
                                        ::qualificationRequired
                        );

        return new NatalPersonalityStatementBundle(

                bundleKind,

                primarySelections,

                supportingSelections,

                relatedInternalReferences,

                involvedFunctions,

                situations,

                qualificationRequired,

                selectionAnalysis.completeChart()
        );
    }

    /**
     * 직접 Selection들의
     * 기능축 중복 제거.
     */
    private List<NatalTenGodFunctionType>
    collectFunctions(

            List<NatalPersonalityStatementSelection>
                    selections

    ) {

        Set<NatalTenGodFunctionType> result =
                new LinkedHashSet<>();

        for (
                NatalPersonalityStatementSelection selection
                : selections
        ) {

            result.addAll(
                    selection.involvedFunctions()
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * 직접 Selection들의
     * Situation 중복 제거.
     */
    private List<NatalResponseSituationType>
    collectSituations(

            List<NatalPersonalityStatementSelection>
                    selections

    ) {

        Set<NatalResponseSituationType> result =
                new LinkedHashSet<>();

        for (
                NatalPersonalityStatementSelection selection
                : selections
        ) {

            result.addAll(
                    selection
                            .fact()
                            .situations()
            );
        }

        return List.copyOf(
                result
        );
    }

    /**
     * =================================================
     * Bundle과 관련된 INTERNAL_REFERENCE
     * =================================================
     *
     * 조건:
     *
     * 1.
     * 기능축이 하나라도 겹치거나
     *
     * 또는
     *
     * 2.
     * 상황이 하나라도 겹친다.
     *
     * =================================================
     *
     * 주의:
     *
     * INTERNAL_REFERENCE는
     * 이미 만들어진 Bundle에 붙기만 한다.
     *
     * Bundle 연결 자체에는
     * 절대로 사용하지 않는다.
     */
    private List<NatalPersonalityStatementSelection>
    findRelatedInternalReferences(

            NatalPersonalityStatementSelectionAnalysis
                    selectionAnalysis,

            List<NatalTenGodFunctionType>
                    involvedFunctions,

            List<NatalResponseSituationType>
                    situations

    ) {

        List<NatalPersonalityStatementSelection> result =
                new ArrayList<>();

        for (
                NatalPersonalityStatementSelection internal
                : selectionAnalysis.internalReferences()
        ) {

            if (
                    sharesAnyFunction(
                            internal,
                            involvedFunctions
                    )

                            || sharesAnySituation(
                            internal,
                            situations
                    )
            ) {

                result.add(
                        internal
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private boolean sharesAnyFunction(

            NatalPersonalityStatementSelection selection,

            List<NatalTenGodFunctionType>
                    involvedFunctions

    ) {

        for (
                NatalTenGodFunctionType functionType
                : selection.involvedFunctions()
        ) {

            if (
                    involvedFunctions.contains(
                            functionType
                    )
            ) {

                return true;
            }
        }

        return false;
    }

    private boolean sharesAnySituation(

            NatalPersonalityStatementSelection selection,

            List<NatalResponseSituationType>
                    situations

    ) {

        for (
                NatalResponseSituationType situation
                : selection
                .fact()
                .situations()
        ) {

            if (
                    situations.contains(
                            situation
                    )
            ) {

                return true;
            }
        }

        return false;
    }
}