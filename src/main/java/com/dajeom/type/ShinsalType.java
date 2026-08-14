package com.dajeom.type;

/**
 * 정밀화 17차-1.
 *
 * 다점 deterministic 신살 분석에서
 * 식별하는 신살 종류.
 *
 * =================================================
 *
 * 17차 초기 지원 대상:
 *
 * 천을귀인
 * 문창귀인
 * 역마
 * 도화
 * 화개
 *
 * =================================================
 *
 * 실제 판정표는 이 Enum에 넣지 않는다.
 *
 * 각 신살의 계산법은
 * ShinsalRule 구현체가 담당한다.
 *
 * =================================================
 *
 * 신살은 보조 Evidence다.
 *
 * 따라서:
 *
 * 신살 존재 = 길
 * 신살 존재 = 흉
 *
 * 같은 판정을 이 타입에서 하지 않는다.
 */
public enum ShinsalType {

    TIAN_YI_NOBLEMAN(
            "천을귀인"
    ),

    WEN_CHANG_NOBLEMAN(
            "문창귀인"
    ),

    TRAVEL_HORSE(
            "역마"
    ),

    PEACH_BLOSSOM(
            "도화"
    ),

    CANOPY(
            "화개"
    );

    private final String koreanName;

    ShinsalType(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }
}