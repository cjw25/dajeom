package com.dajeom.type;

/**
 * 정밀화 16차-5.
 *
 * 통합 Timeline Segment에서
 * 대운 계층의 상태를 표현한다.
 *
 * =================================================
 *
 * ACTIVE
 *
 * 특정 DecadeLuckCandidate가
 * 실제로 해당 Timeline Segment에 참여한다.
 *
 * =================================================
 *
 * BEFORE_FIRST
 *
 * 아직 첫 대운이 시작되지 않았을 가능성
 * 또는 확정 구간.
 *
 * 첫 대운 시작시각이 불확실하다면:
 *
 * [earliestStart, latestStart)
 *
 * 구간에서는:
 *
 * BEFORE_FIRST 가능
 * +
 * 첫 대운 가능
 *
 * 이 동시에 존재할 수 있다.
 *
 * =================================================
 *
 * AFTER_GENERATED_RANGE
 *
 * 현재 생성해 둔 마지막 대운 Period 이후.
 *
 * 실제로 "대운이 없다"는 뜻이 아니다.
 *
 * 아직 다음 대운 Sequence를 생성하지 않은
 * 데이터 범위 밖이라는 의미다.
 *
 * =================================================
 *
 * UNAVAILABLE
 *
 * DecadeLuckAnalysis 자체를 이용할 수 없어
 * 현재 Timeline에서 대운을 연결할 수 없는 상태.
 */
public enum LuckTimelineDecadeState {

    ACTIVE(
            "대운 활성"
    ),

    BEFORE_FIRST(
            "첫 대운 시작 전"
    ),

    AFTER_GENERATED_RANGE(
            "생성 대운 범위 이후"
    ),

    UNAVAILABLE(
            "대운 분석 불가"
    );

    private final String koreanName;

    LuckTimelineDecadeState(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean active() {

        return this == ACTIVE;
    }

    public boolean beforeFirst() {

        return this == BEFORE_FIRST;
    }

    public boolean afterGeneratedRange() {

        return this == AFTER_GENERATED_RANGE;
    }

    public boolean unavailable() {

        return this == UNAVAILABLE;
    }
}