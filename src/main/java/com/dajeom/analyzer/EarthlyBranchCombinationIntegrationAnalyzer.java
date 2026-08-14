package com.dajeom.analyzer;

import com.dajeom.model.EarthlyBranchCombinationIntegratedCandidate;
import com.dajeom.model.EarthlyBranchCombinationIntegrationAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionAnalysis;
import com.dajeom.model.EarthlyBranchCombinationObstructionEvidence;
import com.dajeom.rule.EarthlyBranchCombinationIntegrationRule;
import com.dajeom.type.EarthlyBranchCombinationIntegrationReasonType;
import com.dajeom.type.EarthlyBranchCombinationIntegrationRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 9차-8.
 *
 * 9-7까지 계산된 모든 지지 결합 후보를
 * 서로 교차 비교한다.
 *
 * =================================================
 *
 * 분석 내용:
 *
 * 1.
 * 구성원 공유 여부
 *
 * 2.
 * 같은 목표 오행으로 수렴하는가
 *
 * 3.
 * 다른 목표 오행으로 경쟁하는가
 *
 * 4.
 * 어느 후보가 상대적으로 더 우세한가
 *
 * 5.
 * 최종 통합 역할
 *
 * PRIMARY
 * SUPPORTING
 * COMPETING
 * DEFERRED
 * SUPPRESSED
 *
 * =================================================
 *
 * 후보를 삭제하지 않는다.
 *
 * 후보의 원본 9-4/9-5/9-6 상태와
 * 9-7 방해 Evidence도 그대로 보존한다.
 */
@Component
public class EarthlyBranchCombinationIntegrationAnalyzer {

    private final EarthlyBranchCombinationIntegrationRule
            earthlyBranchCombinationIntegrationRule;

    public EarthlyBranchCombinationIntegrationAnalyzer(
            EarthlyBranchCombinationIntegrationRule
                    earthlyBranchCombinationIntegrationRule
    ) {

        this.earthlyBranchCombinationIntegrationRule =
                Objects.requireNonNull(
                        earthlyBranchCombinationIntegrationRule,
                        "EarthlyBranchCombinationIntegrationRule이 필요합니다."
                );
    }

