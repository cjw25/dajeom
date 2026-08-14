package com.dajeom.type;

/**
 * 정밀화 5차-8.
 *
 * 일반 팔격과 별도로 검토해야 하는
 * 특수 격국 후보 유형.
 *
 * 주의:
 *
 * 모든 값은 최종 격국 확정이 아니라
 * "후보" 또는 "특수 검토 필요" 상태다.
 *
 * 실제 주격/보조격 결정은 5-9,
 * 5차 전체 확정은 5-10에서 진행한다.
 */
public enum SpecialStructureType {

    /**
     * 일간의 건록 지지가
     * 월지에 정확히 위치한 경우.
     */
    JIAN_LU_CANDIDATE(
            "건록 후보",
            "월지가 일간의 건록 위치와 일치합니다.",
            true,
            false
    ),

    /**
     * 다점 기본 정책에서는
     * 양간의 양인 월지만 대상으로 한다.
     */
    YANG_REN_CANDIDATE(
            "양인 후보",
            "월지가 일간의 양인 위치와 일치합니다.",
            true,
            false
    ),

    /**
     * 비견·겁재 월령이지만
     * 현재 기본 정책의 건록/양인으로
     * 바로 분류되지 않는 경우.
     *
     * 일반 비견격/겁재격으로 확정하지 않고
     * 특수구조 검토 대상으로 보존한다.
     */
    COMPANION_MONTH_REVIEW(
            "비겁 월령 특수검토",
            "비견·겁재 성격의 월령이므로 일반 내격과 분리해 검토합니다.",
            true,
            false
    ),

    /**
     * 4-7에서 올라온 종강 후보.
     */
    FOLLOW_STRONG_CANDIDATE(
            "종강 후보",
            "일간의 극강 구조가 매우 강하여 종강 가능성을 추가 검토합니다.",
            false,
            true
    ),

    /**
     * 종약 가운데 식상 방향이
     * 지배적으로 나타나는 후보.
     */
    FOLLOW_OUTPUT_CANDIDATE(
            "종아 계열 후보",
            "극약 구조에서 식상 방향의 세력이 주도적으로 나타납니다.",
            false,
            true
    ),

    /**
     * 종약 가운데 재성 방향이
     * 지배적으로 나타나는 후보.
     */
    FOLLOW_WEALTH_CANDIDATE(
            "종재 계열 후보",
            "극약 구조에서 재성 방향의 세력이 주도적으로 나타납니다.",
            false,
            true
    ),

    /**
     * 종약 가운데 관살 방향이
     * 지배적으로 나타나는 후보.
     */
    FOLLOW_OFFICER_CANDIDATE(
            "종관살 계열 후보",
            "극약 구조에서 관살 방향의 세력이 주도적으로 나타납니다.",
            false,
            true
    ),

    /**
     * 종약 조건은 있으나
     * 식상·재성·관살 가운데
     * 하나가 명확히 독점하지 않는 구조.
     */
    FOLLOW_WEAK_MIXED_CANDIDATE(
            "혼합 종약 후보",
            "극약 구조이지만 하나의 종약 방향으로 단정하기 어려운 혼합 상태입니다.",
            false,
            true
    );

    private final String koreanName;

    private final String description;

    private final boolean monthStructureBased;

    private final boolean extremeStrengthBased;

    SpecialStructureType(
            String koreanName,
            String description,
            boolean monthStructureBased,
            boolean extremeStrengthBased
    ) {

        this.koreanName =
                koreanName;

        this.description =
                description;

        this.monthStructureBased =
                monthStructureBased;

        this.extremeStrengthBased =
                extremeStrengthBased;
    }

    public String koreanName() {

        return koreanName;
    }

    public String description() {

        return description;
    }

    public boolean monthStructureBased() {

        return monthStructureBased;
    }

    public boolean extremeStrengthBased() {

        return extremeStrengthBased;
    }

    public boolean jianLu() {

        return this == JIAN_LU_CANDIDATE;
    }

    public boolean yangRen() {

        return this == YANG_REN_CANDIDATE;
    }

    public boolean companionMonthReview() {

        return this == COMPANION_MONTH_REVIEW;
    }

    public boolean followStrong() {

        return this == FOLLOW_STRONG_CANDIDATE;
    }

    public boolean followWeak() {

        return this == FOLLOW_OUTPUT_CANDIDATE
                || this == FOLLOW_WEALTH_CANDIDATE
                || this == FOLLOW_OFFICER_CANDIDATE
                || this == FOLLOW_WEAK_MIXED_CANDIDATE;
    }

    public boolean followOutput() {

        return this == FOLLOW_OUTPUT_CANDIDATE;
    }

    public boolean followWealth() {

        return this == FOLLOW_WEALTH_CANDIDATE;
    }

    public boolean followOfficer() {

        return this == FOLLOW_OFFICER_CANDIDATE;
    }

    public boolean followWeakMixed() {

        return this == FOLLOW_WEAK_MIXED_CANDIDATE;
    }

    public boolean followStructure() {

        return followStrong()
                || followWeak();
    }
}