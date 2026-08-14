package com.dajeom.engine;

import com.dajeom.analyzer.PalaceContextAnalyzer;
import com.dajeom.analyzer.PalacePositionAnalyzer;
import com.dajeom.analyzer.PalaceTenGodAnalyzer;
import com.dajeom.model.FourPillars;
import com.dajeom.model.PalaceAnalysis;
import com.dajeom.model.PalaceContextAnalysis;
import com.dajeom.model.PalaceCrossContext;
import com.dajeom.model.PalacePositionAnalysis;
import com.dajeom.model.PalaceTenGodAnalysis;
import com.dajeom.model.TwelveLifeStageAnalysis;
import com.dajeom.resolver.PalaceTenGodSourceResolver;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 정밀화 12차-5.
 *
 * 궁위 분석 최종 Engine.
 *
 * =================================================
 *
 * 공식 실행 흐름:
 *
 * FourPillars
 *
 * ↓
 *
 * 12-2
 * PalacePositionAnalyzer
 *
 * ↓
 *
 * 12-3
 * PalaceTenGodAnalyzer
 *
 * ↓
 *
 * 12-4
 * PalaceContextAnalyzer
 *
 * ↓
 *
 * PalaceAnalysis
 *
 * =================================================
 *
 * 11차 TwelveLifeStageAnalysis는
 * 이미 계산된 선행 분석 결과를 받는다.
 *
 * 12차가 11차를 다시 계산하지 않는다.
 *
 * =================================================
 *
 * PalaceTenGodSourceResolver:
 *
 * 기존 2차 지장간
 * +
 * 기존 십성 계산
 *
 * 을 12차에 연결하는 Boundary.
 *
 * =================================================
 *
 * PalaceCrossContext:
 *
 * 기존 8차 관계
 * +
 * 기존 9차 합화
 *
 * 를 궁위 위치로 정규화한 Boundary.
 */
@Component
public class PalaceAnalysisEngine {

    private final PalacePositionAnalyzer
            palacePositionAnalyzer;

    private final PalaceTenGodAnalyzer
            palaceTenGodAnalyzer;

    private final PalaceContextAnalyzer
            palaceContextAnalyzer;

    public PalaceAnalysisEngine(

            PalacePositionAnalyzer
                    palacePositionAnalyzer,

            PalaceTenGodAnalyzer
                    palaceTenGodAnalyzer,

            PalaceContextAnalyzer
                    palaceContextAnalyzer

    ) {

        this.palacePositionAnalyzer =
                Objects.requireNonNull(
                        palacePositionAnalyzer,
                        "PalacePositionAnalyzer가 필요합니다."
                );

        this.palaceTenGodAnalyzer =
                Objects.requireNonNull(
                        palaceTenGodAnalyzer,
                        "PalaceTenGodAnalyzer가 필요합니다."
                );

        this.palaceContextAnalyzer =
                Objects.requireNonNull(
                        palaceContextAnalyzer,
                        "PalaceContextAnalyzer가 필요합니다."
                );
    }

