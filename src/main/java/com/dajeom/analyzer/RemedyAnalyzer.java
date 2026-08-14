package com.dajeom.analyzer;

import com.dajeom.model.ClimateElementCandidate;
import com.dajeom.model.ClimateElementCandidateAnalysis;
import com.dajeom.model.PathologyAnalysis;
import com.dajeom.model.PathologyEvidence;
import com.dajeom.model.RemedyAnalysis;
import com.dajeom.model.RemedyCandidate;
import com.dajeom.model.RemedyPathologyLink;
import com.dajeom.model.StructureAnalysis;
import com.dajeom.rule.RemedyRule;
import com.dajeom.type.ClimateElementPresenceLevel;
import com.dajeom.type.DayMasterElementRole;
import com.dajeom.type.FiveElement;
import com.dajeom.type.PathologyPatternType;
import com.dajeom.type.RemedyCandidateRole;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 6차-5.
 *
 * 6-4에서 탐지된 병(病) 구조를
 * 五行 약(藥) 후보로 변환한다.
 *
 * 입력:
 *
 * - 5차 StructureAnalysis
 * - 6-3 ClimateElementCandidateAnalysis
 * - 6-4 PathologyAnalysis
 *
 * 출력:
 *
 * 木 火 土 金 水 각각:
 *
 * - 어떤 병을 완화하는지
 * - 어떤 방식으로 작용하는지
 * - 다른 병을 악화하는지
 * - 신강신약 방향과 충돌하는지
 * - 조후 방향과 충돌하는지
 * - 현재 오행량이 과한지
 *
 * 를 분석한다.
 *
 * 중요한 원칙:
 *
 * PRIMARY는 여러 개 존재할 수 있다.
 *
 * 아직 최종 용신을 결정하지 않는다.
 */
@Component
public class RemedyAnalyzer {

    private final RemedyRule
            remedyRule;

    public RemedyAnalyzer(
            RemedyRule remedyRule
    ) {

        this.remedyRule =
                Objects.requireNonNull(
                        remedyRule,
                        "RemedyRule이 필요합니다."
                );
    }

    public RemedyAnalysis analyze(

            StructureAnalysis structure,

            ClimateElementCandidateAnalysis climateCandidates,

            PathologyAnalysis pathology

    ) {

        Objects.requireNonNull(
                structure,
                "격국 분석 결과가 필요합니다."
        );

        Objects.requireNonNull(
                climateCandidates,
                "조후 오행 후보 분석이 필요합니다."
        );

        Objects.requireNonNull(
                pathology,
                "병 구조 분석이 필요합니다."
        );

        validateConsistency(
                structure,
                climateCandidates,
                pathology
        );

        List<RemedyCandidate> candidates =
                new ArrayList<>();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            ClimateElementCandidate climateCandidate =
                    climateCandidates
                            .candidateOf(
                                    element
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "조후 오행 후보를 찾을 수 없습니다: "
                                                            + element
                                            )
                            );

            DayMasterElementRole dayMasterRole =
                    remedyRule
                            .determineDayMasterRole(
                                    structure
                                            .dayMasterStrengthAnalysis()
                                            .dayMasterElement(),
                                    element
                            );

            List<RemedyPathologyLink> remedyLinks =
                    remedyRule
                            .findRemedyLinks(
                                    element,
                                    dayMasterRole,
                                    pathology,
                                    structure,
                                    climateCandidate
                            );

            List<PathologyPatternType>
                    aggravatedPathologies =
                    remedyRule
                            .findAggravatedPathologies(
                                    element,
                                    dayMasterRole,
                                    pathology,
                                    structure,
                                    climateCandidate
                            );

            boolean strengthDirectionConflict =
                    remedyRule
                            .hasStrengthDirectionConflict(
                                    structure
                                            .dayMasterStrengthAnalysis()
                                            .level(),
                                    dayMasterRole
                            );

            boolean climateConflict =
                    climateCandidate
                            .hasConflict();

            ClimateElementPresenceLevel presenceLevel =
                    climateCandidate
                            .presenceLevel();

            RemedyCandidateRole role =
                    remedyRule
                            .determineRole(
                                    remedyLinks,
                                    aggravatedPathologies,
                                    pathology,
                                    presenceLevel,
                                    strengthDirectionConflict,
                                    climateConflict
                            );

            /*
             * REJECTED 후보는
             * 약 작용 Evidence를 최종 객체에
             * 보존하지 않는다.
             *
             * "병을 고칠 수 있지만 rejected"
             * 같은 모순 상태를 막는다.
             */
            List<RemedyPathologyLink> finalLinks =
                    role.rejected()
                            ? List.of()
                            : remedyLinks;

