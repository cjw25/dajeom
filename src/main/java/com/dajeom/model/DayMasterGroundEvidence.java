package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.RootRelationType;
import com.dajeom.type.RootSourcePosition;

import java.util.Objects;

/**
 * 정밀화 4차-3.
 *
 * 일간이 특정 지지에서 확보한
 * 하나의 통근/득지 근거.
 *
 * 이 모델은 2차에서 계산된 RootEvidence를
 * 신강·신약 분석용으로 재구성한다.
 *
 * 새로운 통근 점수를 만들지 않는다.
 *
 * 예:
 *
 * 丁 일간
 *
 * 午
 * └─ 丁 정기
 *
 * 이 경우:
 *
 * - SAME_STEM
 * - MAIN
 * - 직접근
 *
 * 으로 기록된다.
 *
 * 반면:
 *
 * 丁 일간
 *
 * 巳
 * └─ 丙 정기
 *
 * 라면 같은 火이지만
 * 천간 자체는 다르므로:
 *
 * - SAME_ELEMENT
 * - 보조근
 *
 * 으로 구분한다.
 */
public record DayMasterGroundEvidence(

        HeavenlyStem dayMaster,

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

    public DayMasterGroundEvidence {

        Objects.requireNonNull(
                dayMaster,
                "일간이 필요합니다."
        );

        Objects.requireNonNull(
                sourcePosition,
                "통근 위치가 필요합니다."
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
     * 일간과 완전히 같은 천간이
     * 지장간에 존재하는 직접근.
     */
    public boolean directRoot() {

        return relationType
                == RootRelationType.SAME_STEM;
    }

    /**
     * 일간과 같은 오행이지만
     * 음양 또는 천간이 다른 보조근.
     */
    public boolean supportingRoot() {

        return relationType
                == RootRelationType.SAME_ELEMENT;
    }

    public boolean yearBranch() {

        return sourcePosition
                == RootSourcePosition.YEAR;
    }

    public boolean monthBranch() {

        return sourcePosition
                == RootSourcePosition.MONTH;
    }

    public boolean dayBranch() {

        return sourcePosition
                == RootSourcePosition.DAY;
    }

    public boolean hourBranch() {

        return sourcePosition
                == RootSourcePosition.HOUR;
    }

    public boolean mainQi() {

        return hiddenStemRole.isMain();
    }

    public boolean middleQi() {

        return hiddenStemRole.isMiddle();
    }

    public boolean residualQi() {

        return hiddenStemRole.isResidual();
    }

    /**
     * 월지 정기에 일간과 같은 천간이
     * 직접 존재하는 매우 명확한 근.
     *
     * 득령과 동일한 개념은 아니다.
     *
     * 월령의 계절 관계는 4-2,
     * 월지 내부의 실제 뿌리는 4-3에서
     * 서로 분리한다.
     */
    public boolean monthMainDirectRoot() {

        return monthBranch()
                && mainQi()
                && directRoot();
    }

    /**
     * 일지 정기에 일간과 같은 천간이
     * 직접 존재하는 근.
     */
    public boolean dayMainDirectRoot() {

        return dayBranch()
                && mainQi()
                && directRoot();
    }

    /**
     * 월지 또는 일지에서 확보한
     * 중심 위치의 근인지.
     */
    public boolean centralGround() {

        return monthBranch()
                || dayBranch();
    }

    /**
     * 월지/일지의 정기에
     * 직접근을 확보했는지.
     */
    public boolean strongCentralDirectRoot() {

        return centralGround()
                && mainQi()
                && directRoot();
    }
}