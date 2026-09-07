package com.dajeom.domain;

import java.util.Objects;
import java.util.Set;

/**
 * 일반 일간 왕쇠 분석 결과.
 *
 * =================================================
 *
 * 이 객체는 최종 용신 판단이 아니다.
 *
 * 또한:
 *
 * - 격국
 * - 조후
 * - 화기격
 * - 용신
 * - 희신
 * - 기신
 *
 * 을 결정하지 않는다.
 *
 * =================================================
 *
 * 저장하는 것:
 *
 * - 최종 일반 왕쇠 상태
 * - 어떤 규칙 경로로 판단했는가
 * - 월령 상태
 * - 得地 구조
 * - 천간 생조 형태
 * - 생부 존재 여부
 * - 식상/재성/관살 방향 존재 여부
 * - 특수구조 후보
 *
 * =================================================
 *
 * 숫자 점수 / 퍼센트 / 가중치 없음.
 */
public record DayMasterStrengthAnalysis(

        HeavenlyStem dayMaster,

        DayMasterStrengthStatus status,

        DayMasterStrengthDecisionBasis decisionBasis,

        FiveElementSeasonState seasonState,

        DayMasterGroundStatus groundStatus,

        DayMasterForceSupportPattern visibleSupportPattern,

        boolean generatingSupportPresent,

        boolean sameElementSupportPresent,

        boolean outputStructurePresent,

        boolean wealthStructurePresent,

        boolean controlStructurePresent,

        Set<DayMasterSpecialStructureCandidateType>
        specialStructureCandidates

) {

    public DayMasterStrengthAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "일간은 필수입니다."
        );

        Objects.requireNonNull(
                status,
                "왕쇠 상태는 필수입니다."
        );

        Objects.requireNonNull(
                decisionBasis,
                "왕쇠 판정 근거 경로는 필수입니다."
        );

        Objects.requireNonNull(
                seasonState,
                "월령 상태는 필수입니다."
        );

        Objects.requireNonNull(
                groundStatus,
                "득지 구조 상태는 필수입니다."
        );

        Objects.requireNonNull(
                visibleSupportPattern,
                "천간 생조 형태는 필수입니다."
        );

        specialStructureCandidates =
                specialStructureCandidates == null
                        ? Set.of()
                        : Set.copyOf(
                        specialStructureCandidates
                );
    }

    /*
     * =================================================
     * 월령
     * =================================================
     */

    public boolean seasonSupportsDayMaster() {

        return seasonState
                == FiveElementSeasonState.WANG

                ||

                seasonState
                        == FiveElementSeasonState.XIANG;
    }

    public boolean seasonDoesNotSupportDayMaster() {

        return !seasonSupportsDayMaster();
    }

    /*
     * =================================================
     * 得地
     * =================================================
     */

    public boolean rootAbsent() {

        return groundStatus
                == DayMasterGroundStatus.NO_ROOT;
    }

    public boolean rootPresent() {

        return !rootAbsent();
    }

    /*
     * =================================================
     * 생부
     * =================================================
     */

    public boolean hasAnySupport() {

        return generatingSupportPresent
                ||
                sameElementSupportPresent;
    }

    /*
     * =================================================
     * 설기 / 재 / 관살
     * =================================================
     */

    public boolean hasAnyOpposingDirection() {

        return outputStructurePresent
                ||
                wealthStructurePresent
                ||
                controlStructurePresent;
    }

    /*
     * =================================================
     * 판정 결과
     * =================================================
     */

    public boolean requiresSpecialStructureReview() {

        return status
                == DayMasterStrengthStatus
                .SPECIAL_STRUCTURE_REVIEW;
    }

    public boolean ordinaryStrengthResolved() {

        return !requiresSpecialStructureReview();
    }

    public boolean clearlyWeak() {

        return status
                == DayMasterStrengthStatus.CLEARLY_WEAK;
    }

    public boolean weakLeaning() {

        return status
                == DayMasterStrengthStatus.WEAK_LEANING;
    }

    public boolean mixed() {

        return status
                == DayMasterStrengthStatus
                .MIXED_NEEDS_REVIEW;
    }

    public boolean strongLeaning() {

        return status
                == DayMasterStrengthStatus.STRONG_LEANING;
    }

    public boolean clearlyStrong() {

        return status
                == DayMasterStrengthStatus.CLEARLY_STRONG;
    }
}