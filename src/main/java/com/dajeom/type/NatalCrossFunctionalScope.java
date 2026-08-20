package com.dajeom.type;

/**
 * 원국 교차 기능 Pattern이
 * 어떤 범위에서 작동하는지를 나타낸다.
 *
 * =================================================
 *
 * BETWEEN_FUNCTIONS
 *
 * 서로 다른 십성 Family 기능축 사이의 작동.
 *
 * 예:
 *
 * 식상 ↔ 관살
 * 재성 → 관살
 * 관살 → 인성
 * 비겁 ↔ 재성
 *
 * =================================================
 *
 * FUNCTION_TO_CORE_SELF
 *
 * 하나의 십성 Family 기능과
 * 일간 자기기반 사이의 작동.
 *
 * 예:
 *
 * 재다신약
 *
 * 재성 기능
 * ↔
 * 일간의 수용 기반
 *
 * =================================================
 *
 * WITHIN_FUNCTION
 *
 * 하나의 Family 내부에서 발생하는
 * 혼재·긴장 구조.
 *
 * 예:
 *
 * 관살혼재
 */
public enum NatalCrossFunctionalScope {

    BETWEEN_FUNCTIONS,

    FUNCTION_TO_CORE_SELF,

    WITHIN_FUNCTION
}