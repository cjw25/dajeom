package com.dajeom.calculator;

import com.dajeom.constant.TwelveLifeStageTable;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 정밀화 11차-2.
 *
 * 일간 기준 12운성 계산기.
 *
 * =================================================
 *
 * 공식 입력:
 *
 * HeavenlyStem dayMaster
 * EarthlyBranch branch
 *
 * =================================================
 *
 * 공식 출력:
 *
 * TwelveLifeStage
 *
 * =================================================
 *
 * 예:
 *
 * 丁 + 酉
 * -> 長生
 *
 * 丁 + 申
 * -> 沐浴
 *
 * 丁 + 丑
 * -> 墓
 *
 * 丁 + 辰
 * -> 衰
 *
 * =================================================
 *
 * 중요 책임 분리:
 *
 * 이 Calculator는:
 *
 * - 년주인지
 * - 월주인지
 * - 일주인지
 * - 시주인지
 * - 출생시간 미상인지
 *
 * 를 판단하지 않는다.
 *
 * 오직:
 *
 * 일간 + 지지
 * -> 12운성
 *
 * 만 계산한다.
 *
 * =================================================
 *
 * 또한 12운성을:
 *
 * - 오행 세력
 * - 신강신약 점수
 * - 격국 점수
 *
 * 에 합산하지 않는다.
 */
@Component
public class TwelveLifeStageCalculator {

    /**
     * =================================================
     * 공식 계산 API
     * =================================================
     *
     * 일간을 기준으로
     * 대상 지지의 12운성을 계산한다.
     */
    public TwelveLifeStage calculate(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        Objects.requireNonNull(
                dayMaster,
                "12운성 계산에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                branch,
                "12운성 계산에는 대상 지지가 필요합니다."
        );

        return TwelveLifeStageTable.get(
                dayMaster,
                branch
        );
    }

    /**
     * =================================================
     * 장생 여부
     * =================================================
     */
    public boolean isChangSheng(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        return calculate(
                dayMaster,
                branch
        )
                == TwelveLifeStage.CHANG_SHENG;
    }

    /**
     * =================================================
     * 임관 여부
     * =================================================
     */
    public boolean isLinGuan(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        return calculate(
                dayMaster,
                branch
        )
                == TwelveLifeStage.LIN_GUAN;
    }

    /**
     * =================================================
     * 제왕 여부
     * =================================================
     */
    public boolean isDiWang(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        return calculate(
                dayMaster,
                branch
        )
                == TwelveLifeStage.DI_WANG;
    }

    /**
     * =================================================
     * 묘 여부
     * =================================================
     */
    public boolean isMu(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        return calculate(
                dayMaster,
                branch
        )
                == TwelveLifeStage.MU;
    }

    /**
     * =================================================
     * 절 여부
     * =================================================
     */
    public boolean isJue(

            HeavenlyStem dayMaster,

            EarthlyBranch branch

    ) {

        return calculate(
                dayMaster,
                branch
        )
                == TwelveLifeStage.JUE;
    }

    /**
     * =================================================
     * 특정 상태 여부
     * =================================================
     *
     * 이후 Analyzer에서 특정 12운성 상태를
     * 확인해야 할 때 사용할 수 있다.
     */
    public boolean is(

            HeavenlyStem dayMaster,

            EarthlyBranch branch,

            TwelveLifeStage expectedStage

    ) {

        Objects.requireNonNull(
                expectedStage,
                "비교할 12운성이 필요합니다."
        );

        return calculate(
                dayMaster,
                branch
        )
                == expectedStage;
    }

    /**
     * =================================================
     * 특정 일간의 12지 전체 계산
     * =================================================
     *
     * 12운성 기준표 전체를
     * Calculator API를 통해 조회할 때 사용한다.
     *
     * 반환 Map:
     *
     * 子 -> ...
     * 丑 -> ...
     * 寅 -> ...
     * ...
     *
     * =================================================
     *
     * 반환 Map은 호출자가 수정할 수 없는
     * 독립 복사본으로 만든다.
     */
    public Map<EarthlyBranch, TwelveLifeStage> calculateAll(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "12운성 전체 계산에는 일간이 필요합니다."
        );

        EnumMap<EarthlyBranch, TwelveLifeStage> result =
                new EnumMap<>(
                        EarthlyBranch.class
                );

        for (
                EarthlyBranch branch
                : EarthlyBranch.values()
        ) {

            result.put(
                    branch,
                    calculate(
                            dayMaster,
                            branch
                    )
            );
        }

        return Map.copyOf(
                result
        );
    }

    /**
     * =================================================
     * 특정 12운성에 해당하는 지지 역조회
     * =================================================
     *
     * 예:
     *
     * 丁 + 長生
     * -> 酉
     *
     * 丁 + 帝旺
     * -> 巳
     */
    public EarthlyBranch branchOf(

            HeavenlyStem dayMaster,

            TwelveLifeStage stage

    ) {

        Objects.requireNonNull(
                dayMaster,
                "12운성 역조회에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                stage,
                "12운성 역조회에는 상태가 필요합니다."
        );

        return TwelveLifeStageTable.branchOf(
                dayMaster,
                stage
        );
    }

    /**
     * =================================================
     * 장생지 역조회
     * =================================================
     */
    public EarthlyBranch changShengBranchOf(
            HeavenlyStem dayMaster
    ) {

        Objects.requireNonNull(
                dayMaster,
                "장생지 조회에는 일간이 필요합니다."
        );

        return TwelveLifeStageTable
                .changShengBranchOf(
                        dayMaster
                );
    }
}