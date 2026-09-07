package com.dajeom.domain;

/**
 * 천간과 지장간 사이에서 확인되는 연결 종류.
 *
 * EXACT_STEM:
 * 甲 ↔ 甲처럼 동일 천간 글자.
 *
 * SAME_ELEMENT_OTHER_STEM:
 * 甲 ↔ 乙처럼 오행은 같지만
 * 천간 글자는 다른 경우.
 *
 * 둘을 같은 의미의 통근으로 합치지 않는다.
 */
public enum StemGroundRelationType {

    EXACT_STEM,

    SAME_ELEMENT_OTHER_STEM
}