            candidates.add(
                    new RemedyCandidate(
                            element,
                            dayMasterRole,
                            role,
                            climateCandidate.role(),
                            presenceLevel,
                            climateCandidate
                                    .existingShareBasisPoints(),
                            finalLinks,
                            aggravatedPathologies,
                            strengthDirectionConflict,
                            climateConflict
                    )
            );
        }

        /*
         * ================================================
         * 정렬
         * ================================================
         *
         * 1.
         * PRIMARY
         * SECONDARY
         * CONDITIONAL
         * REJECTED
         *
         * 2.
         * 더 심한 병을 완화하는 후보
         *
         * 3.
         * 여러 병을 동시에 완화하는 후보
         *
         * 4.
         * 현재 오행량이 적은 후보
         *
         * 순으로 정렬한다.
         *
         * 이 정렬은 용신 점수가 아니다.
         */
        candidates.sort(
                Comparator
                        .comparingInt(
                                (
                                        RemedyCandidate candidate
                                ) ->
                                        candidate
                                                .role()
                                                .rank()
                        )
                        .reversed()

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                this
                                                        ::highestAddressedSeverityRank
                                        )
                                        .reversed()
                        )

                        .thenComparing(
                                Comparator
                                        .comparingInt(
                                                RemedyCandidate
                                                        ::meaningfulAddressedPathologyCount
                                        )
                                        .reversed()
                        )

                        .thenComparingInt(
                                RemedyCandidate
                                        ::existingShareBasisPoints
                        )

                        .thenComparingInt(
                                candidate ->
                                        candidate
                                                .element()
                                                .ordinal()
                        )
        );

        List<PathologyEvidence> unresolved =
                pathology
                        .evidences()
                        .stream()
                        .filter(
                                pathologyEvidence ->
                                        candidates
                                                .stream()
                                                .noneMatch(
                                                        candidate ->
                                                                addressesEvidence(
                                                                        candidate,
                                                                        pathologyEvidence
                                                                )
                                                )
                        )
                        .toList();

        return new RemedyAnalysis(
                structure.dayMaster(),
                structure.monthBranch(),
                pathology,
                climateCandidates,
                candidates,
                unresolved,
                structure.completeChart()
        );
    }

    private int highestAddressedSeverityRank(
            RemedyCandidate candidate
    ) {

        return candidate
                .remedyLinks()
                .stream()
                .map(
                        RemedyPathologyLink
                                ::pathologySeverity
                )
                .mapToInt(
                        severity ->
                                severity.rank()
                )
                .max()
                .orElse(
                        0
                );
    }

    /**
     * PathologyEvidence가
     * 실제 약 후보 하나 이상에 의해
     * 다뤄지고 있는지 확인한다.
     *
     * ELEMENT_OVERCONCENTRATION은
     * 같은 PatternType으로
     * 여러 오행에서 발생할 수 있으므로
     * 대상 오행까지 비교한다.
     */
    private boolean addressesEvidence(

            RemedyCandidate candidate,

            PathologyEvidence evidence

    ) {

        if (
                candidate.rejected()
        ) {

            return false;
        }

        return candidate
                .remedyLinks()
                .stream()
                .anyMatch(
                        link -> {

                            if (
                                    link.pathologyPatternType()
                                            != evidence.patternType()
                            ) {

                                return false;
                            }

                            if (
                                    evidence.patternType()
                                            == PathologyPatternType
                                            .ELEMENT_OVERCONCENTRATION
                            ) {

                                if (
                                        evidence
                                                .relatedElements()
                                                .isEmpty()
                                ) {

                                    return false;
                                }

                                return link.pathologyElement()
                                        == evidence
                                        .relatedElements()
                                        .get(
                                                0
                                        );
                            }

                            return true;
                        }
                );
    }

    private void validateConsistency(

            StructureAnalysis structure,

            ClimateElementCandidateAnalysis climateCandidates,

            PathologyAnalysis pathology

    ) {

        if (
                structure.dayMaster()
                        != climateCandidates.dayMaster()

                        || structure.dayMaster()
                        != pathology.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "격국·조후·병약 분석의 일간이 서로 일치하지 않습니다."
            );
        }

        if (
                structure.monthBranch()
                        != climateCandidates.monthBranch()

                        || structure.monthBranch()
                        != pathology.monthBranch()
        ) {

            throw new IllegalArgumentException(
                    "격국·조후·병약 분석의 월지가 서로 일치하지 않습니다."
            );
        }

        if (
                structure.completeChart()
                        != climateCandidates.completeChart()

                        || structure.completeChart()
                        != pathology.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "격국·조후·병약 분석의 원국 완전성 상태가 일치하지 않습니다."
            );
        }
    }
}