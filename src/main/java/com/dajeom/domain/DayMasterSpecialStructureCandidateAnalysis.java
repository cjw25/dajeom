package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 일반 왕쇠 판단 전에
 * 특수구조 검토 필요 여부를 정리한다.
 *
 * =================================================
 *
 * 이 객체가 보는 것:
 *
 * - 월령 旺相休囚死
 * - 得地 영역의 실제 근 존재
 * - 천간/지장간 생부 구조
 * - 설기 구조
 * - 재성 방향
 * - 관살 방향
 *
 * =================================================
 *
 * 아직 보지 않는 것:
 *
 * - 최종 종격 성립
 * - 격국의 성패
 * - 용신
 * - 운의 영향
 *
 * =================================================
 *
 * 점수 / 가중치 / 퍼센트 없음.
 */
public record DayMasterSpecialStructureCandidateAnalysis(

        HeavenlyStem dayMaster,

        FiveElementSeasonState seasonState,

        DayMasterGroundStatus groundStatus,

        Set<DayMasterSpecialStructureCandidateType> candidates,

        boolean rootAbsent,

        boolean generatingSupportPresent,

        boolean sameElementSupportPresent,

        boolean outputStructurePresent,

        boolean wealthStructurePresent,

        boolean controlStructurePresent

) {

    public DayMasterSpecialStructureCandidateAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                seasonState,
                "월령 상태는 필수입니다."
        );

        Objects.requireNonNull(
                groundStatus,
                "득지 구조 상태는 필수입니다."
        );

        candidates =
                candidates == null
                        ? Set.of()
                        : Set.copyOf(
                        candidates
                );

        if (candidates.isEmpty()) {

            throw new IllegalArgumentException(
                    "최소 하나의 검토 경로가 필요합니다."
            );
        }
    }

    /**
     * 일간을 생하는 인성 방향이나
     * 같은 오행인 비겁 방향이
     * 하나라도 존재하는가.
     */
    public boolean hasAnySupport() {

        return generatingSupportPresent
                ||
                sameElementSupportPresent;
    }

    /**
     * 일간에서 빠져나가거나
     * 일간이 상대하거나
     * 일간을 극하는 방향이
     * 하나라도 존재하는가.
     */
    public boolean hasAnyOpposingDirection() {

        return outputStructurePresent
                ||
                wealthStructurePresent
                ||
                controlStructurePresent;
    }

    /**
     * 진종의 가장 기본적인 검토 문턱.
     *
     * 무근
     * +
     * 생부 없음
     *
     * 까지만 확인한다.
     *
     * 이것이 true라고
     * 從格이 성립한 것은 아니다.
     */
    public boolean pureFollowingBaseContext() {

        return rootAbsent
                &&
                !hasAnySupport();
    }

    /**
     * 무근이지만 인성 또는 비겁의
     * 생부가 남아 있는 경우.
     *
     * 진종으로 바로 넘기면 안 되는 Context.
     */
    public boolean followingBlockedBySupport() {

        return rootAbsent
                &&
                hasAnySupport();
    }

    public boolean requiresGeneralStrengthReview() {

        return candidates.contains(
                DayMasterSpecialStructureCandidateType
                        .GENERAL_STRENGTH_REVIEW
        );
    }

    public boolean requiresSpecialStructureReview() {

        return candidates
                .stream()
                .anyMatch(
                        type ->
                                type
                                        != DayMasterSpecialStructureCandidateType
                                        .GENERAL_STRENGTH_REVIEW
                );
    }

    public boolean hasCandidate(
            DayMasterSpecialStructureCandidateType type
    ) {

        Objects.requireNonNull(
                type,
                "후보 종류는 필수입니다."
        );

        return candidates.contains(
                type
        );
    }
}