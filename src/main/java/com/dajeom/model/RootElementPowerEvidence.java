package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.RootRelationType;
import com.dajeom.type.RootSourcePosition;

import java.util.Objects;

/**
 * 오행 세력 계산에 사용할 통근 근거.
 *
 * 중요:
 *
 * 이 값은 새로운 지장간 세력을 추가하는 값이 아니다.
 *
 * 지장간 자체의 세력은 이미
 * HiddenElementPowerAnalysis에서 계산되어 있다.
 *
 * 여기서는 해당 지장간이
 * 표면 천간의 뿌리로 실제 연결되어 있는지를
 * 별도의 활성 근거로 보존한다.
 */
public record RootElementPowerEvidence(

        FiveElement targetElement,

        HeavenlyStem targetStem,

        RootSourcePosition sourcePosition,

        EarthlyBranch sourceBranch,

        HeavenlyStem hiddenStem,

        HiddenStemRole hiddenStemRole,

        RootRelationType relationType,

        int hiddenStemWeight,

        int positionWeightRate,

        int relationWeightRate,

        int effectiveRootWeight

) {

    public RootElementPowerEvidence {

        Objects.requireNonNull(
                targetElement,
                "통근 대상 오행이 필요합니다."
        );

        Objects.requireNonNull(
                targetStem,
                "통근 대상 천간이 필요합니다."
        );

        Objects.requireNonNull(
                sourcePosition,
                "통근 근원 위치가 필요합니다."
        );

        Objects.requireNonNull(
                sourceBranch,
                "통근 근원 지지가 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "통근 지장간이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStemRole,
                "지장간 역할이 필요합니다."
        );

        Objects.requireNonNull(
                relationType,
                "통근 관계가 필요합니다."
        );

        if (
                hiddenStemWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "지장간 세력은 0보다 커야 합니다."
            );
        }

        if (
                positionWeightRate <= 0
        ) {

            throw new IllegalArgumentException(
                    "통근 위치 가중치는 0보다 커야 합니다."
            );
        }

        if (
                relationWeightRate <= 0
        ) {

            throw new IllegalArgumentException(
                    "통근 관계 가중치는 0보다 커야 합니다."
            );
        }

        if (
                effectiveRootWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "통근 유효 세력은 0보다 커야 합니다."
            );
        }
    }

    /**
     * 같은 천간으로 직접 뿌리를 둔 경우.
     */
    public boolean directRoot() {

        return relationType.direct();
    }

    /**
     * 같은 오행의 다른 음양 천간으로
     * 보조 연결된 경우.
     */
    public boolean supportingRoot() {

        return relationType.supporting();
    }

    public boolean monthRoot() {

        return sourcePosition.monthBranch();
    }

    public boolean dayRoot() {

        return sourcePosition.dayBranch();
    }

    public boolean mainQiRoot() {

        return hiddenStemRole.isMain();
    }

    public boolean middleQiRoot() {

        return hiddenStemRole.isMiddle();
    }

    public boolean residualQiRoot() {

        return hiddenStemRole.isResidual();
    }

    /**
     * 월지 정기에 같은 천간으로
     * 직접 통근한 강한 근거.
     */
    public boolean strongMonthMainDirectRoot() {

        return monthRoot()
                && mainQiRoot()
                && directRoot();
    }
}