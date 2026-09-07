package com.dajeom.domain;

/**
 * 일간을 돕는 천간 구조의 형태.
 *
 * 得勢 판단에 필요한 원형 자료다.
 *
 * 숫자, 개수 점수, 강약 점수는 없다.
 *
 * 여기서는 천간에 실제로 드러난:
 *
 * - 일간을 생하는 오행
 * - 일간과 같은 오행
 *
 * 의 존재 형태만 분류한다.
 */
public enum DayMasterVisibleSupportPattern {

    /**
     * 천간에 일간을 생하거나
     * 같은 오행인 글자가 없음.
     */
    NO_VISIBLE_SUPPORT,

    /**
     * 일간을 생하는 천간은 있으나
     * 같은 오행 천간은 없음.
     */
    GENERATING_ONLY,

    /**
     * 같은 오행 천간은 있으나
     * 일간을 생하는 천간은 없음.
     */
    SAME_ELEMENT_ONLY,

    /**
     * 일간을 생하는 천간과
     * 같은 오행 천간이 모두 존재.
     */
    GENERATING_AND_SAME_ELEMENT
}