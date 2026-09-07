package com.dajeom.domain;

/**
 * 기타 귀인 / 신살이 어떤 규칙으로 성립했는지 보존한다.
 */
public enum SpecialStarReferenceType {

    /**
     * 일간 → 특정 지지.
     *
     * 예:
     * 문창귀인
     * 문곡귀인
     * 학당귀인
     * 천주귀인
     * 암록
     * 태극귀인
     * 복성귀인
     * 관귀학관
     * 금여
     * 비인살
     */
    DAY_STEM_BRANCH,

    /**
     * 해당 기둥의 간지 자체.
     *
     * 예:
     * 백호살
     */
    PILLAR_GANJI,

    /**
     * 해당 기둥의 천간 또는 지지 글자 자체.
     *
     * 예:
     * 현침살
     */
    PILLAR_CHARACTER,

    /**
     * 월지를 기준으로 특정 천간 또는 지지를 찾는다.
     *
     * 예:
     * 천덕귀인
     * 월덕귀인
     */
    MONTH_BRANCH_CHARACTER
}