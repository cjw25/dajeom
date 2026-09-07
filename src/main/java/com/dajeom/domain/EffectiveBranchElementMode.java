package com.dajeom.domain;

/**
 * 지지의 본래 오행과
 * 합화 작용 오행의 현재 상태.
 *
 * ORIGINAL_ONLY
 * - 인정된 합화가 없음
 *
 * TRANSFORMATION_EFFECT_ACTIVE
 * - 합화 ACCEPTED가 존재
 * - 본래 오행은 그대로 보존
 * - 작용층에 변환 오행을 추가
 *
 * CONFLICTING_ACCEPTED_TRANSFORMATIONS
 * - 서로 다른 합화 오행이 동시에 ACCEPTED된 비정상 상태
 *
 * 정상적인 경쟁 판정이 선행됐다면
 * 마지막 상태는 발생하지 않아야 한다.
 */
public enum EffectiveBranchElementMode {

    ORIGINAL_ONLY,

    TRANSFORMATION_EFFECT_ACTIVE,

    CONFLICTING_ACCEPTED_TRANSFORMATIONS
}