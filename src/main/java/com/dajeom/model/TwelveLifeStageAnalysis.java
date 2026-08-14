package com.dajeom.model;

import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.TwelveLifeStage;
import com.dajeom.type.TwelveLifeStageContextScope;
import com.dajeom.type.TwelveLifeStagePositionContextType;
import com.dajeom.type.TwelveLifeStageStateGroup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 11차-5.
 *
 * 12운성 최종 Aggregate.
 *
 * =================================================
 *
 * 포함 단계:
 *
 * 11-1 십간 12운성 기준표
 * 11-2 일간 기준 12운성 계산
 * 11-3 년·월·일·시 Evidence
 * 11-4 위치·시간미상 Context
 *
 * =================================================
 *
 * 이 객체가 정밀화 11차의 공식 최종 결과다.
 *
 * 이후:
 *
 * 12차 궁위
 * 13차 대운
 * 14차 세운
 * 15차 월운
 * 16차 운 통합
 * 18차 생활영역
 * 19차 고민분석
 * 20차 AI Grounding
 *
 * 에서 12운성이 필요할 경우
 * 이 Aggregate를 기준으로 접근한다.
 *
 * =================================================
 *
 * 중요 정책:
 *
 * 1.
 * 12운성을 신강신약 점수로 사용하지 않는다.
 *
 * 2.
 * 장생/제왕을 자동으로 길하게 해석하지 않는다.
 *
 * 3.
 * 병/사/묘/절을 자동으로 흉하게 해석하지 않는다.
 *
 * 4.
 * YEAR/MONTH/DAY/HOUR를
 * 가족관계에 직접 1:1 대응하지 않는다.
 *
 * 5.
 * 시간 미상은 HOUR를 생성하지 않는다.
 */
