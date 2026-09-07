package com.dajeom.domain;

/**
 * 일간의 지지 뿌리 구성 형태.
 *
 * 신강/신약이나 득지 여부를 뜻하지 않는다.
 *
 * 단순히 현재 원국에서 확인된
 * 통근 형태를 분류한다.
 */
public enum DayMasterGroundPattern {

    /**
     * 동일 천간 근도 없고
     * 같은 오행의 다른 천간 연결도 없음.
     */
    NO_ROOT_CONNECTION,

    /**
     * 동일 천간은 없지만
     * 같은 오행의 다른 천간 연결은 존재.
     */
    SAME_ELEMENT_ONLY,

    /**
     * 동일 천간 근은 존재하지만
     * 같은 오행의 다른 천간 연결은 없음.
     */
    EXACT_STEM_ONLY,

    /**
     * 동일 천간 근과
     * 같은 오행의 다른 천간 연결이 모두 존재.
     */
    EXACT_AND_SAME_ELEMENT
}