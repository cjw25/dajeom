package com.dajeom.type;

/**
 * 카테고리 전용 deterministic 판단이
 * 어느 시간층에서 생성되었는지를 나타낸다.
 *
 * =================================================
 *
 * NATAL
 *
 * 사주팔자 원국 자체에서 계산되는
 * 카테고리별 기본 판단.
 *
 * =================================================
 *
 * CURRENT_TIMELINE
 *
 * 현재 기준 시점의
 * 대운 / 세운 / 월운 Timeline Segment에서
 * 추가로 계산되는 카테고리 판단.
 *
 * =================================================
 *
 * TIMELINE_YEAR
 *
 * PAST_LUCK / FUTURE_LUCK에서 사용하는
 * 특정 명리연도의 카테고리별 판단.
 */
public enum ConsultationCategoryDeterministicLayer {

    NATAL,

    CURRENT_TIMELINE,

    TIMELINE_YEAR
}