package com.dajeom.type;

/**
 * 정밀화 16차-2.
 *
 * 특정 운 Candidate의 확정 수준.
 *
 * =================================================
 *
 * DEFINITE
 *
 * 현재 LuckReferenceContext 전체를 고려해도
 * 해당 운이라고 확정할 수 있다.
 *
 * 예:
 *
 * 정확한 시각이 대운 definite window 안에 존재.
 *
 * 또는:
 *
 * DATE_ONLY로 조회했지만
 * 그 날짜 전체가 하나의 대운 definite window 안에 존재.
 *
 * =================================================
 *
 * POSSIBLE
 *
 * 해당 운일 가능성은 있지만
 * 현재 입력 정보만으로 확정할 수 없다.
 *
 * 예:
 *
 * - 출생시간 미상 교운구간
 * - 조회시간 미상
 * - 교운 당일 DATE_ONLY
 *
 * =================================================
 *
 * POSSIBLE은 "낮은 확률"이라는 의미가 아니다.
 *
 * 확률 계산을 하지 않는다.
 */
public enum LuckCandidateCertainty {

    DEFINITE(
            "확정"
    ),

    POSSIBLE(
            "가능"
    );

    private final String koreanName;

    LuckCandidateCertainty(
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