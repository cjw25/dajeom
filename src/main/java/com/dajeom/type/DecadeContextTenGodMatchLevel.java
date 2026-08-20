package com.dajeom.type;

/**
 * 원국 CrossFunctional Pattern의
 * involvedTenGodTypes와
 *
 * 13-6 DecadeLuckContextSignal의
 * tenGodTypes가 어느 정도 구조적으로
 * 일치하는지를 나타낸다.
 *
 * =================================================
 *
 * NONE
 *
 * 공통 십성이 없음.
 *
 * =================================================
 *
 * OVERLAP
 *
 * 일부 십성만 공통.
 *
 * 예:
 *
 * 원국 Pattern:
 * [食神, 七殺]
 *
 * Context:
 * [七殺]
 *
 * =================================================
 *
 * EXACT
 *
 * 양쪽 십성 Set이 정확히 동일.
 *
 * =================================================
 *
 * 중요:
 *
 * EXACT는:
 *
 * - 독립 Evidence 추가
 * - Pattern 강화
 * - Pattern 승격
 *
 * 을 뜻하지 않는다.
 *
 * 동일 구조를 가리키는 Context가
 * 구조 필드 수준에서 확인되었다는
 * 의미만 가진다.
 */
public enum DecadeContextTenGodMatchLevel {

    NONE,

    OVERLAP,

    EXACT;

    public boolean matches() {

        return this != NONE;
    }

    public boolean overlap() {

        return this == OVERLAP;
    }

    public boolean exact() {

        return this == EXACT;
    }
}