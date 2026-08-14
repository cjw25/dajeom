package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.SpecialStructureType;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 5차-8.
 *
 * 특수 월령 구조와
 * 4-7 종격 후보를 하나의 분석 결과로
 * 연결한다.
 *
 * 주의:
 *
 * evidences에 여러 후보가 동시에
 * 존재할 수 있다.
 *
 * 예:
 *
 * 건록 후보
 * +
 * 종강 후보
 *
 * 또는
 *
 * 양인 후보
 * +
 * 종강 후보
 *
 * 등이 동시에 존재할 수 있다.
 *
 * 여기서 하나를 강제로 제거하지 않는다.
 *
 * 최종 주격 결정은 5-9에서 진행한다.
 */
public record SpecialStructureAnalysis(

        HeavenlyStem dayMaster,

        EarthlyBranch monthBranch,

        boolean mainQiSpecialDeferred,

        int meaningfulStandardCandidateCount,

        List<SpecialStructureEvidence> evidences

) {

    public SpecialStructureAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                monthBranch,
                "월지가 필요합니다."
        );

        if (
                meaningfulStandardCandidateCount < 0
        ) {

            throw new IllegalArgumentException(
                    "일반 내격 후보 수는 음수가 될 수 없습니다."
            );
        }

        evidences =
                evidences == null
                        ? List.of()
                        : List.copyOf(
                        evidences
                );

        boolean duplicatedType =
                evidences
                        .stream()
                        .map(
                                SpecialStructureEvidence
                                        ::type
                        )
                        .distinct()
                        .count()
                        != evidences.size();

        if (
                duplicatedType
        ) {

            throw new IllegalArgumentException(
                    "동일한 특수구조 후보가 중복되었습니다."
            );
        }

        for (
                SpecialStructureEvidence evidence
                : evidences
        ) {

            if (
                    evidence.dayMaster()
                            != dayMaster
            ) {

                throw new IllegalArgumentException(
                        "특수구조 Evidence의 일간이 서로 일치하지 않습니다."
                );
            }

            if (
                    evidence.monthBranch()
                            != monthBranch
            ) {

                throw new IllegalArgumentException(
                        "특수구조 Evidence의 월지가 서로 일치하지 않습니다."
                );
            }

            if (
                    evidence.mainQiSpecialDeferred()
                            != mainQiSpecialDeferred
            ) {

                throw new IllegalArgumentException(
                        "월령 특수구조 보류 상태가 서로 일치하지 않습니다."
                );
            }

            if (
                    evidence.meaningfulStandardCandidateCount()
                            != meaningfulStandardCandidateCount
            ) {

                throw new IllegalArgumentException(
                        "일반 내격 후보 수가 서로 일치하지 않습니다."
                );
            }
        }
    }

    public boolean hasAnyCandidate() {

        return !evidences.isEmpty();
    }

    public boolean hasJianLuCandidate() {

        return hasType(
                SpecialStructureType
                        .JIAN_LU_CANDIDATE
        );
    }

    public boolean hasYangRenCandidate() {

        return hasType(
                SpecialStructureType
                        .YANG_REN_CANDIDATE
        );
    }

    public boolean hasCompanionMonthReview() {

        return hasType(
                SpecialStructureType
                        .COMPANION_MONTH_REVIEW
        );
    }

    public boolean hasFollowStrongCandidate() {

        return hasType(
                SpecialStructureType
                        .FOLLOW_STRONG_CANDIDATE
        );
    }

    public boolean hasFollowWeakCandidate() {

        return evidences
                .stream()
                .anyMatch(
                        evidence ->
                                evidence
                                        .type()
                                        .followWeak()
                );
    }

    public boolean hasFollowOutputCandidate() {

        return hasType(
                SpecialStructureType
                        .FOLLOW_OUTPUT_CANDIDATE
        );
    }

    public boolean hasFollowWealthCandidate() {

        return hasType(
                SpecialStructureType
                        .FOLLOW_WEALTH_CANDIDATE
        );
    }

    public boolean hasFollowOfficerCandidate() {

        return hasType(
                SpecialStructureType
                        .FOLLOW_OFFICER_CANDIDATE
        );
    }

    public boolean hasMixedFollowWeakCandidate() {

        return hasType(
                SpecialStructureType
                        .FOLLOW_WEAK_MIXED_CANDIDATE
        );
    }

    /**
     * 일반 내격과 특수구조가
     * 함께 경쟁하는 상태인가.
     */
    public boolean hasGeneralStructureCompetition() {

        return meaningfulStandardCandidateCount > 0
                && hasAnyCandidate();
    }

    public List<SpecialStructureEvidence>
    monthStructureEvidences() {

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence
                                        .type()
                                        .monthStructureBased()
                )
                .toList();
    }

    public List<SpecialStructureEvidence>
    followStructureEvidences() {

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence
                                        .type()
                                        .followStructure()
                )
                .toList();
    }

    public boolean requiresSpecialStructureReview() {

        return hasAnyCandidate();
    }

    public Optional<SpecialStructureEvidence>
    evidenceOf(
            SpecialStructureType type
    ) {

        if (
                type == null
        ) {

            return Optional.empty();
        }

        return evidences
                .stream()
                .filter(
                        evidence ->
                                evidence.type()
                                        == type
                )
                .findFirst();
    }

    private boolean hasType(
            SpecialStructureType type
    ) {

        return evidenceOf(
                type
        )
                .isPresent();
    }
}