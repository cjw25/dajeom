package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.RootAnalysis;
import com.dajeom.model.RootElementPowerAnalysis;
import com.dajeom.model.RootElementPowerEvidence;
import com.dajeom.model.RootEvidence;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.RootSourcePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 3차-4.
 *
 * 2차에서 계산한 통근 정보를
 * 오행 세력 계산용 데이터로 변환한다.
 *
 * 매우 중요한 원칙:
 *
 * 통근 Evidence의 hiddenStemWeight를
 * 오행 세력에 그대로 다시 더하지 않는다.
 *
 * 이유:
 *
 * 지장간 자체 세력은 이미 3-2에서
 * 계산되었기 때문이다.
 *
 * 이 단계의 값은:
 *
 * "그 지장간이 표면 천간의 뿌리로
 * 얼마나 실제 활성화되어 있는가"
 *
 * 를 표현하는 통근 활성 지수다.
 *
 * 또한 같은 지장간 뿌리가
 * 같은 오행의 여러 표면 천간에 연결되더라도
 * 동일한 근원을 중복 집계하지 않는다.
 */
@Component
public class RootElementPowerAnalyzer {

    public RootElementPowerAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        /*
         * 동일한 지장간 근원이
         * 여러 표면 천간에 연결되어도
         * 오행 세력에서는 한 번만 사용한다.
         *
         * 같은 source에 대해:
         *
         * 직접 통근
         * >
         * 동기 보조근
         *
         * 순으로 더 강한 Evidence 하나를 남긴다.
         */
        Map<RootSourceKey, RootElementPowerEvidence>
                uniqueEvidenceMap =
                new LinkedHashMap<>();

        for (
                RootAnalysis rootAnalysis
                : basePowerAnalysis
                .visibleStemRootAnalyses()
                .values()
        ) {

            FiveElement targetElement =
                    elementOfStem(
                            rootAnalysis.targetStem()
                    );

            for (
                    RootEvidence rootEvidence
                    : rootAnalysis.evidences()
            ) {

                RootElementPowerEvidence converted =
                        convertEvidence(
                                targetElement,
                                rootEvidence
                        );

                RootSourceKey key =
                        new RootSourceKey(
                                targetElement,
                                rootEvidence.sourcePosition(),
                                rootEvidence.sourceBranch(),
                                rootEvidence.hiddenStem()
                        );

                uniqueEvidenceMap.merge(
                        key,
                        converted,
                        this::chooseStrongerEvidence
                );
            }
        }

        List<RootElementPowerEvidence> evidences =
                List.copyOf(
                        uniqueEvidenceMap.values()
                );

        Map<FiveElement, Integer>
                directRootWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                supportingRootWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                totalRootWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                rootSourceCounts =
                createZeroElementMap();

        Map<FiveElement, Integer>
                monthRootSourceCounts =
                createZeroElementMap();

        Map<FiveElement, Integer>
                mainQiRootSourceCounts =
                createZeroElementMap();

        for (
                RootElementPowerEvidence evidence
                : evidences
        ) {

            FiveElement element =
                    evidence.targetElement();

            /*
             * 동일 source는 이미 dedup 됐으므로
             * 여기서는 안전하게 한 번만 합산한다.
             */
            totalRootWeights.compute(
                    element,
                    (
                            key,
                            current
                    ) ->
                            safeValue(
                                    current
                            )
                                    + evidence.effectiveRootWeight()
            );

            if (
                    evidence.directRoot()
            ) {

                directRootWeights.compute(
                        element,
                        (
                                key,
                                current
                        ) ->
                                safeValue(
                                        current
                                )
                                        + evidence.effectiveRootWeight()
                );

            } else {

                supportingRootWeights.compute(
                        element,
                        (
                                key,
                                current
                        ) ->
                                safeValue(
                                        current
                                )
                                        + evidence.effectiveRootWeight()
                );
            }

            rootSourceCounts.compute(
                    element,
                    (
                            key,
                            current
                    ) ->
                            safeValue(
                                    current
                            )
                                    + 1
            );

            if (
                    evidence.monthRoot()
            ) {

                monthRootSourceCounts.compute(
                        element,
                        (
                                key,
                                current
                        ) ->
                                safeValue(
                                        current
                                )
                                        + 1
                );
            }

            if (
                    evidence.mainQiRoot()
            ) {

                mainQiRootSourceCounts.compute(
                        element,
                        (
                                key,
                                current
                        ) ->
                                safeValue(
                                        current
                                )
                                        + 1
                );
            }
        }

        RootAnalysis dayMasterRootAnalysis =
                basePowerAnalysis
                        .dayMasterRootAnalysis();

        HeavenlyStem dayMaster =
                basePowerAnalysis
                        .dayMaster();

        FiveElement dayMasterElement =
                elementOfStem(
                        dayMaster
                );

