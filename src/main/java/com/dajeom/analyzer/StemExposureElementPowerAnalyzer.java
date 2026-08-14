package com.dajeom.analyzer;

import com.dajeom.model.BasePowerAnalysis;
import com.dajeom.model.HiddenElementPowerAnalysis;
import com.dajeom.model.StemExposureAnalysis;
import com.dajeom.model.StemExposureElementPowerAnalysis;
import com.dajeom.model.StemExposureElementPowerEvidence;
import com.dajeom.model.StemExposureEvidence;
import com.dajeom.type.EarthlyBranch;
import com.dajeom.type.FiveElement;
import com.dajeom.type.HeavenlyStem;
import com.dajeom.type.HiddenStemRole;
import com.dajeom.type.StemExposurePosition;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 3차-5.
 *
 * 투간 정보를 오행 세력 계산용
 * 활성 데이터로 변환한다.
 *
 * 중요한 원칙:
 *
 * 1.
 * 지장간 자체 세력은 3-2에서
 * 이미 계산되어 있다.
 *
 * 따라서 투간되었다고
 * hiddenStemWeight를 다시
 * 최종 세력에 그대로 더하지 않는다.
 *
 * 2.
 * 같은 지장간 source가
 * 여러 천간 위치로 투출해도
 * source 자체는 한 번만 계산한다.
 *
 * 예:
 *
 * 월지 申 속 庚
 *
 * 년간 庚
 * 월간 庚
 *
 * 두 군데 드러났다고 해서
 * 申 속 庚 60을 120으로 계산하지 않는다.
 *
 * 대신:
 *
 * source 세력 = 60
 * visibleStemCount = 2
 * repeatedExposure = true
 *
 * 로 보존한다.
 *
 * 3.
 * 오행별 활성비율은:
 *
 * 투간된 지장간 원시 세력
 * ------------------------
 * 해당 오행 전체 지장간 원시 세력
 *
 * 으로 계산한다.
 */
@Component
public class StemExposureElementPowerAnalyzer {

    private final HiddenElementPowerAnalyzer
            hiddenElementPowerAnalyzer;

    public StemExposureElementPowerAnalyzer(
            HiddenElementPowerAnalyzer hiddenElementPowerAnalyzer
    ) {

        this.hiddenElementPowerAnalyzer =
                Objects.requireNonNull(
                        hiddenElementPowerAnalyzer,
                        "HiddenElementPowerAnalyzer가 필요합니다."
                );
    }

    /**
     * 3-2 결과를 내부에서 생성해서 분석.
     */
    public StemExposureElementPowerAnalysis analyze(
            BasePowerAnalysis basePowerAnalysis
    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        HiddenElementPowerAnalysis hiddenPower =
                hiddenElementPowerAnalyzer.analyze(
                        basePowerAnalysis
                );

        return analyze(
                basePowerAnalysis,
                hiddenPower
        );
    }