    /**
     * =================================================
     * 공식 12차 분석 진입점
     * =================================================
     *
     * 2차/8차/9차 Adapter가 모두 준비된
     * 완전한 분석 방식.
     */
    public PalaceAnalysis analyze(

            FourPillars fourPillars,

            TwelveLifeStageAnalysis
                    twelveLifeStageAnalysis,

            PalaceTenGodSourceResolver
                    tenGodSourceResolver,

            PalaceCrossContext
                    crossContext

    ) {

        validateInputs(
                fourPillars,
                twelveLifeStageAnalysis,
                tenGodSourceResolver,
                crossContext
        );

        /*
         * =================================================
         * 12-2
         *
         * 궁위 천간·지지 Evidence
         * =================================================
         */

        PalacePositionAnalysis positionAnalysis =
                palacePositionAnalyzer
                        .analyze(
                                fourPillars
                        );

        /*
         * =================================================
         * 선행 11차 결과와 동일 원국인지 검증
         * =================================================
         */

        validateTwelveLifeStageAnalysis(
                positionAnalysis,
                twelveLifeStageAnalysis
        );

        /*
         * =================================================
         * PalaceCrossContext도 동일한
         * completeChart 상태를 가져야 한다.
         * =================================================
         */

        if (
                crossContext.completeChart()
                        != positionAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "궁위 원국과 PalaceCrossContext의 completeChart 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 12-3
         *
         * 궁위 × 십성
         * =================================================
         */

        PalaceTenGodAnalysis tenGodAnalysis =
                palaceTenGodAnalyzer
                        .analyze(
                                positionAnalysis,
                                tenGodSourceResolver
                        );

        /*
         * =================================================
         * 12-4
         *
         * 궁위 Context 결합
         * =================================================
         */

        PalaceContextAnalysis contextAnalysis =
                palaceContextAnalyzer
                        .analyze(
                                positionAnalysis,
                                tenGodAnalysis,
                                twelveLifeStageAnalysis,
                                crossContext
                        );

        /*
         * =================================================
         * 12-5
         *
         * 최종 Aggregate
         * =================================================
         */

        return new PalaceAnalysis(
                positionAnalysis.dayMaster(),
                positionAnalysis,
                tenGodAnalysis,
                contextAnalysis,
                positionAnalysis.completeChart()
        );
    }

    /**
     * =================================================
     * 관계/합화 미연결 상태
     * =================================================
     *
     * 2차 지장간/십성 Resolver는 준비되어 있지만
     * 8차/9차 -> Palace Adapter가 아직 준비되지 않은
     * 경우에만 사용한다.
     *
     * 실제:
     *
     * - Palace 위치
     * - 궁위 십성
     * - 12운성
     *
     * 은 정상 반영한다.
     *
     * 관계/합화만 중립 처리한다.
     */
    public PalaceAnalysis analyzeConservatively(

            FourPillars fourPillars,

            TwelveLifeStageAnalysis
                    twelveLifeStageAnalysis,

            PalaceTenGodSourceResolver
                    tenGodSourceResolver

    ) {

        Objects.requireNonNull(
                fourPillars,
                "궁위 분석에는 FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                twelveLifeStageAnalysis,
                "11차 TwelveLifeStageAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodSourceResolver,
                "PalaceTenGodSourceResolver가 필요합니다."
        );

        /*
         * completeChart를 추측하지 않기 위해
         * 먼저 12-2를 실행한다.
         */

        PalacePositionAnalysis positionAnalysis =
                palacePositionAnalyzer
                        .analyze(
                                fourPillars
                        );

        validateTwelveLifeStageAnalysis(
                positionAnalysis,
                twelveLifeStageAnalysis
        );

        PalaceTenGodAnalysis tenGodAnalysis =
                palaceTenGodAnalyzer
                        .analyze(
                                positionAnalysis,
                                tenGodSourceResolver
                        );

        PalaceContextAnalysis contextAnalysis =
                palaceContextAnalyzer
                        .analyze(
                                positionAnalysis,
                                tenGodAnalysis,
                                twelveLifeStageAnalysis,
                                PalaceCrossContext.neutral(
                                        positionAnalysis.completeChart()
                                )
                        );

        return new PalaceAnalysis(
                positionAnalysis.dayMaster(),
                positionAnalysis,
                tenGodAnalysis,
                contextAnalysis,
                positionAnalysis.completeChart()
        );
    }

    /*
     * =================================================
     * 입력 검증
     * =================================================
     */

    private void validateInputs(

            FourPillars fourPillars,

            TwelveLifeStageAnalysis
                    twelveLifeStageAnalysis,

            PalaceTenGodSourceResolver
                    tenGodSourceResolver,

            PalaceCrossContext
                    crossContext

    ) {

        Objects.requireNonNull(
                fourPillars,
                "궁위 분석에는 FourPillars가 필요합니다."
        );

        Objects.requireNonNull(
                twelveLifeStageAnalysis,
                "11차 TwelveLifeStageAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodSourceResolver,
                "PalaceTenGodSourceResolver가 필요합니다."
        );

        Objects.requireNonNull(
                crossContext,
                "PalaceCrossContext가 필요합니다."
        );
    }

    /*
     * =================================================
     * 11차 ↔ 12차 정합성
     * =================================================
     */

    private void validateTwelveLifeStageAnalysis(

            PalacePositionAnalysis positionAnalysis,

            TwelveLifeStageAnalysis
                    twelveLifeStageAnalysis

    ) {

        if (
                positionAnalysis.dayMaster()
                        != twelveLifeStageAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "11차 12운성과 12차 궁위 분석의 일간이 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.completeChart()
                        != twelveLifeStageAnalysis.completeChart()
        ) {

            throw new IllegalArgumentException(
                    "11차 12운성과 12차 궁위 분석의 completeChart 상태가 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.knownPalaceCount()
                        != twelveLifeStageAnalysis.knownPillarCount()
        ) {

            throw new IllegalArgumentException(
                    "11차와 12차의 알려진 지주 개수가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 실제 지지까지 동일한지 검사
         *
         * 단순히 일간/개수만 같고
         * 서로 다른 원국이 섞이는 것을 막는다.
         * =================================================
         */

        if (
                positionAnalysis.yearBranch()
                        != twelveLifeStageAnalysis
                        .yearContext()
                        .orElseThrow()
                        .branch()
        ) {

            throw new IllegalArgumentException(
                    "11차와 12차의 년지가 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.monthBranch()
                        != twelveLifeStageAnalysis
                        .monthContext()
                        .orElseThrow()
                        .branch()
        ) {

            throw new IllegalArgumentException(
                    "11차와 12차의 월지가 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.dayBranch()
                        != twelveLifeStageAnalysis
                        .dayContext()
                        .orElseThrow()
                        .branch()
        ) {

            throw new IllegalArgumentException(
                    "11차와 12차의 일지가 일치하지 않습니다."
            );
        }

        if (
                positionAnalysis.completeChart()
        ) {

            if (
                    positionAnalysis
                            .hourBranch()
                            .orElseThrow()

                            != twelveLifeStageAnalysis
                            .hourContext()
                            .orElseThrow()
                            .branch()
            ) {

                throw new IllegalArgumentException(
                        "11차와 12차의 시지가 일치하지 않습니다."
                );
            }
        }
    }
}