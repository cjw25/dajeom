package com.dajeom.model;

import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.PalacePosition;
import com.dajeom.type.StructureTenGodType;
import com.dajeom.type.TwelveLifeStage;
import com.dajeom.type.TwelveLifeStageStateGroup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 정밀화 12차-5.
 *
 * 궁위 분석 최종 Aggregate.
 *
 * =================================================
 *
 * 포함 단계:
 *
 * 12-1 궁위 위치 기준
 * 12-2 천간·지지 Palace Evidence
 * 12-3 Palace × 십성
 * 12-4 Palace Context
 *
 * =================================================
 *
 * 이 객체가 정밀화 12차의 공식 최종 결과다.
 *
 * 이후:
 *
 * 13차 대운
 * 14차 세운
 * 15차 월운
 * 16차 운 통합
 * 18차 생활영역
 * 19차 고민 분석
 * 20차 AI Grounding
 *
 * 에서 궁위 정보가 필요하면
 * 이 Aggregate를 기준으로 접근한다.
 *
 * =================================================
 *
 * 중요:
 *
 * PalaceAnalysis는 궁위를
 * 가족관계에 기계적으로 고정하지 않는다.
 *
 * 예:
 *
 * YEAR = 부모
 * MONTH = 형제
 * DAY = 배우자
 * HOUR = 자녀
 *
 * 로 확정하지 않는다.
 *
 * 궁위는 위치 Context이며
 * 실제 생활영역 해석은
 * 십성·관계·운 흐름과 함께 결정한다.
 */
