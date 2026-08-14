package com.dajeom.rule;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.TwelveLifeStage;
import com.dajeom.type.TwelveLifeStageContextScope;
import com.dajeom.type.TwelveLifeStagePositionContextType;
import com.dajeom.type.TwelveLifeStageStateGroup;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 11차-4.
 *
 * 12운성 상태 및 위치 Context Rule.
 *
 * =================================================
 *
 * 이 Rule은 점수 계산을 하지 않는다.
 *
 * 책임:
 *
 * 1.
 * 12운성 -> 중립적 상태 그룹
 *
 * 2.
 * YEAR/MONTH/DAY/HOUR -> 위치 Context
 *
 * 3.
 * completeChart -> 해석 Scope
 *
 * =================================================
 *
 * 고전의 12운성 원래 상태를 삭제하거나
 * 다른 상태로 바꾸지 않는다.
 *
 * 원래 TwelveLifeStage는
 * sourceEvidence에 그대로 보존된다.
 */
@Component
public class TwelveLifeStageContextRule {

    /**
     * =================================================
     * 12운성 -> 상태 그룹
     * =================================================
     */
    public TwelveLifeStageStateGroup stateGroupOf(
            TwelveLifeStage stage
    ) {

        Objects.requireNonNull(
                stage,
                "12운성 상태가 필요합니다."
        );

        return switch (
                stage
                ) {

            /*
             * 다음 순환을 준비하는 잠재 단계.
             */
            case TAI,
                 YANG ->
                    TwelveLifeStageStateGroup
                            .PREPARATION;

            /*
             * 발생 및 외부 전개가 시작되는 단계.
             */
            case CHANG_SHENG,
                 MU_YU ->
                    TwelveLifeStageStateGroup
                            .EMERGENCE;

            /*
             * 역할과 형태가 구체화되는 단계.
             */
            case GUAN_DAI,
                 LIN_GUAN ->
                    TwelveLifeStageStateGroup
                            .DEVELOPMENT;

            /*
             * 활동성이 가장 응축된 단계.
             */
            case DI_WANG ->
                    TwelveLifeStageStateGroup
                            .PEAK_ACTIVITY;

            /*
             * 기존 활동성이 점차 감소하는 단계.
             */
            case SHUAI,
                 BING ->
                    TwelveLifeStageStateGroup
                            .DECLINING_ACTIVITY;

            /*
             * 기존 흐름이 정리 또는 저장되는 단계.
             */
            case SI,
                 MU ->
                    TwelveLifeStageStateGroup
                            .CLOSURE_STORAGE;

            /*
             * 기존 순환과 다음 순환 사이의
             * 전환점.
             */
            case JUE ->
                    TwelveLifeStageStateGroup
                            .TRANSITION;
        };
    }

    /**
     * =================================================
     * 위치 -> Context
     * =================================================
     */
    public TwelveLifeStagePositionContextType
    positionContextOf(
            EarthlyBranchPosition position
    ) {

        Objects.requireNonNull(
                position,
                "지지 위치가 필요합니다."
        );

        return switch (
                position
                ) {

            case YEAR ->
                    TwelveLifeStagePositionContextType
                            .YEAR_BACKGROUND;

            case MONTH ->
                    TwelveLifeStagePositionContextType
                            .MONTH_ENVIRONMENT;

            case DAY ->
                    TwelveLifeStagePositionContextType
                            .DAY_CORE;

            case HOUR ->
                    TwelveLifeStagePositionContextType
                            .HOUR_EXTENSION;
        };
    }

    /**
     * =================================================
     * 원국 범위
     * =================================================
     */
    public TwelveLifeStageContextScope scopeOf(
            boolean completeChart
    ) {

        return completeChart
                ? TwelveLifeStageContextScope.FULL_CHART
                : TwelveLifeStageContextScope.THREE_PILLAR_ONLY;
    }
}