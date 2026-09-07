package com.dajeom.domain;

/**
 * 일반 신강/신약 판정 전에
 * 별도 검토해야 하는 특수구조 후보.
 *
 * 중요:
 *
 * CANDIDATE는 성격 확정을 뜻하지 않는다.
 *
 * 예:
 *
 * FOLLOW_WEALTH_REVIEW
 *
 * != 從財格 확정
 *
 * 실제 성격 여부는 이후
 * 월령 / 통근 / 생부 / 투출 / 합화 /
 * 전체 기세를 다시 검토해서 결정한다.
 */
public enum DayMasterSpecialStructureCandidateType {

    /**
     * 특수구조보다 일반 왕쇠 판단으로
     * 우선 진행해야 하는 원국.
     */
    GENERAL_STRENGTH_REVIEW,

    /**
     * 일간이 무근하고 생부도 없으며
     * 식상/재성/관살 방향으로 기세가 흐르는
     * 약한 쪽의 從象 검토 대상.
     *
     * 아직 從格 확정이 아니다.
     */
    WEAK_FOLLOWING_REVIEW,

    /**
     * 從財 가능성 검토.
     */
    FOLLOW_WEALTH_REVIEW,

    /**
     * 從官 / 從殺 가능성 검토.
     *
     * 정관/칠살 세부 구분은
     * 기존 TenGodCalculator 결과와
     * 이후 연결한다.
     */
    FOLLOW_CONTROL_REVIEW,

    /**
     * 從兒 가능성 검토.
     *
     * 일간이 식상 방향으로
     * 기운을 내보내는 구조.
     */
    FOLLOW_OUTPUT_REVIEW,

    /**
     * 財 / 官殺 / 食傷이 함께 형성되어
     * 하나의 신만 단순히 따르기 어려운
     * 從勢 가능성 검토.
     */
    FOLLOW_MOMENTUM_REVIEW,

    /**
     * 비겁 중심으로 극왕한
     * 從旺 가능성 검토.
     */
    FOLLOW_PROSPEROUS_REVIEW,

    /**
     * 인성과 비겁이 함께 일간을
     * 극도로 생조하는 從強 가능성 검토.
     */
    FOLLOW_STRONG_REVIEW
}