        return new RootElementPowerAnalysis(
                dayMaster,
                dayMasterElement,

                dayMasterRootAnalysis.rooted(),

                dayMasterRootAnalysis.directRooted(),

                dayMasterRootAnalysis.monthRooted(),

                dayMasterRootAnalysis.mainHiddenStemRooted(),

                dayMasterRootAnalysis.strongMonthMainRoot(),

                dayMasterRootAnalysis.totalRootWeight(),

                evidences.size(),

                evidences,

                directRootWeights,

                supportingRootWeights,

                totalRootWeights,

                rootSourceCounts,

                monthRootSourceCounts,

                mainQiRootSourceCounts
        );
    }

    private RootElementPowerEvidence convertEvidence(

            FiveElement targetElement,

            RootEvidence rootEvidence

    ) {

        /*
         * 통근 관계는 반드시 같은 오행 안에서
         * 형성돼야 한다.
         *
         * RootAnalyzer 쪽 데이터가 잘못되었을 경우
         * 여기서 즉시 잡는다.
         */
        FiveElement hiddenElement =
                elementOfStem(
                        rootEvidence.hiddenStem()
                );

        if (
                targetElement
                        != hiddenElement
        ) {

            throw new IllegalStateException(
                    "통근 대상 오행과 지장간 오행이 일치하지 않습니다."
            );
        }

        return new RootElementPowerEvidence(
                targetElement,
                rootEvidence.targetStem(),
                rootEvidence.sourcePosition(),
                rootEvidence.sourceBranch(),
                rootEvidence.hiddenStem(),
                rootEvidence.hiddenStemRole(),
                rootEvidence.relationType(),
                rootEvidence.hiddenStemWeight(),
                rootEvidence.positionWeightRate(),
                rootEvidence.relationWeightRate(),
                rootEvidence.effectiveWeight()
        );
    }

    /**
     * 동일한 지장간 source에 대해
     * 여러 통근 Evidence가 생겼을 때
     * 가장 강한 하나만 남긴다.
     *
     * 예:
     *
     * 甲과 乙이 모두 표면에 있고
     * 卯 속 乙이 있을 경우
     *
     * 甲 -> 乙 = SAME_ELEMENT
     * 乙 -> 乙 = SAME_STEM
     *
     * 木 세력에 같은 卯 속 乙을
     * 두 번 더하면 안 된다.
     *
     * 따라서 SAME_STEM 쪽 하나만 보존한다.
     */
    private RootElementPowerEvidence chooseStrongerEvidence(

            RootElementPowerEvidence first,

            RootElementPowerEvidence second

    ) {

        if (
                first.effectiveRootWeight()
                        > second.effectiveRootWeight()
        ) {

            return first;
        }

        if (
                second.effectiveRootWeight()
                        > first.effectiveRootWeight()
        ) {

            return second;
        }

        /*
         * 유효 세력이 동일하면
         * 직접 통근을 우선한다.
         */
        if (
                first.directRoot()
                        && !second.directRoot()
        ) {

            return first;
        }

        if (
                second.directRoot()
                        && !first.directRoot()
        ) {

            return second;
        }

        return first;
    }

    private Map<FiveElement, Integer>
    createZeroElementMap() {

        EnumMap<FiveElement, Integer> result =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            result.put(
                    element,
                    0
            );
        }

        return result;
    }

    private int safeValue(
            Integer value
    ) {

        return value == null
                ? 0
                : value;
    }

    /**
     * 천간 -> 오행.
     *
     * 甲乙 木
     * 丙丁 火
     * 戊己 土
     * 庚辛 金
     * 壬癸 水
     */
    private FiveElement elementOfStem(
            HeavenlyStem stem
    ) {

        Objects.requireNonNull(
                stem,
                "천간이 필요합니다."
        );

        int index =
                findStemIndex(
                        stem
                );

        return switch (
                index / 2
                ) {

            case 0 ->
                    FiveElement.WOOD;

            case 1 ->
                    FiveElement.FIRE;

            case 2 ->
                    FiveElement.EARTH;

            case 3 ->
                    FiveElement.METAL;

            case 4 ->
                    FiveElement.WATER;

            default ->
                    throw new IllegalStateException(
                            "천간 오행을 판정할 수 없습니다: "
                                    + stem
                    );
        };
    }

    private int findStemIndex(
            HeavenlyStem stem
    ) {

        for (
                int index = 0;
                index < 10;
                index++
        ) {

            if (
                    HeavenlyStem.fromIndex(
                            index
                    )
                            == stem
            ) {

                return index;
            }
        }

        throw new IllegalArgumentException(
                "알 수 없는 천간입니다: "
                        + stem
        );
    }

    /**
     * 동일 지장간 뿌리의
     * 중복 계산을 방지하기 위한 내부 key.
     */
    private record RootSourceKey(

            FiveElement targetElement,

            RootSourcePosition sourcePosition,

            EarthlyBranch sourceBranch,

            HeavenlyStem hiddenStem

    ) {

        private RootSourceKey {

            Objects.requireNonNull(
                    targetElement
            );

            Objects.requireNonNull(
                    sourcePosition
            );

            Objects.requireNonNull(
                    sourceBranch
            );

            Objects.requireNonNull(
                    hiddenStem
            );
        }
    }
}