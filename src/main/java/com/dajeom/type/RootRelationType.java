package com.dajeom.type;

/**
 * 천간이 지장간에 뿌리를 두는 형태.
 *
 * SAME_STEM
 * 천간과 지장간이 완전히 같은 경우.
 *
 * 예:
 * 甲 -> 寅 속 甲
 * 丁 -> 午 속 丁
 *
 * SAME_ELEMENT
 * 천간 글자는 다르지만 같은 오행의
 * 음양짝이 지장간에 존재하는 경우.
 *
 * 예:
 * 甲 -> 卯 속 乙
 * 丁 -> 巳 속 丙
 *
 * 다점에서는 두 경우를 동일하게 보지 않는다.
 *
 * SAME_STEM이 더 직접적인 통근이고,
 * SAME_ELEMENT는 동기(同氣)의 보조근으로 처리한다.
 */
public enum RootRelationType {

    SAME_STEM(
            "동간 통근",
            100
    ),

    SAME_ELEMENT(
            "동기 보조근",
            70
    );

    private final String koreanName;

    /**
     * 통근 관계 자체의 상대 가중치.
     *
     * 확률이 아니다.
     */
    private final int weightRate;

    RootRelationType(
            String koreanName,
            int weightRate
    ) {

        this.koreanName =
                koreanName;

        this.weightRate =
                weightRate;
    }

    public String koreanName() {

        return koreanName;
    }

    public int weightRate() {

        return weightRate;
    }

    public boolean direct() {

        return this == SAME_STEM;
    }

    public boolean supporting() {

        return this == SAME_ELEMENT;
    }
}