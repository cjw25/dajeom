package com.dajeom.type;

/**
 * 정밀화 18차-1.
 *
 * 생활영역 Rule이 Evidence를 읽은 결과.
 *
 * =================================================
 *
 * SUPPORTIVE
 *
 * 해당 생활영역을 뒷받침하는 구조.
 *
 * =================================================
 *
 * PRESSURE
 *
 * 해당 생활영역에서 부담·소모·충돌 등
 * 주의할 Context.
 *
 * =================================================
 *
 * MIXED
 *
 * 지원 요소와 부담 요소가 동시에 존재.
 *
 * =================================================
 *
 * CONTEXTUAL
 *
 * 방향성을 단독 결정하지 않고
 * 다른 구조의 해석을 보완하는 Evidence.
 *
 * =================================================
 *
 * 이것은:
 *
 * 길 / 흉
 * 성공 / 실패
 * 사건발생 확률
 *
 * 이 아니다.
 */
public enum LifeDomainSignalStatus {

    SUPPORTIVE(
            "지원"
    ),

    PRESSURE(
            "부담"
    ),

    MIXED(
            "혼재"
    ),

    CONTEXTUAL(
            "맥락"
    );

    private final String koreanName;

    LifeDomainSignalStatus(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String koreanName() {
        return koreanName;
    }
}