package com.dajeom.analyzer;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.ClimateHealthCandidate;
import com.dajeom.model.ClimateHealthIntegrationAnalysis;
import com.dajeom.model.PassageCandidate;
import com.dajeom.model.PassageCandidateAnalysis;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.rule.ClimateHealthIntegrationRule;
import com.dajeom.type.ClimateHealthCandidateRole;
import com.dajeom.type.ClimateHealthIntegrationState;
import com.dajeom.type.FiveElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-8.
 *
 * 6-3 조후 후보
 * 6-5 병약 후보
 * 6-7 통관 후보
 *
 * 를 五行별로 통합한다.
 *
 * =================================================
 *
 * 처리
 *
 * 木
 * ├─ Climate
 * ├─ Remedy
 * └─ Passage
 *
 * 火
 * ├─ Climate
 * ├─ Remedy
 * └─ Passage
 *
 * ...
 *
 * ↓
 *
 * 각 오행마다:
 *
 * CONVERGENT
 * SUPPORTIVE
 * CONDITIONAL
 * CONFLICTED
 * INACTIVE
 *
 * ↓
 *
 * PRIMARY
 * SECONDARY
 * DEFERRED
 * REJECTED
 *
 * =================================================
 *
 * 중요한 원칙
 *
 * 단순 종합점수를 만들지 않는다.
 *
 * 조후 4점
 * 병약 3점
 * 통관 2점
 *
 * 같은 식으로 합산하지 않는다.
 *
 * 각 분석법의 독립적인 판단이:
 *
 * - 수렴하는지
 * - 조건부인지
 * - 충돌하는지
 *
 * 를 구조적으로 비교한다.
 */
@Component
public class ClimateHealthIntegrationAnalyzer {

    private final ClimateHealthIntegrationRule
            climateHealthIntegrationRule;

    public ClimateHealthIntegrationAnalyzer(
            ClimateHealthIntegrationRule
                    climateHealthIntegrationRule
    ) {

        this.climateHealthIntegrationRule =
                Objects.requireNonNull(
                        climateHealthIntegrationRule,
                        "ClimateHealthIntegrationRule이 필요합니다."
                );
    }

    /**
     * 6-7 PassageCandidateAnalysis 안에는
     * RemedyAnalysis가 있고,
     *
     * RemedyAnalysis 안에는
     * ClimateElementCandidateAnalysis가 있으므로
     *
     * 6-7 결과 하나에서 6-8을 실행할 수 있다.
     */
    public ClimateHealthIntegrationAnalysis analyze(
            PassageCandidateAnalysis passageAnalysis
    ) {

        Objects.requireNonNull(
                passageAnalysis,
                "통관 후보 분석이 필요합니다."
        );

        RemedyAnalysis remedyAnalysis =
                passageAnalysis
                        .remedyAnalysis();

        ClimateElementCandidateAnalysis climateAnalysis =
                remedyAnalysis
                        .climateElementCandidateAnalysis();

        return analyze(
                climateAnalysis,
                remedyAnalysis,
                passageAnalysis
        );
    }

    /**
     * 6차 전체 Engine에서 사용할
     * 명시적 오버로드.
     */
    public ClimateHealthIntegrationAnalysis analyze(

            ClimateElementCandidateAnalysis climateAnalysis,

            RemedyAnalysis remedyAnalysis,

            PassageCandidateAnalysis passageAnalysis

    ) {

        Objects.requireNonNull(
                climateAnalysis,
                "조후 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                remedyAnalysis,
                "병약 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                passageAnalysis,
                "통관 후보 분석이 필요합니다."
        );

        validateConsistency(
                climateAnalysis,
                remedyAnalysis,
                passageAnalysis
        );

        List<ClimateHealthCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            ClimateElementCandidate climateCandidate =
                    climateAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "조후 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            RemedyCandidate remedyCandidate =
                    remedyAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "병약 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            PassageCandidate passageCandidate =
                    passageAnalysis
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "통관 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            validateElementConsistency(
                    element,
                    climateCandidate,
                    remedyCandidate,
                    passageCandidate
            );

