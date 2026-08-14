package com.dajeom.type;

/**
 * 정밀화 11차-4.
 *
 * 12운성을 사용자 설명 및 후속 분석에서
 * 다루기 쉽게 묶은 중립적 상태 그룹.
 *
 * =================================================
 *
 * 이 분류는:
 *
 * - 길흉 등급
 * - 신강신약 점수
 * - 오행 세력
 * - 격국 성패
 *
 * 를 의미하지 않는다.
 *
 * =================================================
 *
 * PREPARATION
 *
 * 아직 외부 활동으로 완전히 전개되기 전의
 * 준비·잠재 상태.
 *
 * 胎, 養
 *
 * =================================================
 *
 * EMERGENCE
 *
 * 기능이 나타나고 외부와 접촉하기 시작하는 상태.
 *
 * 長生, 沐浴
 *
 * =================================================
 *
 * DEVELOPMENT
 *
 * 형태와 역할이 보다 구체화되는 상태.
 *
 * 冠帶, 臨官
 *
 * =================================================
 *
 * PEAK_ACTIVITY
 *
 * 활동성이 가장 응축되는 상태.
 *
 * 帝旺
 *
 * =================================================
 *
 * DECLINING_ACTIVITY
 *
 * 기존 활동성이 점차 감소하거나
 * 방향을 조정하는 상태.
 *
 * 衰, 病
 *
 * =================================================
 *
 * CLOSURE_STORAGE
 *
 * 활동이 정리되거나 저장되는 상태.
 *
 * 死, 墓
 *
 * =================================================
 *
 * TRANSITION
 *
 * 기존 흐름이 끊어지고
 * 다음 순환으로 넘어가는 전환 상태.
 *
 * 絶
 */
public enum TwelveLifeStageStateGroup {

    PREPARATION(
            "준비·잠재"
    ),

    EMERGENCE(
            "발생·전개"
    ),

    DEVELOPMENT(
            "성장·구체화"
    ),

    PEAK_ACTIVITY(
            "최대 활성"
    ),

    DECLINING_ACTIVITY(
            "활성 감소·조정"
    ),

    CLOSURE_STORAGE(
            "정리·저장"
    ),

    TRANSITION(
            "전환·재구성"
    );

    private final String koreanName;

    TwelveLifeStageStateGroup(
            String koreanName
    ) {

        this.koreanName =
                koreanName;
    }

    public String koreanName() {

        return koreanName;
    }

    public boolean preparation() {

        return this == PREPARATION;
    }

    public boolean emergence() {

        return this == EMERGENCE;
    }

    public boolean development() {

        return this == DEVELOPMENT;
    }

    public boolean peakActivity() {

        return this == PEAK_ACTIVITY;
    }

    public boolean decliningActivity() {

        return this == DECLINING_ACTIVITY;
    }

    public boolean closureStorage() {

        return this == CLOSURE_STORAGE;
    }

    public boolean transition() {

        return this == TRANSITION;
    }
}