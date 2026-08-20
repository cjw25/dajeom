package com.dajeom.type;

/**
 * Personality Statement Bundle의 종류.
 *
 * =================================================
 *
 * PRIMARY_CONNECTED
 *
 * 최소 하나의 PRIMARY Selection이 있고,
 * 같은 기능축으로 연결된
 * SUPPORTING Selection을 함께 묶은 Bundle.
 *
 * =================================================
 *
 * SUPPORTING_CONNECTED
 *
 * PRIMARY까지 올라간 Fact는 없지만
 * 직접 설명 가능한 SUPPORTING Fact들이
 * 존재하는 Bundle.
 *
 * =================================================
 *
 * 중요한 원칙:
 *
 * SUPPORTING_CONNECTED라고 해서
 * SUPPORTING을 PRIMARY로 승격시키는 것이 아니다.
 *
 * 기존 Selection Role은 그대로 유지한다.
 */
public enum PersonalityStatementBundleKind {

    PRIMARY_CONNECTED,

    SUPPORTING_CONNECTED
}