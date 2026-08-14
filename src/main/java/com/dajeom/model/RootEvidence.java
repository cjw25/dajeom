package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.RootRelationType;
import com.dajeom.type.RootSourcePosition;

import java.util.Objects;

/**
 * 하나의 통근 근거.
 *
 * 예:
 *
 * 甲 천간
 * ↓
 * 寅
 * ↓
 * 寅 지장간 甲
 * ↓
 * 정기
 * ↓
 * SAME_STEM
 *
 * 같은 방식으로 각각의 뿌리를
 * 개별 Evidence로 보존한다.
 *
 * 이후 신강·신약 판정에서
 * 단순 점수만 보는 것이 아니라
 * 왜 그 점수가 나왔는지 추적할 수 있다.
 */
public record RootEvidence(

        HeavenlyStem targetStem,

        RootSourcePosition sourcePosition,

        EarthlyBranch sourceBranch,

        HeavenlyStem hiddenStem,

        HiddenStemRole hiddenStemRole,

        RootRelationType relationType,

        int hiddenStemWeight,

        int positionWeightRate,

        int relationWeightRate,

        int effectiveWeight

) {

    public RootEvidence {

        Objects.requireNonNull(
                targetStem,
                "통근 대상 천간이 필요합니다."
        );

        Objects.requireNonNull(
                sourcePosition,
                "통근 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                sourceBranch,
                "통근 지지가 필요합니다."
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
                    "지장간 가중치는 0보다 커야 합니다."
            );
        }

        if (
                positionWeightRate <= 0
        ) {
            throw new IllegalArgumentException(
                    "위치 가중치는 0보다 커야 합니다."
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
                effectiveWeight <= 0
        ) {
            throw new IllegalArgumentException(
                    "최종 통근 가중치는 0보다 커야 합니다."
            );
        }
    }

    /**
     * 동일 천간으로 직접 통근했는지.
     */
    public boolean directRoot() {

        return relationType.direct();
    }

    /**
     * 같은 오행의 다른 천간을 통한
     * 보조근인지.
     */
    public boolean supportingRoot() {

        return relationType.supporting();
    }

    /**
     * 월지에서 확보한 뿌리인지.
     */
    public boolean monthRoot() {

        return sourcePosition.monthBranch();
    }

    /**
     * 일지에서 확보한 뿌리인지.
     */
    public boolean dayRoot() {

        return sourcePosition.dayBranch();
    }

    /**
     * 정기에 뿌리를 두었는지.
     */
    public boolean mainHiddenStemRoot() {

        return hiddenStemRole.isMain();
    }
}