            int activeAxisCount =
                    climateHealthIntegrationRule
                            .activeAxisCount(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            int selectedAxisCount =
                    climateHealthIntegrationRule
                            .selectedAxisCount(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            int primaryAxisCount =
                    climateHealthIntegrationRule
                            .primaryAxisCount(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            int conditionalAxisCount =
                    climateHealthIntegrationRule
                            .conditionalAxisCount(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            boolean hardConflict =
                    climateHealthIntegrationRule
                            .hasHardConflict(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            boolean saturated =
                    climateHealthIntegrationRule
                            .saturated(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            ClimateHealthIntegrationState integrationState =
                    climateHealthIntegrationRule
                            .determineIntegrationState(
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            ClimateHealthCandidateRole role =
                    climateHealthIntegrationRule
                            .determineRole(
                                    integrationState,
                                    climateCandidate,
                                    remedyCandidate,
                                    passageCandidate
                            );

            candidates.add(
                    new ClimateHealthCandidate(
                            element,
                            integrationState,
                            role,
                            climateCandidate,
                            remedyCandidate,
                            passageCandidate,
                            activeAxisCount,
                            selectedAxisCount,
                            primaryAxisCount,
                            conditionalAxisCount,
                            hardConflict,
                            saturated,
                            climateHealthIntegrationRule
                                    .reasonsFor(
                                            climateCandidate,
                                            remedyCandidate,
                                            passageCandidate
                                    )
                    )
            );
        }

        /*
         * ================================================
         * 결과 정렬
         * ================================================
         *
         * 1.
         * PRIMARY / SECONDARY / DEFERRED / REJECTED
         *
         * 2.
         * 통합 상태
         *
         * 3.
         * PRIMARY 분석축 수
         *
         * 4.
         * 선택 분석축 수
         *
         * 5.
         * 높은 우선도 분석축 수
         *
         * 6.
         * 현재 오행 비중이 낮은 후보
         *
         * 순.
         *
         * 이 정렬은 종합점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        ClimateHealthCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                (
                                                        ClimateHealthCandidate candidate
                                                ) ->
                                                        candidate
                                                                .integrationState()
                                                                .rank()
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ClimateHealthCandidate
                                                        ::primaryAxisCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ClimateHealthCandidate
                                                        ::selectedAxisCount
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                ClimateHealthCandidate
                                                        ::highPriorityAxisCount
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                ClimateHealthCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        return new ClimateHealthIntegrationAnalysis(
                passageAnalysis.dayMaster(),
                passageAnalysis.dayMasterElement(),
                passageAnalysis.monthBranch(),
                climateAnalysis,
                remedyAnalysis,
                passageAnalysis,
                candidates,
                passageAnalysis.completeChart()
        );
    }

    private void validateElementConsistency(

            FiveElement element,

            ClimateElementCandidate climateCandidate,

            RemedyCandidate remedyCandidate,

            PassageCandidate passageCandidate

    ) {

        if (
                climateCandidate.element()
                        != element

                        || remedyCandidate.element()
                        != element

                        || passageCandidate.element()
                        != element
        ) {

            throw new IllegalStateException(
                    "통합 대상 오행이 서로 일치하지 않습니다: "
                            + element
            );
        }

        int climateShare =
                climateCandidate
                        .existingShareBasisPoints();

        int remedyShare =
                remedyCandidate
                        .existingShareBasisPoints();

        int passageShare =
                passageCandidate
                        .existingShareBasisPoints();

        if (
                climateShare != remedyShare
                        || climateShare != passageShare
        ) {

            throw new IllegalStateException(
                    "조후·병약·통관 분석의 오행 비중이 일치하지 않습니다: "
                            + element
            );
        }
    }

    private void validateConsistency(

            ClimateElementCandidateAnalysis climateAnalysis,

            RemedyAnalysis remedyAnalysis,

            PassageCandidateAnalysis passageAnalysis

    ) {

        if (
                climateAnalysis.dayMaster()
                        != remedyAnalysis.dayMaster()

                        || climateAnalysis.dayMaster()
                        != passageAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                climateAnalysis.monthBranch()
                        != remedyAnalysis.monthBranch()

                        || climateAnalysis.monthBranch()
                        != passageAnalysis.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                climateAnalysis.completeChart()
                        != remedyAnalysis.completeChart()

                        || climateAnalysis.completeChart()
                        != passageAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "조후·병약·통관 분석의 원국 완전성 상태가 서로 일치하지 않습니다."
            );
        }
    }
}