package com.dajeom.type;

/**
 * 정밀화 17차-6.
 *
 * 현재 조회 기준에서
 * 신살 Evidence 자체가 얼마나 확정적으로
 * 존재하는지를 표현한다.
 *
 * =================================================
 *
 * DEFINITE
 *
 * 원국 Evidence이거나,
 *
 * 현재 가능한 모든 Timeline Scenario에서
 * 동일 신살 Evidence가 존재한다.
 *
 * =================================================
 *
 * POSSIBLE
 *
 * 현재 가능한 Timeline Scenario 중
 * 일부에서만 해당 신살 Evidence가 존재한다.
 *
 * =================================================
 *
 * 중요:
 *
 * 이것은:
 *
 * - 신살 강도
 * - 길흉 점수
 * - 사건 발생 확률
 * - confidence score
 *
 * 가 아니다.
 */
public enum ShinsalEvidenceCertainty {

    DEFINITE(
            "확정"
    ),

    POSSIBLE(
            "가능"
    );

    private final String koreanName;

    ShinsalEvidenceCertainty(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean definite() {

        return this == DEFINITE;
    }

    public boolean possibleOnly() {

        return this == POSSIBLE;
    }
}