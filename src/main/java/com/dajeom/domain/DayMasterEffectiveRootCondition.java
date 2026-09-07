package com.dajeom.domain;

/**
 * 일간의 기존 통근 지점에
 * ACCEPTED 합화 작용이 연결되었을 때,
 * 그 합화 오행과 일간의 관계를 분류한다.
 *
 * 중요:
 *
 * 이 enum은
 *
 * - 근이 강하다
 * - 근이 약하다
 * - 근이 무효다
 * - 근이 제거됐다
 *
 * 를 의미하지 않는다.
 *
 * 기존 통근은 항상 원국 사실로 보존한다.
 */
public enum DayMasterEffectiveRootCondition {

    /**
     * ACCEPTED 합화 작용이 없음.
     *
     * 원래 통근 Context만 존재.
     */
    ORIGINAL_ROOT_CONTEXT,

    /**
     * 합화 작용 오행이 일간을 생함.
     *
     * 예:
     * 丙火 일간
     * 합화 작용 木
     *
     * 木生火
     */
    TRANSFORMATION_GENERATES_DAY_MASTER,

    /**
     * 합화 작용 오행이
     * 일간과 같은 오행.
     *
     * 예:
     * 甲木 일간
     * 합화 작용 木
     */
    TRANSFORMATION_SAME_ELEMENT,

    /**
     * 일간이 합화 작용 오행을 생함.
     *
     * 예:
     * 丁火 일간
     * 합화 작용 土
     *
     * 火生土
     */
    TRANSFORMATION_GENERATED_BY_DAY_MASTER,

    /**
     * 합화 작용 오행이 일간을 극함.
     *
     * 예:
     * 甲木 일간
     * 합화 작용 金
     *
     * 金克木
     */
    TRANSFORMATION_CONTROLS_DAY_MASTER,

    /**
     * 일간이 합화 작용 오행을 극함.
     *
     * 예:
     * 丁火 일간
     * 합화 작용 金
     *
     * 火克金
     */
    TRANSFORMATION_CONTROLLED_BY_DAY_MASTER
}