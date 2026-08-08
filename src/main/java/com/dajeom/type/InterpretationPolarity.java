package com.dajeom.type;

/**
 * 해석 신호가 가리키는 기본 방향.
 *
 * SUPPORTIVE:
 * 해당 분야에서 도움으로 활용될 가능성이 있는 신호
 *
 * CAUTION:
 * 변화, 부담, 관리가 필요한 신호
 *
 * MIXED:
 * 도움과 부담 요소가 동시에 존재
 *
 * NEUTRAL:
 * 성향이나 구조에 대한 정보이며
 * 길흉 방향을 부여하지 않는 신호
 *
 * 이 값은 미래의 사건을 확정하는
 * 확률이나 예언값이 아니다.
 */
public enum InterpretationPolarity {

    SUPPORTIVE(
            "지원"
    ),

    CAUTION(
            "주의"
    ),

    MIXED(
            "복합"
    ),

    NEUTRAL(
            "중립"
    );

    private final String koreanName;

    InterpretationPolarity(
            String koreanName
    ) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}