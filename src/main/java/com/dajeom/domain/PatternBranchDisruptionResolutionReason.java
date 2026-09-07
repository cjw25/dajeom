package com.dajeom.domain;

/**
 * 지지 교란 해소 후보가
 * 어떤 이유로 만들어졌는지 나타낸다.
 *
 * 점수 / 가중치가 아니다.
 */
public enum PatternBranchDisruptionResolutionReason {

    /**
     * 六合이 교란의 한쪽 지지를 묶는 후보.
     */
    SIX_HARMONY_RESOLUTION_CANDIDATE,

    /**
     * 완전 三合이 교란의 한쪽 지지를
     * 회국으로 묶는 후보.
     */
    COMPLETE_THREE_HARMONY_RESOLUTION_CANDIDATE,

    /**
     * 완전 三會/方合이 교란의 한쪽 지지를
     * 회국으로 묶는 후보.
     */
    COMPLETE_THREE_MEETING_RESOLUTION_CANDIDATE,

    /**
     * 하나의 교란에 복수의 회합 해소 후보가 존재.
     *
     * 바로 하나를 선택하지 않는다.
     */
    MULTIPLE_RESOLUTION_CANDIDATES,

    /**
     * 해소에 사용되는 상대 지지가
     * 다른 회합에도 참여하고 있음.
     *
     * 예:
     *
     * 子午沖
     * +
     * 子丑合
     *
     * 이지만
     *
     * 丑이 다시 巳酉丑 회국에 참여.
     *
     * → 子丑合이 실제 沖을 풀 수 있는지
     *   추가 검토 필요.
     */
    RESOLVER_PARTNER_INVOLVED_IN_OTHER_COMBINATION
}