package com.dajeom.domain;

/**
 * 원국 최종 격국 판정 상태.
 *
 * 숫자 / 확률 / 점수 없음.
 */
public enum NatalPatternDecisionStatus {

    /**
     * 격이 성립.
     */
    FORMED,

    /**
     * 격은 성립하지만 명확한 결함이 함께 존재.
     */
    FORMED_WITH_DEFECT,

    /**
     * 격이 깨짐.
     */
    BROKEN,

    /**
     * 원래 파격 요인이 있으나
     * 구응 구조가 확인됨.
     */
    BROKEN_WITH_RESCUE,

    /**
     * 복수 후보, 爭合, 미확정 회합,
     * 추가 구조 조건 등 때문에
     * 현재 단계에서 확정하지 않음.
     */
    REVIEW_REQUIRED,

    /**
     * 建祿 / 月劫 / 陽刃 등에서
     * 사용할 성격 경로가 확인되지 않음.
     */
    UNFORMED
}