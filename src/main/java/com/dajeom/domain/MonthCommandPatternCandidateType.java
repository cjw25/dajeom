package com.dajeom.domain;

/**
 * 월령을 기준으로 추출한 격국 후보 종류.
 *
 * 아직 격국 확정이 아니다.
 *
 * 正官格 / 七殺格 / 正財格 / 偏財格
 * 食神格 / 傷官格 / 正印格 / 偏印格
 *
 * 을 후보로 보존한다.
 *
 * 월령 MAIN이 비견/겁재인 경우는
 * 일반 8격으로 억지 분류하지 않고
 * 建祿 / 月劫 계열 검토 대상으로 따로 둔다.
 */
public enum MonthCommandPatternCandidateType {

    /**
     * 正官格 후보.
     */
    DIRECT_OFFICER,

    /**
     * 七殺格 후보.
     */
    SEVEN_KILLINGS,

    /**
     * 正財格 후보.
     */
    DIRECT_WEALTH,

    /**
     * 偏財格 후보.
     */
    INDIRECT_WEALTH,

    /**
     * 食神格 후보.
     */
    EATING_GOD,

    /**
     * 傷官格 후보.
     */
    HURTING_OFFICER,

    /**
     * 正印格 후보.
     */
    DIRECT_RESOURCE,

    /**
     * 偏印格 후보.
     */
    INDIRECT_RESOURCE,

    /**
     * 월령 MAIN이 비견.
     *
     * 일반 8격으로 확정하지 않고
     * 建祿 계열 별도 검토.
     */
    MONTH_COMPANION,

    /**
     * 월령 MAIN이 겁재.
     *
     * 일반 8격으로 확정하지 않고
     * 月劫 계열 별도 검토.
     */
    MONTH_ROB_WEALTH;

    public boolean standardEightPatternCandidate() {

        return switch (this) {

            case DIRECT_OFFICER,
                 SEVEN_KILLINGS,
                 DIRECT_WEALTH,
                 INDIRECT_WEALTH,
                 EATING_GOD,
                 HURTING_OFFICER,
                 DIRECT_RESOURCE,
                 INDIRECT_RESOURCE ->
                    true;

            case MONTH_COMPANION,
                 MONTH_ROB_WEALTH ->
                    false;
        };
    }

    public boolean companionMonthCommand() {

        return this
                == MONTH_COMPANION

                ||

                this
                        == MONTH_ROB_WEALTH;
    }
}