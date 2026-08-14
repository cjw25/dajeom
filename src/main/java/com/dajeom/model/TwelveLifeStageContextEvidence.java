package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;
import com.dajeom.type.TwelveLifeStagePositionContextType;
import com.dajeom.type.TwelveLifeStageStateGroup;

import java.util.Objects;

/**
 * 정밀화 11차-4.
 *
 * 11-3 TwelveLifeStageEvidence에
 *
 * - 상태 그룹
 * - 위치 Context
 *
 * 를 추가한 Evidence.
 *
 * =================================================
 *
 * sourceEvidence를 그대로 보존하므로
 * 원래 계산값을 잃지 않는다.
 *
 * =================================================
 *
 * 예:
 *
 * 丁 / YEAR 酉 / 長生
 *
 * ->
 *
 * stateGroup =
 * EMERGENCE
 *
 * positionContext =
 * YEAR_BACKGROUND
 */
public record TwelveLifeStageContextEvidence(

        TwelveLifeStageEvidence sourceEvidence,

        TwelveLifeStageStateGroup stateGroup,

        TwelveLifeStagePositionContextType positionContext

) {

    public TwelveLifeStageContextEvidence {

        Objects.requireNonNull(
                sourceEvidence,
                "11-3 12운성 Evidence가 필요합니다."
        );

        Objects.requireNonNull(
                stateGroup,
                "12운성 상태 그룹이 필요합니다."
        );

        Objects.requireNonNull(
                positionContext,
                "12운성 위치 Context가 필요합니다."
        );
    }

    public HeavenlyStem dayMaster() {

        return sourceEvidence
                .dayMaster();
    }

    public EarthlyBranchPosition position() {

        return sourceEvidence
                .position();
    }

    public EarthlyBranch branch() {

        return sourceEvidence
                .branch();
    }

    public TwelveLifeStage stage() {

        return sourceEvidence
                .stage();
    }

    public boolean yearPosition() {

        return sourceEvidence
                .yearPosition();
    }

    public boolean monthPosition() {

        return sourceEvidence
                .monthPosition();
    }

    public boolean dayPosition() {

        return sourceEvidence
                .dayPosition();
    }

    public boolean hourPosition() {

        return sourceEvidence
                .hourPosition();
    }

    public boolean isStage(
            TwelveLifeStage expected
    ) {

        return expected != null
                && stage() == expected;
    }

    public boolean isStateGroup(
            TwelveLifeStageStateGroup expected
    ) {

        return expected != null
                && stateGroup == expected;
    }

    public boolean isPositionContext(
            TwelveLifeStagePositionContextType expected
    ) {

        return expected != null
                && positionContext == expected;
    }
}