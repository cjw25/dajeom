package com.dajeom.domain;

/**
 * 명리 해석의 계층.
 *
 * 숫자 가중치가 아니라
 * 해석 순서를 구분하기 위한 것이다.
 */
public enum InterpretationLayer {

    /**
     * 월령, 일간, 계절 관계 등
     * 원국의 가장 기본적인 구조.
     */
    CORE_STRUCTURE,

    /**
     * 통근, 투간, 투출 등
     * 기운이 실제로 뿌리와 연결되는 구조.
     */
    ROOT_AND_EXPOSURE,

    /**
     * 십성이 어느 위치에 있고
     * 무엇이 겉으로 드러나는지.
     */
    TEN_GOD_STRUCTURE,

    /**
     * 천간합충,
     * 지지 합충형파해,
     * 삼합/방합 등.
     */
    RELATION_STRUCTURE,

    /**
     * 대운, 세운, 월운이
     * 원국에 만들어내는 변화.
     */
    TIME_FLOW,

    /**
     * 귀인, 신살 등
     * 본체 해석을 보조하는 요소.
     */
    SECONDARY_MARKER
}