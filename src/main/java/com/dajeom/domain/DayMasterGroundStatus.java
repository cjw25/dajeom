package com.dajeom.domain;

/**
 * 일간의 지지 뿌리 구조 상태.
 *
 * 흔히 得地라고 부르는 영역을
 * 단순 true / false로 줄이지 않고
 * 실제 원국 Context에 따라 분류한다.
 *
 * 이 enum 자체는:
 *
 * - 신강
 * - 신약
 * - 중화
 *
 * 를 의미하지 않는다.
 */
public enum DayMasterGroundStatus {

    /**
     * 동일 천간 근도 없고
     * 같은 오행의 다른 천간 연결도 없음.
     */
    NO_ROOT,

    /**
     * 근이 존재하고,
     * 현재 확인된 충/형/파/해나
     * ACCEPTED 합화 작용이 직접 걸리지 않음.
     */
    ROOT_PRESENT,

    /**
     * 근은 존재하지만
     * 충/형/파/해 등의 관계 Context가 존재.
     *
     * 근이 제거됐다는 뜻은 아니다.
     */
    ROOT_PRESENT_WITH_DISTURBANCE,

    /**
     * 근이 있는 지지에 ACCEPTED 합화가 있고,
     * 그 합화 작용이 일간을 생하거나
     * 일간과 같은 오행으로 작용.
     *
     * 이것만으로 "근이 강화됐다"고
     * 최종 판정하지 않는다.
     */
    ROOT_PRESENT_WITH_SUPPORTIVE_TRANSFORMATION,

    /**
     * 근이 있는 지지에 ACCEPTED 합화가 있고,
     * 합화 작용이 일간을 생하는 쪽이 아님.
     *
     * 실제 관계는 각 Root Context에
     * GENERATED_BY_DAY_MASTER,
     * CONTROLS_DAY_MASTER,
     * CONTROLLED_BY_DAY_MASTER
     *
     * 중 하나로 그대로 남는다.
     */
    ROOT_PRESENT_WITH_NON_SUPPORTIVE_TRANSFORMATION,

    /**
     * 원국의 여러 근에서:
     *
     * - 정상 근
     * - 관계 방해
     * - 생조 합화
     * - 비생조 합화
     *
     * 등이 서로 섞여 존재.
     *
     * 하나의 단순 상태로 축약하면
     * 명리 정보가 손실되는 구조.
     */
    MIXED_ROOT_CONTEXT
}