    /**
     * 이미 계산된 3-2 결과를 받아
     * 중복 계산 없이 투간 활성도를 계산.
     */
    public StemExposureElementPowerAnalysis analyze(

            BasePowerAnalysis basePowerAnalysis,

            HiddenElementPowerAnalysis hiddenPower

    ) {

        Objects.requireNonNull(
                basePowerAnalysis,
                "기초 세력 분석이 필요합니다."
        );

        Objects.requireNonNull(
                hiddenPower,
                "지장간 오행 세력 분석이 필요합니다."
        );

        /*
         * 동일한 지장간 source를
         * 하나로 합친다.
         *
         * StemExposureAnalyzer는
         *
         * hidden source × visible target
         *
         * 형태의 Evidence를 생성하기 때문에
         * 여기서 source 기준으로 다시 묶는다.
         */
        Map<ExposureSourceKey, ExposureSourceBuilder>
                sourceBuilders =
                new LinkedHashMap<>();

        for (
                StemExposureAnalysis exposureAnalysis
                : basePowerAnalysis
                .hiddenStemExposureAnalyses()
                .values()
        ) {

            if (
                    !exposureAnalysis.exposed()
            ) {

                continue;
            }

            for (
                    StemExposureEvidence evidence
                    : exposureAnalysis.evidences()
            ) {

                FiveElement element =
                        elementOfStem(
                                evidence.stem()
                        );

                ExposureSourceKey key =
                        new ExposureSourceKey(
                                element,
                                evidence.stem(),
                                evidence.sourcePosition(),
                                evidence.sourceBranch(),
                                evidence.hiddenStemRole()
                        );

                ExposureSourceBuilder builder =
                        sourceBuilders.computeIfAbsent(
                                key,
                                unused ->
                                        new ExposureSourceBuilder(
                                                element,
                                                evidence.stem(),
                                                evidence.sourcePosition(),
                                                evidence.sourceBranch(),
                                                evidence.hiddenStemRole(),
                                                evidence.hiddenStemWeight(),
                                                evidence.effectiveSourceWeight()
                                        )
                        );

                builder.addTargetPosition(
                        evidence.targetPosition()
                );
            }
        }

        List<StemExposureElementPowerEvidence> evidences =
                new ArrayList<>();

        for (
                ExposureSourceBuilder builder
                : sourceBuilders.values()
        ) {

            evidences.add(
                    builder.build()
            );
        }

        Map<FiveElement, Integer>
                totalHiddenBaseWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                exposedBaseWeights =
                createZeroElementMap();

        Map<FiveElement, Integer>
                weightedActivationIndexes =
                createZeroElementMap();

        Map<FiveElement, Integer>
                activationRates =
                createZeroElementMap();

        Map<FiveElement, Integer>
                exposedSourceCounts =
                createZeroElementMap();

        Map<FiveElement, Integer>
                monthExposedSourceCounts =
                createZeroElementMap();

        Map<FiveElement, Integer>
                mainQiExposedSourceCounts =
                createZeroElementMap();

        Map<FiveElement, Integer>
                repeatedExposureSourceCounts =
                createZeroElementMap();

        /*
         * 같은 오행의 visible 위치를
         * 중복 없이 계산한다.
         */
        Map<FiveElement, Set<StemExposurePosition>>
                visiblePositions =
                createVisiblePositionMap();

        /*
         * 3-2 전체 지장간 세력을
         * denominator로 사용.
         */
        for (
                FiveElement element
                : FiveElement.values()
        ) {

            totalHiddenBaseWeights.put(
                    element,
                    hiddenPower.baseWeightOf(
                            element
                    )
            );
        }

        for (
                StemExposureElementPowerEvidence evidence
                : evidences
        ) {

            FiveElement element =
                    evidence.element();

            /*
             * 투간된 source의 raw 지장간 세력.
             *
             * source당 한 번만 합산한다.
             */
            exposedBaseWeights.compute(
                    element,
                    (
                            key,
                            current
                    ) ->
                            safeValue(
                                    current
                            )
                                    + evidence.hiddenStemWeight()
            );

            /*
             * 위치 가중까지 포함한
             * 별도의 활성 지수.
             *
             * 최종 오행 세력에 그대로
             * 더하지 않는다.
             */
            weightedActivationIndexes.compute(
                    element,
                    (
                            key,
                            current
                    ) ->
                            safeValue(
                                    current
                            )
                                    + evidence.effectiveSourceWeight()
            );

            exposedSourceCounts.compute(
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
                    evidence.fromMonthBranch()
            ) {

                monthExposedSourceCounts.compute(
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
                    evidence.mainQiExposure()
            ) {

                mainQiExposedSourceCounts.compute(
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
                    evidence.repeatedExposure()
            ) {

                repeatedExposureSourceCounts.compute(
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

            visiblePositions
                    .get(
                            element
                    )
                    .addAll(
                            evidence.targetPositions()
                    );
        }

        Map<FiveElement, Integer>
                visibleStemCounts =
                createZeroElementMap();

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            int totalHidden =
                    totalHiddenBaseWeights
                            .get(
                                    element
                            );

            int exposed =
                    exposedBaseWeights
                            .get(
                                    element
                            );

            int activationRate =
                    calculateActivationRate(
                            exposed,
                            totalHidden
                    );

            activationRates.put(
                    element,
                    activationRate
            );

            visibleStemCounts.put(
                    element,
                    visiblePositions
                            .get(
                                    element
                            )
                            .size()
            );
        }

        return new StemExposureElementPowerAnalysis(
                evidences.size(),
                evidences,
                totalHiddenBaseWeights,
                exposedBaseWeights,
                weightedActivationIndexes,
                activationRates,
                exposedSourceCounts,
                visibleStemCounts,
                monthExposedSourceCounts,
                mainQiExposedSourceCounts,
                repeatedExposureSourceCounts
        );
    }

    /**
     * 활성비율.
     *
     * 투간된 hidden raw power
     * ------------------------
     * 전체 hidden raw power
     *
     * × 100
     *
     * 전체 지장간 세력이 없는 오행은
     * 투간 개념 자체가 성립하지 않으므로 0.
     */
    private int calculateActivationRate(

            int exposedWeight,

            int totalHiddenWeight

    ) {

        if (
                totalHiddenWeight <= 0
        ) {

            return 0;
        }

        double ratio =
                (
                        exposedWeight
                                * 100.0
                )
                        / totalHiddenWeight;

        return Math.max(
                0,
                Math.min(
                        100,
                        (int) Math.round(
                                ratio
                        )
                )
        );
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

    private Map<FiveElement, Set<StemExposurePosition>>
    createVisiblePositionMap() {

        EnumMap<
                FiveElement,
                Set<StemExposurePosition>
                > result =
                new EnumMap<>(
                        FiveElement.class
                );

        for (
                FiveElement element
                : FiveElement.values()
        ) {

            result.put(
                    element,
                    new LinkedHashSet<>()
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
     * 동일 투간 source 식별용 내부 key.
     *
     * target 위치는 key에 넣지 않는다.
     *
     * 그래야 같은 지장간이
     * 여러 천간으로 투출해도
     * source 자체를 한 번만 계산할 수 있다.
     */
    private record ExposureSourceKey(

            FiveElement element,

            HeavenlyStem stem,

            StemExposurePosition sourcePosition,

            EarthlyBranch sourceBranch,

            HiddenStemRole hiddenStemRole

    ) {

        private ExposureSourceKey {

            Objects.requireNonNull(
                    element
            );

            Objects.requireNonNull(
                    stem
            );

            Objects.requireNonNull(
                    sourcePosition
            );

            Objects.requireNonNull(
                    sourceBranch
            );

            Objects.requireNonNull(
                    hiddenStemRole
            );
        }
    }

    /**
     * 하나의 지장간 source가
     * 여러 target 천간으로 투출할 수 있으므로
     * 이를 하나의 Evidence로 합치기 위한 builder.
     */
    private static final class ExposureSourceBuilder {

        private final FiveElement element;

        private final HeavenlyStem hiddenStem;

        private final StemExposurePosition
                sourcePosition;

        private final EarthlyBranch sourceBranch;

        private final HiddenStemRole
                hiddenStemRole;

        private final int hiddenStemWeight;

        private final int effectiveSourceWeight;

        private final Set<StemExposurePosition>
                targetPositions =
                new LinkedHashSet<>();

        private ExposureSourceBuilder(

                FiveElement element,

                HeavenlyStem hiddenStem,

                StemExposurePosition sourcePosition,

                EarthlyBranch sourceBranch,

                HiddenStemRole hiddenStemRole,

                int hiddenStemWeight,

                int effectiveSourceWeight

        ) {

            this.element =
                    Objects.requireNonNull(
                            element
                    );

            this.hiddenStem =
                    Objects.requireNonNull(
                            hiddenStem
                    );

            this.sourcePosition =
                    Objects.requireNonNull(
                            sourcePosition
                    );

            this.sourceBranch =
                    Objects.requireNonNull(
                            sourceBranch
                    );

            this.hiddenStemRole =
                    Objects.requireNonNull(
                            hiddenStemRole
                    );

            this.hiddenStemWeight =
                    hiddenStemWeight;

            this.effectiveSourceWeight =
                    effectiveSourceWeight;
        }

        private void addTargetPosition(
                StemExposurePosition position
        ) {

            targetPositions.add(
                    Objects.requireNonNull(
                            position
                    )
            );
        }

        private StemExposureElementPowerEvidence
        build() {

            return new StemExposureElementPowerEvidence(
                    element,
                    hiddenStem,
                    sourcePosition,
                    sourceBranch,
                    hiddenStemRole,
                    hiddenStemWeight,
                    effectiveSourceWeight,
                    List.copyOf(
                            targetPositions
                    )
            );
        }
    }
}