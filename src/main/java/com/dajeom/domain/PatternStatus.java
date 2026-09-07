package com.dajeom.domain;

/**
 * 격국의 공통 성패 상태.
 *
 * =================================================
 *
 * FORMED
 * → 成格
 *
 * BROKEN
 * → 破格
 *
 * FORMED_WITH_DEFECT
 * → 成中有敗
 *
 * BROKEN_WITH_RESCUE
 * → 敗中有救
 *
 * REVIEW_REQUIRED
 * → 조건이 혼재하거나 현재 구조만으로
 *   단정하면 안 되는 경우
 *
 * =================================================
 *
 * 점수 / 퍼센트 / confidence 없음.
 */
public enum PatternStatus {

    FORMED,

    BROKEN,

    FORMED_WITH_DEFECT,

    BROKEN_WITH_RESCUE,

    REVIEW_REQUIRED
}