package com.dajeom.type;

/**
 * Annual CrossFunctional Pattern과
 * 14-6 AnnualLuckContextSignal 사이의
 * 구조적 십성 Match 수준.
 *
 * =================================================
 *
 * NONE
 *
 * 겹치는 StructureTenGodType이 없음.
 *
 * =================================================
 *
 * OVERLAP
 *
 * 하나 이상 겹치지만
 * Pattern의 involvedTenGodTypes와
 * Context Signal의 tenGodTypes가
 * 정확히 동일하지 않음.
 *
 * =================================================
 *
 * EXACT
 *
 * 두 Set이 정확히 동일.
 *
 * =================================================
 *
 * 중요:
 *
 * EXACT는 독립 Evidence 추가가 아니다.
 *
 * Functional Layer에서 확인된 구조가
 * Context Layer에서도 같은 십성 구성으로
 * 다시 나타난다는 뜻만 가진다.
 */
public enum AnnualContextTenGodMatchLevel {

    NONE,

    OVERLAP,

    EXACT;

    public boolean matched() {

        return this != NONE;
    }

    public boolean exact() {

        return this == EXACT;
    }

    public boolean overlap() {

        return this == OVERLAP;
    }
}