    public EarthlyBranchCombinationIntegrationAnalysis analyze(
            EarthlyBranchCombinationObstructionAnalysis
                    obstructionAnalysis
    ) {

        Objects.requireNonNull(
                obstructionAnalysis,
                "9-7 지지 결합 방해 분석이 필요합니다."
        );

        List<EarthlyBranchCombinationObstructionEvidence> sourceCandidates =
                obstructionAnalysis.evidences();

        List<EarthlyBranchCombinationIntegratedCandidate> results =
                new ArrayList<>();

        for (
                EarthlyBranchCombinationObstructionEvidence candidate
                : sourceCandidates
        ) {

            /*
             * =================================================
             * 1. 같은 구성원 + 같은 목표 오행
             * =================================================
             */
            List<EarthlyBranchCombinationObstructionEvidence>
                    convergentCandidates =
                    sourceCandidates
                            .stream()
                            .filter(
                                    other ->
                                            !other.equals(
                                                    candidate
                                            )
                            )
                            .filter(
                                    other ->
                                            earthlyBranchCombinationIntegrationRule
                                                    .sharesMember(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .filter(
                                    other ->
                                            earthlyBranchCombinationIntegrationRule
                                                    .sameTarget(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 2. 같은 목표 중 더 강한 후보
             * =================================================
             */
            List<EarthlyBranchCombinationObstructionEvidence>
                    higherPriorityConvergentCandidates =
                    convergentCandidates
                            .stream()
                            .filter(
                                    other ->
                                            earthlyBranchCombinationIntegrationRule
                                                    .higherPriority(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 3. 같은 구성원 + 다른 목표 오행
             * =================================================
             */
            List<EarthlyBranchCombinationObstructionEvidence>
                    competingCandidates =
                    sourceCandidates
                            .stream()
                            .filter(
                                    other ->
                                            !other.equals(
                                                    candidate
                                            )
                            )
                            .filter(
                                    other ->
                                            earthlyBranchCombinationIntegrationRule
                                                    .sharesMember(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .filter(
                                    other ->
                                            !earthlyBranchCombinationIntegrationRule
                                                    .sameTarget(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 4. 더 강한 다른 오행 후보
             * =================================================
             */
            List<EarthlyBranchCombinationObstructionEvidence>
                    higherPriorityCompetingCandidates =
                    competingCandidates
                            .stream()
                            .filter(
                                    other ->
                                            earthlyBranchCombinationIntegrationRule
                                                    .higherPriority(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 5. 동급 다른 오행 후보
             * =================================================
             */
            List<EarthlyBranchCombinationObstructionEvidence>
                    equalPriorityCompetingCandidates =
                    competingCandidates
                            .stream()
                            .filter(
                                    other ->
                                            earthlyBranchCombinationIntegrationRule
                                                    .equalPriority(
                                                            candidate,
                                                            other
                                                    )
                            )
                            .toList();

            /*
             * =================================================
             * 6. 최종 역할
             * =================================================
             */
            EarthlyBranchCombinationIntegrationRole role =
                    earthlyBranchCombinationIntegrationRule
                            .determineRole(
                                    candidate,
                                    convergentCandidates,
                                    higherPriorityConvergentCandidates,
                                    competingCandidates,
                                    higherPriorityCompetingCandidates,
                                    equalPriorityCompetingCandidates
                            );

            /*
             * =================================================
             * 7. 판정 근거
             * =================================================
             */
            List<EarthlyBranchCombinationIntegrationReasonType> reasons =
                    earthlyBranchCombinationIntegrationRule
                            .buildReasons(
                                    candidate,
                                    role,
                                    convergentCandidates,
                                    higherPriorityConvergentCandidates,
                                    competingCandidates,
                                    higherPriorityCompetingCandidates,
                                    equalPriorityCompetingCandidates
                            );

            results.add(
                    new EarthlyBranchCombinationIntegratedCandidate(
                            candidate,
                            role,
                            convergentCandidates,
                            higherPriorityConvergentCandidates,
                            competingCandidates,
                            higherPriorityCompetingCandidates,
                            equalPriorityCompetingCandidates,
                            reasons
                    )
            );
        }

        /*
         * =================================================
         * 정렬
         * =================================================
         *
         * 1. 최종 역할
         * 2. 실제 우선도
         * 3. 원국 첫 위치
         *
         * 길흉 순위가 아니다.
         */
        results.sort(
                (first, second) -> {

                    int roleComparison =
                            Integer.compare(
                                    second.role().rank(),
                                    first.role().rank()
                            );

                    if (
                            roleComparison != 0
                    ) {

                        return roleComparison;
                    }

                    int priorityComparison =
                            earthlyBranchCombinationIntegrationRule
                                    .comparePriority(
                                            first.sourceEvidence(),
                                            second.sourceEvidence()
                                    );

                    if (
                            priorityComparison != 0
                    ) {

                        /*
                         * comparePriority는
                         * 양수 = first 우세.
                         *
                         * Comparator에서는
                         * first를 앞으로 보내기 위해 음수 반환.
                         */
                        return -priorityComparison;
                    }

                    int kindComparison =
                            Integer.compare(
                                    first
                                            .candidateKind()
                                            .ordinal(),

                                    second
                                            .candidateKind()
                                            .ordinal()
                            );

                    if (
                            kindComparison != 0
                    ) {

                        return kindComparison;
                    }

                    return Integer.compare(
                            first
                                    .candidatePositions()
                                    .get(0)
                                    .ordinal(),

                            second
                                    .candidatePositions()
                                    .get(0)
                                    .ordinal()
                    );
                }
        );

        return new EarthlyBranchCombinationIntegrationAnalysis(
                obstructionAnalysis.dayMaster(),
                obstructionAnalysis,
                results,
                obstructionAnalysis.completeChart()
        );
    }
}