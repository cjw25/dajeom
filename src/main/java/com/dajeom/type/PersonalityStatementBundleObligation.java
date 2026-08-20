package com.dajeom.type;

/**
 * Personality Statement Bundle을
 * 최종 설명에서 어느 수준으로
 * 다뤄야 하는지 나타낸다.
 *
 * =================================================
 *
 * REQUIRED
 *
 * PRIMARY Selection이 존재하는 Bundle.
 *
 * 최종 기본성향 설명에서
 * 반드시 다뤄야 한다.
 *
 * =================================================
 *
 * OPTIONAL_SUPPORT
 *
 * PRIMARY는 없지만
 * 직접 설명 가능한 SUPPORTING Selection이
 * 존재하는 Bundle.
 *
 * 의미 있는 보조 설명으로 사용할 수 있지만
 * 핵심 성향처럼 과장하면 안 된다.
 *
 * =================================================
 *
 * 이것은 중요도 점수가 아니다.
 *
 * 기존 Selection Role을
 * Contract 수준에서 보존한 것이다.
 */
public enum PersonalityStatementBundleObligation {

    REQUIRED,

    OPTIONAL_SUPPORT
}