public record TwelveLifeStageAnalysis(

        HeavenlyStem dayMaster,

        TwelveLifeStageEvidenceAnalysis evidenceAnalysis,

        TwelveLifeStageContextAnalysis contextAnalysis,

        boolean completeChart

) {

    public TwelveLifeStageAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "12운성 최종 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                evidenceAnalysis,
                "11-3 TwelveLifeStageEvidenceAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                contextAnalysis,
                "11-4 TwelveLifeStageContextAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 일간 동일성
         * =================================================
         */

        if (
                evidenceAnalysis.dayMaster()
                        != dayMaster

                        || contextAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "11차 12운성 분석들의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 11-4는 반드시 현재 11-3을 참조해야 한다.
         * =================================================
         */

        if (
                contextAnalysis.evidenceAnalysis()
                        != evidenceAnalysis
        ) {

            throw new IllegalArgumentException(
                    "11-4 Context 분석이 현재 11-3 Evidence 분석을 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart 정합성
         * =================================================
         */

        if (
                evidenceAnalysis.completeChart()
                        != completeChart

                        || contextAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "11차 12운성 분석들의 completeChart 상태가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * knownPillarCount 정합성
         * =================================================
         */

        if (
                evidenceAnalysis.knownPillarCount()
                        != contextAnalysis.knownPillarCount()
        ) {

            throw new IllegalArgumentException(
                    "11-3과 11-4의 knownPillarCount가 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * Scope 정합성
         * =================================================
         */

        if (
                completeChart
                        && contextAnalysis.scope()
                        != TwelveLifeStageContextScope.FULL_CHART
        ) {

            throw new IllegalArgumentException(
                    "완전한 4주 원국의 12운성 Scope는 FULL_CHART여야 합니다."
            );
        }

        if (
                !completeChart
                        && contextAnalysis.scope()
                        != TwelveLifeStageContextScope.THREE_PILLAR_ONLY
        ) {

            throw new IllegalArgumentException(
                    "시간 미상 원국의 12운성 Scope는 THREE_PILLAR_ONLY여야 합니다."
            );
        }
    }

    /*
     * =================================================
     * 기본 상태
     * =================================================
     */

    public int knownPillarCount() {

        return evidenceAnalysis
                .knownPillarCount();
    }

    public TwelveLifeStageContextScope scope() {

        return contextAnalysis
                .scope();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }

    public boolean fullChart() {

        return completeChart;
    }

    /*
     * =================================================
     * 원본 Evidence
     * =================================================
     */

    public List<TwelveLifeStageEvidence> evidences() {

        return evidenceAnalysis
                .evidences();
    }

    public Optional<TwelveLifeStageEvidence> evidenceOf(
            EarthlyBranchPosition position
    ) {

        return evidenceAnalysis
                .evidenceOf(
                        position
                );
    }

    /*
     * =================================================
     * Context Evidence
     * =================================================
     */

    public List<TwelveLifeStageContextEvidence> contexts() {

        return contextAnalysis
                .contexts();
    }

    public Optional<TwelveLifeStageContextEvidence> contextOf(
            EarthlyBranchPosition position
    ) {

        return contextAnalysis
                .contextOf(
                        position
                );
    }

    /*
     * =================================================
     * YEAR
     * =================================================
     */

    public TwelveLifeStage yearStage() {

        return evidenceAnalysis
                .yearStage();
    }

    public Optional<TwelveLifeStageContextEvidence> yearContext() {

        return contextAnalysis
                .yearContext();
    }

    /*
     * =================================================
     * MONTH
     * =================================================
     */

    public TwelveLifeStage monthStage() {

        return evidenceAnalysis
                .monthStage();
    }

    public Optional<TwelveLifeStageContextEvidence> monthContext() {

        return contextAnalysis
                .monthContext();
    }

    /*
     * =================================================
     * DAY
     * =================================================
     */

    public TwelveLifeStage dayStage() {

        return evidenceAnalysis
                .dayStage();
    }

    public Optional<TwelveLifeStageContextEvidence> dayContext() {

        return contextAnalysis
                .dayContext();
    }

    /*
     * =================================================
     * HOUR
     * =================================================
     */

    public Optional<TwelveLifeStage> hourStage() {

        return evidenceAnalysis
                .hourStage();
    }

    public Optional<TwelveLifeStageContextEvidence> hourContext() {

        return contextAnalysis
                .hourContext();
    }

    /*
     * =================================================
     * 특정 12운성 조회
     * =================================================
     */

    public List<TwelveLifeStageEvidence> evidencesOf(
            TwelveLifeStage stage
    ) {

        return evidenceAnalysis
                .evidencesOf(
                        stage
                );
    }

    public List<TwelveLifeStageContextEvidence> contextsOf(
            TwelveLifeStage stage
    ) {

        return contextAnalysis
                .contextsOf(
                        stage
                );
    }

    public boolean contains(
            TwelveLifeStage stage
    ) {

        return evidenceAnalysis
                .contains(
                        stage
                );
    }

    public long count(
            TwelveLifeStage stage
    ) {

        return evidenceAnalysis
                .count(
                        stage
                );
    }

    public boolean repeatedStage(
            TwelveLifeStage stage
    ) {

        return contextAnalysis
                .repeatedStage(
                        stage
                );
    }

    /*
     * =================================================
     * 상태 그룹 조회
     * =================================================
     */

    public List<TwelveLifeStageContextEvidence> contextsOf(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return contextAnalysis
                .contextsOf(
                        stateGroup
                );
    }

    public boolean containsStateGroup(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return contextAnalysis
                .containsStateGroup(
                        stateGroup
                );
    }

    public long countStateGroup(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return contextAnalysis
                .countStateGroup(
                        stateGroup
                );
    }

    public boolean repeatedStateGroup(
            TwelveLifeStageStateGroup stateGroup
    ) {

        return contextAnalysis
                .repeatedStateGroup(
                        stateGroup
                );
    }

    /*
     * =================================================
     * 위치 Context 조회
     * =================================================
     */

    public Optional<TwelveLifeStageContextEvidence>
    contextOfPositionType(
            TwelveLifeStagePositionContextType positionContextType
    ) {

        return contextAnalysis
                .contextOfPositionType(
                        positionContextType
                );
    }

    /*
     * =================================================
     * 대표 상태 Shortcut
     *
     * 길흉 Shortcut이 아니다.
     * 단순 존재 확인용이다.
     * =================================================
     */

    public boolean hasChangSheng() {

        return contains(
                TwelveLifeStage.CHANG_SHENG
        );
    }

    public boolean hasMuYu() {

        return contains(
                TwelveLifeStage.MU_YU
        );
    }

    public boolean hasGuanDai() {

        return contains(
                TwelveLifeStage.GUAN_DAI
        );
    }

    public boolean hasLinGuan() {

        return contains(
                TwelveLifeStage.LIN_GUAN
        );
    }

    public boolean hasDiWang() {

        return contains(
                TwelveLifeStage.DI_WANG
        );
    }

    public boolean hasShuai() {

        return contains(
                TwelveLifeStage.SHUAI
        );
    }

    public boolean hasBing() {

        return contains(
                TwelveLifeStage.BING
        );
    }

    public boolean hasSi() {

        return contains(
                TwelveLifeStage.SI
        );
    }

    public boolean hasMu() {

        return contains(
                TwelveLifeStage.MU
        );
    }

    public boolean hasJue() {

        return contains(
                TwelveLifeStage.JUE
        );
    }

    public boolean hasTai() {

        return contains(
                TwelveLifeStage.TAI
        );
    }

    public boolean hasYang() {

        return contains(
                TwelveLifeStage.YANG
        );
    }

    /*
     * =================================================
     * 상태 그룹 Shortcut
     * =================================================
     */

    public boolean hasPreparationState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.PREPARATION
        );
    }

    public boolean hasEmergenceState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.EMERGENCE
        );
    }

    public boolean hasDevelopmentState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.DEVELOPMENT
        );
    }

    public boolean hasPeakActivityState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.PEAK_ACTIVITY
        );
    }

    public boolean hasDecliningActivityState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.DECLINING_ACTIVITY
        );
    }

    public boolean hasClosureStorageState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.CLOSURE_STORAGE
        );
    }

    public boolean hasTransitionState() {

        return containsStateGroup(
                TwelveLifeStageStateGroup.TRANSITION
        );
    }
}