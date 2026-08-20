package com.dajeom.type;

/**
 * Personality Finding Synthesis의
 * 구조적 결합 종류.
 *
 * =================================================
 *
 * 이것은 심리 유형이 아니다.
 *
 * 기존 명리 Pattern끼리
 * 어떤 구조로 연결되어 있는지를
 * 분류하기 위한 값이다.
 *
 * =================================================
 *
 * TENSION_WITH_REGULATION
 *
 * 한 기능 간 긴장/충돌이 존재하면서
 * 다른 Pattern이 그 기능을
 * 조절하는 구조.
 *
 * =================================================
 *
 * FUNCTIONAL_CHAIN
 *
 * A → B
 * B → C
 *
 * 형태로 기존 Pattern들이
 * 하나의 연쇄를 구성하는 구조.
 *
 * =================================================
 *
 * FUNCTIONAL_CONVERGENCE
 *
 * 서로 다른 기능이
 * 같은 기능축에 작용하는 구조.
 *
 * =================================================
 *
 * MIXED_STRUCTURE_WITH_PROCESSING
 *
 * 혼재 구조와
 * 처리/인성 작용이 같이 존재하는 구조.
 *
 * =================================================
 *
 * COMPOUND_PRESSURE
 *
 * 동일 기능영역에
 * 서로 다른 부담/경쟁 Pattern이
 * 함께 존재하는 구조.
 *
 * =================================================
 *
 * 주의:
 *
 * 어느 Kind도
 * 길흉이나 성격의 좋고 나쁨을 뜻하지 않는다.
 */
public enum PersonalityFindingSynthesisKind {

    TENSION_WITH_REGULATION,

    FUNCTIONAL_CHAIN,

    FUNCTIONAL_CONVERGENCE,

    MIXED_STRUCTURE_WITH_PROCESSING,

    COMPOUND_PRESSURE
}