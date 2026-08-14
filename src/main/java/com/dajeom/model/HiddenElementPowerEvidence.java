package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StemExposurePosition;

import java.util.Objects;

/**
 * 지장간 하나가 오행 세력에 기여하는
 * 개별 내부 근거.
 *
 * 예:
 *
 * 월지 申
 * ├─ 庚 정기 60 -> 金 60
 * ├─ 壬 중기 25 -> 水 25
 * └─ 戊 여기 15 -> 土 15
 *
 * 이 단계의 baseWeight는
 * 지장간 테이블의 원시 세력값이다.
 *
 * 아직:
 *
 * - 월령 계절 보정
 * - 통근
 * - 투간
 * - 생극
 * - 합충
 *
 * 을 반영하지 않는다.
 */
public record HiddenElementPowerEvidence(

        StemExposurePosition sourcePosition,

        EarthlyBranch sourceBranch,

        HeavenlyStem hiddenStem,

        HiddenStemRole hiddenStemRole,

        FiveElement element,

        int baseWeight

) {

    public HiddenElementPowerEvidence {

        Objects.requireNonNull(
                sourcePosition,
                "지장간 출처 위치가 필요합니다."
        );

        Objects.requireNonNull(
                sourceBranch,
                "지장간 출처 지지가 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStem,
                "지장간 천간이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenStemRole,
                "지장간 역할이 필요합니다."
        );

        Objects.requireNonNull(
                element,
                "지장간 오행이 필요합니다."
        );

        if (
                baseWeight <= 0
        ) {

            throw new IllegalArgumentException(
                    "지장간 기본 세력은 0보다 커야 합니다."
            );
        }
    }

    public boolean yearBranch() {

        return sourcePosition.isYear();
    }

    public boolean monthBranch() {

        return sourcePosition.isMonth();
    }

    public boolean dayBranch() {

        return sourcePosition.isDay();
    }

    public boolean hourBranch() {

        return sourcePosition.isHour();
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
     * 월지 정기인지.
     *
     * 다음 3-3 월령 계절 보정에서
     * 중요한 근거가 된다.
     */
    public boolean monthMainQi() {

        return monthBranch()
                && mainQi();
    }
}