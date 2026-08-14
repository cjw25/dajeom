package com.dajeom.type;

/**
 * 정밀화 9차-7.
 *
 * 지지 결합 후보에 대한
 * 충·형·파·해 방해 수준.
 *
 * =================================================
 *
 * NONE
 *
 * 후보 구성원에 영향을 주는
 * 충·형·파·해 Evidence가 없음.
 *
 * =================================================
 *
 * MINOR
 *
 * 결합 구성원 하나에
 * 비교적 간접적이거나 원거리의
 * 단일 방해 관계가 존재.
 *
 * 기본 결합 후보 자체를 무효화하지 않는다.
 *
 * =================================================
 *
 * MEANINGFUL
 *
 * 결합 내부 Pair를 직접 건드리거나,
 * 왕지를 건드리거나,
 * 충·형 등의 직접적 관계가 있거나,
 * 인접한 방해가 존재하는 상태.
 *
 * 9-8에서 실제 결합 성립 상태를
 * 조정해야 하는 수준이다.
 *
 * =================================================
 *
 * SEVERE
 *
 * 다음과 같은 강한 구조:
 *
 * - 결합 내부 직접 沖
 * - 내부에 복수 방해 종류 중첩
 * - 복수 구성원이 동시에 영향받음
 * - 왕지가 沖을 받음
 * - 복수 구성원에 沖/刑이 걸림
 *
 * 등이 존재.
 *
 * =================================================
 *
 * 중요:
 *
 * SEVERE도 "결합 관계 자체 삭제"를 뜻하지 않는다.
 *
 * 최종 조정은 9-8에서 한다.
 */
public enum EarthlyBranchCombinationObstructionLevel {

    NONE(
            "방해 없음",
            1
    ),

    MINOR(
            "경미한 방해",
            2
    ),

    MEANINGFUL(
            "유의미한 방해",
            3
    ),

    SEVERE(
            "강한 방해",
            4
    );

    private final String koreanName;

    private final int rank;

    EarthlyBranchCombinationObstructionLevel(
            String koreanName,
            int rank
    ) {

        this.koreanName = koreanName;
        this.rank = rank;
    }

    public String koreanName() {

        return koreanName;
    }

    public int rank() {

        return rank;
    }

    public boolean none() {

        return this == NONE;
    }

    public boolean minor() {

        return this == MINOR;
    }

    public boolean meaningful() {

        return this == MEANINGFUL;
    }

    public boolean severe() {

        return this == SEVERE;
    }

    public boolean hasObstruction() {

        return !none();
    }

    public boolean requiresAdjustment() {

        return meaningful()
                || severe();
    }
}