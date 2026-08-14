package com.dajeom.analyzer;

import com.dajeom.model.PalaceBranchEvidence;
import com.dajeom.model.PalaceContextAnalysis;
import com.dajeom.model.PalaceContextEvidence;
import com.dajeom.model.PalaceCrossContext;
import com.dajeom.model.PalacePositionAnalysis;
import com.dajeom.model.PalaceStemEvidence;
import com.dajeom.model.PalaceTenGodAnalysis;
import com.dajeom.model.PalaceTenGodEvidence;
import com.dajeom.model.TwelveLifeStageAnalysis;
import com.dajeom.model.TwelveLifeStageContextEvidence;
import com.dajeom.rule.PalacePositionRule;
import com.dajeom.type.EarthlyBranchPosition;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.PalacePositionContextType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * 정밀화 12차-4.
 *
 * 궁위 Context 결합 Analyzer.
 *
 * =================================================
 *
 * 12-2 위치
 * +
 * 12-3 십성
 * +
 * 11차 12운성
 * +
 * 8차 관계 Boundary
 * +
 * 9차 합화 Boundary
 *
 * 를 Palace 단위로 결합한다.
 *
 * =================================================
 *
 * 기존 분석을 재계산하지 않는다.
 */
@Component
public class PalaceContextAnalyzer {

    private final PalacePositionRule
            palacePositionRule;

    public PalaceContextAnalyzer(
            PalacePositionRule palacePositionRule
    ) {

        this.palacePositionRule =
                Objects.requireNonNull(
                        palacePositionRule,
                        "PalacePositionRule이 필요합니다."
                );
    }

    /**
     * 공식 12-4 API.
     */
    public PalaceContextAnalysis analyze(

            PalacePositionAnalysis positionAnalysis,

            PalaceTenGodAnalysis tenGodAnalysis,

            TwelveLifeStageAnalysis twelveLifeStageAnalysis,

            PalaceCrossContext crossContext

    ) {

        Objects.requireNonNull(
                positionAnalysis,
                "12-2 PalacePositionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "12-3 PalaceTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                twelveLifeStageAnalysis,
                "11차 TwelveLifeStageAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                crossContext,
                "PalaceCrossContext가 필요합니다."
        );

        validateInputs(
                positionAnalysis,
                tenGodAnalysis,
                twelveLifeStageAnalysis,
                crossContext
        );

        List<PalaceContextEvidence> contexts =
                new ArrayList<>();

        for (
                PalacePosition position
                : positionAnalysis.knownPositions()
        ) {

            PalaceStemEvidence stemEvidence =
                    positionAnalysis
                            .stemEvidenceOf(
                                    position
                            )
                            .orElseThrow();

            PalaceBranchEvidence branchEvidence =
                    positionAnalysis
                            .branchEvidenceOf(
                                    position
                            )
                            .orElseThrow();

            PalacePositionContextType positionContext =
                    stemEvidence
                            .positionContext();

            List<PalaceTenGodEvidence> tenGodEvidences =
                    tenGodAnalysis
                            .evidencesOf(
                                    position
                            );

            /*
             * =================================================
             * 11차 12운성 연결
             * =================================================
             */

            EarthlyBranchPosition branchPosition =
                    palacePositionRule
                            .toEarthlyBranchPosition(
                                    position
                            );

            TwelveLifeStageContextEvidence lifeStageContext =
                    twelveLifeStageAnalysis
                            .contextAnalysis()
                            .contextOf(
                                    branchPosition
                            )
                            .orElseThrow(
                                    () ->
                                            new IllegalStateException(
                                                    "Palace에 대응하는 12운성 Context가 없습니다: "
                                                            + position
                                            )
                            );

            /*
             * =================================================
             * 8차 관계
             * =================================================
             */

            var stemRelations =
                    crossContext
                            .stemRelationsOf(
                                    position
                            );

            var branchRelations =
                    crossContext
                            .branchRelationsOf(
                                    position
                            );

            /*
             * =================================================
             * 9차 합화
             * =================================================
             */

            var transformations =
                    crossContext
                            .transformationsOf(
                                    position
                            );

            contexts.add(
                    new PalaceContextEvidence(
                            position,
                            positionContext,
                            stemEvidence,
                            branchEvidence,
                            tenGodEvidences,
                            lifeStageContext,
                            stemRelations,
                            branchRelations,
                            transformations
                    )
            );
        }

        /*
         * 출력 안정성을 위한 정렬.
         *
         * 중요도 순위가 아니다.
         */
        contexts.sort(
                Comparator.comparingInt(
                        context ->
                                context.position()
                                        .order()
                )
        );

        return new PalaceContextAnalysis(
                positionAnalysis.dayMaster(),
                positionAnalysis,
                tenGodAnalysis,
                twelveLifeStageAnalysis,
                crossContext,
                contexts,
                positionAnalysis.knownPalaceCount(),
                positionAnalysis.completeChart()
        );
    }

    /**
     * 관계/합화 Adapter가 아직 연결되지 않은 경우.
     *
     * 12운성과 십성은 정상 반영하고
     * 8차·9차 궁위 연결만 비워둔다.
     */
    public PalaceContextAnalysis analyzeConservatively(

            PalacePositionAnalysis positionAnalysis,

            PalaceTenGodAnalysis tenGodAnalysis,

            TwelveLifeStageAnalysis twelveLifeStageAnalysis

    ) {

        return analyze(
                positionAnalysis,
                tenGodAnalysis,
                twelveLifeStageAnalysis,
                PalaceCrossContext.neutral(
                        positionAnalysis.completeChart()
                )
        );
    }

    private void validateInputs(

            PalacePositionAnalysis positionAnalysis,

            PalaceTenGodAnalysis tenGodAnalysis,

            TwelveLifeStageAnalysis twelveLifeStageAnalysis,

            PalaceCrossContext crossContext

    ) {

        if (
                tenGodAnalysis.positionAnalysis()
                        != positionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "12-3 PalaceTenGodAnalysis가 현재 12-2 분석을 참조하지 않습니다."
            );
        }

        if (
                positionAnalysis.dayMaster()
                        != tenGodAnalysis.dayMaster()

                        || positionAnalysis.dayMaster()
                        != twelveLifeStageAnalysis.dayMaster()
        ) {

            throw new IllegalArgumentException(
                    "12-4 입력 분석들의 일간이 서로 다릅니다."
            );
        }

        boolean completeChart =
                positionAnalysis.completeChart();

        if (
                tenGodAnalysis.completeChart()
                        != completeChart

                        || twelveLifeStageAnalysis.completeChart()
                        != completeChart

                        || crossContext.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "12-4 입력 분석들의 completeChart 상태가 서로 다릅니다."
            );
        }
    }
}