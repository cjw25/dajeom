package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;

import java.util.Objects;

/**
 * 정밀화 12차-2.
 *
 * 특정 궁위의 지지 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 癸酉
 * 庚申
 * 丁丑
 * 甲辰
 *
 * 이라면:
 *
 * YEAR  -> 酉
 * MONTH -> 申
 * DAY   -> 丑
 * HOUR  -> 辰
 *
 * =================================================
 *
 * 이번 단계에서는 아직:
 *
 * - 지장간
 * - 본기
 * - 십성
 * - 12운성
 * - 합충형파해
 *
 * 를 이 객체 안에 섞지 않는다.
 *
 * 그런 분석은 기존 단계 결과와
 * 12-3 / 12-4에서 결합한다.
 */
public record PalaceBranchEvidence(

        HeavenlyStem dayMaster,

        PalacePosition position,

        PalacePositionContextType positionContext,

        EarthlyBranch branch

) {

    public PalaceBranchEvidence {

        Objects.requireNonNull(
                dayMaster,
                "궁위 지지 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "궁위 지지 Evidence에는 궁위 위치가 필요합니다."
        );

        Objects.requireNonNull(
                positionContext,
                "궁위 지지 Evidence에는 위치 Context가 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "궁위 지지 Evidence에는 지지가 필요합니다."
        );
    }

    public boolean yearPosition() {

        return position == PalacePosition.YEAR;
    }

    public boolean monthPosition() {

        return position == PalacePosition.MONTH;
    }

    public boolean dayPosition() {

        return position == PalacePosition.DAY;
    }

    public boolean hourPosition() {

        return position == PalacePosition.HOUR;
    }
}