package com.dajeom.domain;

/**
 * 일반 왕쇠 판정이 어떤 명리 조건을 통해
 * 결정되었는지 나타내는 규칙 경로.
 *
 * 점수나 confidence가 아니다.
 */
public enum DayMasterStrengthDecisionBasis {

    /**
     * 종격 등 특수구조를 먼저 검토해야 함.
     */
    SPECIAL_STRUCTURE_PRECEDES_GENERAL_STRENGTH,

    /**
     * 旺/相
     * +
     * 비교적 온전한 근
     * +
     * 생부
     * +
     * 설기/재/관살 방향이 없음
     */
    SUPPORTIVE_SEASON_WITH_ROOT_AND_SUPPORT,

    /**
     * 旺/相이며
     * 근 또는 생부가 존재하지만
     * CLEARLY_STRONG 조건 전체에는 미달.
     */
    SUPPORTIVE_SEASON_WITH_PARTIAL_FOUNDATION,

    /**
     * 休/囚/死
     * +
     * 무근
     * +
     * 생부 없음
     */
    NON_SUPPORTIVE_SEASON_WITHOUT_ROOT_OR_SUPPORT,

    /**
     * 休/囚/死
     * +
     * 무근
     * +
     * 생부는 남아 있음
     */
    NON_SUPPORTIVE_SEASON_WITHOUT_ROOT_BUT_WITH_SUPPORT,

    /**
     * 休/囚/死이며
     * 근이 존재하지만 근 Context가 불안정하고
     * 생부도 없으며 반대 방향 구조가 존재.
     */
    NON_SUPPORTIVE_SEASON_WITH_VULNERABLE_ROOT,

    /**
     * 여러 명리 조건이 서로 엇갈려
     * 일반 규칙만으로 명백한 강/약을
     * 확정하지 않은 경우.
     */
    MIXED_STRUCTURE
}