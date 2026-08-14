package com.dajeom.model;

import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;

import java.util.Objects;

/**
 * 정밀화 11차-3.
 *
 * 원국의 특정 지지가
 * 일간 기준으로 어떤 12운성에 해당하는지
 * 보존하는 Evidence.
 *
 * =================================================
 *
 * 예:
 *
 * 일간 丁
 *
 * YEAR  酉 -> 長生
 * MONTH 申 -> 沐浴
 * DAY   丑 -> 墓
 * HOUR  辰 -> 衰
 *
 * =================================================
 *
 * 중요:
 *
 * position은 12운성의 강약 순위가 아니다.
 *
 * 단지:
 *
 * - 년지
 * - 월지
 * - 일지
 * - 시지
 *
 * 중 어디에서 해당 상태가 나타났는지를
 * 추적하기 위한 위치 정보다.
 *
 * =================================================
 *
 * TwelveLifeStage 역시:
 *
 * - 오행 세력 점수
 * - 신강신약 점수
 * - 길흉 점수
 *
 * 로 변환하지 않는다.
 */
public record TwelveLifeStageEvidence(

        HeavenlyStem dayMaster,

        EarthlyBranchPosition position,

        EarthlyBranch branch,

        TwelveLifeStage stage

) {

    public TwelveLifeStageEvidence {

        Objects.requireNonNull(
                dayMaster,
                "12운성 Evidence에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                position,
                "12운성 Evidence에는 지지 위치가 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "12운성 Evidence에는 지지가 필요합니다."
        );

        Objects.requireNonNull(
                stage,
                "12운성 Evidence에는 12운성 상태가 필요합니다."
        );
    }

    public boolean yearPosition() {

        return position
                == EarthlyBranchPosition.YEAR;
    }

    public boolean monthPosition() {

        return position
                == EarthlyBranchPosition.MONTH;
    }

    public boolean dayPosition() {

        return position
                == EarthlyBranchPosition.DAY;
    }

    public boolean hourPosition() {

        return position
                == EarthlyBranchPosition.HOUR;
    }

    public boolean is(
            TwelveLifeStage expectedStage
    ) {

        return expectedStage != null
                && stage == expectedStage;
    }

    public boolean changSheng() {

        return stage
                == TwelveLifeStage.CHANG_SHENG;
    }

    public boolean muYu() {

        return stage
                == TwelveLifeStage.MU_YU;
    }

    public boolean guanDai() {

        return stage
                == TwelveLifeStage.GUAN_DAI;
    }

    public boolean linGuan() {

        return stage
                == TwelveLifeStage.LIN_GUAN;
    }

    public boolean diWang() {

        return stage
                == TwelveLifeStage.DI_WANG;
    }

    public boolean shuai() {

        return stage
                == TwelveLifeStage.SHUAI;
    }

    public boolean bing() {

        return stage
                == TwelveLifeStage.BING;
    }

    public boolean si() {

        return stage
                == TwelveLifeStage.SI;
    }

    public boolean mu() {

        return stage
                == TwelveLifeStage.MU;
    }

    public boolean jue() {

        return stage
                == TwelveLifeStage.JUE;
    }

    public boolean tai() {

        return stage
                == TwelveLifeStage.TAI;
    }

    public boolean yang() {

        return stage
                == TwelveLifeStage.YANG;
    }
}