public record PalaceAnalysis(

        HeavenlyStem dayMaster,

        PalacePositionAnalysis positionAnalysis,

        PalaceTenGodAnalysis tenGodAnalysis,

        PalaceContextAnalysis contextAnalysis,

        boolean completeChart

) {

    public PalaceAnalysis {

        Objects.requireNonNull(
                dayMaster,
                "궁위 최종 분석에는 일간이 필요합니다."
        );

        Objects.requireNonNull(
                positionAnalysis,
                "12-2 PalacePositionAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                tenGodAnalysis,
                "12-3 PalaceTenGodAnalysis가 필요합니다."
        );

        Objects.requireNonNull(
                contextAnalysis,
                "12-4 PalaceContextAnalysis가 필요합니다."
        );

        /*
         * =================================================
         * 일간 정합성
         * =================================================
         */

        if (
                positionAnalysis.dayMaster()
                        != dayMaster

                        || tenGodAnalysis.dayMaster()
                        != dayMaster

                        || contextAnalysis.dayMaster()
                        != dayMaster
        ) {

            throw new IllegalArgumentException(
                    "12차 궁위 분석들의 일간이 서로 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 단계 참조 정합성
         * =================================================
         */

        if (
                tenGodAnalysis.positionAnalysis()
                        != positionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "12-3 PalaceTenGodAnalysis가 현재 12-2 결과를 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.positionAnalysis()
                        != positionAnalysis
        ) {

            throw new IllegalArgumentException(
                    "12-4 PalaceContextAnalysis가 현재 12-2 결과를 참조하지 않습니다."
            );
        }

        if (
                contextAnalysis.tenGodAnalysis()
                        != tenGodAnalysis
        ) {

            throw new IllegalArgumentException(
                    "12-4 PalaceContextAnalysis가 현재 12-3 결과를 참조하지 않습니다."
            );
        }

        /*
         * =================================================
         * completeChart 정합성
         * =================================================
         */

        if (
                positionAnalysis.completeChart()
                        != completeChart

                        || tenGodAnalysis.completeChart()
                        != completeChart

                        || contextAnalysis.completeChart()
                        != completeChart
        ) {

            throw new IllegalArgumentException(
                    "12차 궁위 분석들의 completeChart 상태가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * knownPalaceCount 정합성
         * =================================================
         */

        int knownPalaceCount =
                positionAnalysis
                        .knownPalaceCount();

        if (
                tenGodAnalysis.knownPalaceCount()
                        != knownPalaceCount

                        || contextAnalysis.knownPalaceCount()
                        != knownPalaceCount
        ) {

            throw new IllegalArgumentException(
                    "12차 궁위 분석들의 knownPalaceCount가 일치하지 않습니다."
            );
        }

        /*
         * =================================================
         * 시간 미상 무결성
         * =================================================
         */

        if (
                !completeChart
                        && (
                        positionAnalysis.containsPosition(
                                PalacePosition.HOUR
                        )

                                || !tenGodAnalysis
                                .hourEvidences()
                                .isEmpty()

                                || contextAnalysis
                                .hourContext()
                                .isPresent()
                )
        ) {

            throw new IllegalArgumentException(
                    "시간 미상 원국에 HOUR Palace 분석이 존재합니다."
            );
        }
    }

    /*
     * =================================================
     * 선행 분석 접근
     * =================================================
     */

    public TwelveLifeStageAnalysis twelveLifeStageAnalysis() {

        return contextAnalysis
                .twelveLifeStageAnalysis();
    }

    public PalaceCrossContext crossContext() {

        return contextAnalysis
                .crossContext();
    }

    /*
     * =================================================
     * 기본 상태
     * =================================================
     */

    public int knownPalaceCount() {

        return positionAnalysis
                .knownPalaceCount();
    }

    public boolean hourUnknown() {

        return !completeChart;
    }

    public boolean fullChart() {

        return completeChart;
    }

    public List<PalacePosition> knownPositions() {

        return positionAnalysis
                .knownPositions();
    }

    /*
     * =================================================
     * Palace Context 조회
     * =================================================
     */

    public Optional<PalaceContextEvidence> contextOf(
            PalacePosition position
    ) {

        return contextAnalysis
                .contextOf(
                        position
                );
    }

    public PalaceContextEvidence yearContext() {

        return contextAnalysis
                .yearContext();
    }

    public PalaceContextEvidence monthContext() {

        return contextAnalysis
                .monthContext();
    }

    public PalaceContextEvidence dayContext() {

        return contextAnalysis
                .dayContext();
    }

    public Optional<PalaceContextEvidence> hourContext() {

        return contextAnalysis
                .hourContext();
    }

    /*
     * =================================================
     * 천간 / 지지 조회
     * =================================================
     */

    public PalaceStemEvidence yearStemEvidence() {

        return positionAnalysis
                .yearStemEvidence();
    }

    public PalaceBranchEvidence yearBranchEvidence() {

        return positionAnalysis
                .yearBranchEvidence();
    }

    public PalaceStemEvidence monthStemEvidence() {

        return positionAnalysis
                .monthStemEvidence();
    }

    public PalaceBranchEvidence monthBranchEvidence() {

        return positionAnalysis
                .monthBranchEvidence();
    }

    public PalaceStemEvidence dayMasterAnchor() {

        return positionAnalysis
                .dayStemEvidence();
    }

    public PalaceBranchEvidence dayBranchEvidence() {

        return positionAnalysis
                .dayBranchEvidence();
    }

    public Optional<PalaceStemEvidence> hourStemEvidence() {

        return positionAnalysis
                .hourStemEvidence();
    }

    public Optional<PalaceBranchEvidence> hourBranchEvidence() {

        return positionAnalysis
                .hourBranchEvidence();
    }

    /*
     * =================================================
     * 궁위 × 십성 조회
     * =================================================
     */

    public List<PalaceTenGodEvidence> tenGodEvidencesOf(
            PalacePosition position
    ) {

        return tenGodAnalysis
                .evidencesOf(
                        position
                );
    }

    public List<PalaceTenGodEvidence> tenGodEvidencesOf(
            StructureTenGodType tenGodType
    ) {

        return tenGodAnalysis
                .evidencesOf(
                        tenGodType
                );
    }

    public boolean containsTenGod(

            PalacePosition position,

            StructureTenGodType tenGodType

    ) {

        return tenGodAnalysis
                .contains(
                        position,
                        tenGodType
                );
    }

    public Optional<PalaceTenGodEvidence> visibleTenGodOf(
            PalacePosition position
    ) {

        return tenGodAnalysis
                .visibleEvidenceOf(
                        position
                );
    }

    public Optional<PalaceTenGodEvidence> mainHiddenTenGodOf(
            PalacePosition position
    ) {

        return tenGodAnalysis
                .mainHiddenEvidenceOf(
                        position
                );
    }

    /*
     * =================================================
     * 12운성 조회
     * =================================================
     */

    public TwelveLifeStage twelveLifeStageOf(
            PalacePosition position
    ) {

        return contextOf(
                position
        )
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "Palace Context가 존재하지 않습니다: "
                                                + position
                                )
                )
                .twelveLifeStage();
    }

    public List<PalaceContextEvidence> contextsOf(
            TwelveLifeStage stage
    ) {

        return contextAnalysis
                .contextsOf(
                        stage
                );
    }

    public List<PalaceContextEvidence> contextsOf(
            TwelveLifeStageStateGroup stateGroup
    ) {

        if (
                stateGroup == null
        ) {

            return List.of();
        }

        return contextAnalysis
                .contexts()
                .stream()
                .filter(
                        context ->
                                context
                                        .twelveLifeStageContext()
                                        .stateGroup()
                                        == stateGroup
                )
                .toList();
    }

    /*
     * =================================================
     * 궁위 관계
     * =================================================
     */

    public List<PalaceRelationReference> relationsBetween(

            PalacePosition first,

            PalacePosition second

    ) {

        return contextAnalysis
                .relationsBetween(
                        first,
                        second
                );
    }

    public List<PalaceRelationReference> relationsOf(
            PalacePosition position
    ) {

        return crossContext()
                .relationsOf(
                        position
                );
    }

    public List<PalaceRelationReference> stemRelationsOf(
            PalacePosition position
    ) {

        return crossContext()
                .stemRelationsOf(
                        position
                );
    }

    public List<PalaceRelationReference> branchRelationsOf(
            PalacePosition position
    ) {

        return crossContext()
                .branchRelationsOf(
                        position
                );
    }

    /*
     * =================================================
     * 합화
     * =================================================
     */

    public List<PalaceTransformationReference> transformationsOf(
            PalacePosition position
    ) {

        return contextAnalysis
                .transformationsOf(
                        position
                );
    }

    public boolean hasActivatedTransformation(
            PalacePosition position
    ) {

        return transformationsOf(
                position
        )
                .stream()
                .anyMatch(
                        PalaceTransformationReference
                                ::activated
                );
    }

    public boolean hasReducedOriginalFunction(
            PalacePosition position
    ) {

        return transformationsOf(
                position
        )
                .stream()
                .anyMatch(
                        PalaceTransformationReference
                                ::originalFunctionReduced
                );
    }

    /*
     * =================================================
     * DAY Anchor
     * =================================================
     */

    public boolean dayStemExcludedFromExternalTenGod() {

        return tenGodAnalysis
                .dayStemExcludedFromExternalTenGod();
    }
}